package com.dinotom.project_koff_ma;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.dinotom.project_koff_ma.pojo.RegistrationResult;
import com.dinotom.project_koff_ma.pojo.TokenDetail;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity
{
    private AwesomeValidation awesomeValidation;
    private EditText editUsername, editPassword, editRepeatPassword, editFirstName, editLastName, editEmail;
    private ProgressBar progressBar;

    private void initializeForm()
    {
        editUsername = findViewById(R.id.username_input);
        editPassword = findViewById(R.id.password_input);
        editRepeatPassword = findViewById(R.id.password_repeat_input);
        editFirstName = findViewById(R.id.first_name_input);
        editLastName = findViewById(R.id.last_name_input);
        editEmail = findViewById(R.id.email_input);
    }

    private void initializeFormValidation()
    {
        awesomeValidation.addValidation(this,
                                        R.id.username_input,
                                        "^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$",
                                        R.string.registration_username_error);
        awesomeValidation.addValidation(this,
                R.id.password_input,
                "^[A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*$",
                R.string.registration_password_error);
        awesomeValidation.addValidation(this,
                R.id.password_repeat_input,
                R.id.password_input,
                R.string.registration_passwords_not_equal_error);
        awesomeValidation.addValidation(this,
                R.id.first_name_input,
                "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$",
                R.string.registration_first_name_error);
        awesomeValidation.addValidation(this,
                R.id.last_name_input,
                "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$",
                R.string.registration_last_name_error);
        awesomeValidation.addValidation(this,
                R.id.email_input,
                Patterns.EMAIL_ADDRESS,
                R.string.registration_email_error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        overridePendingTransition(R.anim.enter_activity_1, R.anim.enter_activity_2);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        initializeForm();
        initializeFormValidation();

        Button registerButton = findViewById(R.id.registration_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerOnClick();
            }
        });

        Button resetButton = findViewById(R.id.registration_clear_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerClearOnClick();
            }
        });

        progressBar = findViewById(R.id.registration_progress_bar);

        Toolbar registrationToolbar = (Toolbar) findViewById(R.id.registration_appbar);
        registrationToolbar.setTitle(getBaseContext().getResources().getString(R.string.registration_appbar_title));

        setSupportActionBar(registrationToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void registerOnClick()
    {
        if (!awesomeValidation.validate()) return;

        progressBar.setVisibility(View.VISIBLE);

        APIInterface apiInterface = APIClient.getClientWithoutDefaultHeaders().create(APIInterface.class);

        Call<RegistrationResult> registrationResultCall = apiInterface.registerUser(
                editUsername.getText().toString(),
                editPassword.getText().toString(),
                editEmail.getText().toString(),
                editFirstName.getText().toString(),
                editLastName.getText().toString()
        );
        registrationResultCall.enqueue(new Callback<RegistrationResult>()
        {
            @Override
            public void onResponse(Call<RegistrationResult> call, Response<RegistrationResult> response)
            {
                RegistrationResult registrationResult = response.body();
                UserUtilities.setNewUserToken(registrationResult.getAuthToken());

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onFailure(Call<RegistrationResult> call, Throwable t)
            {
                progressBar.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }

    private void registerClearOnClick()
    {
        editUsername.setText("");
        editPassword.setText("");
        editRepeatPassword.setText("");
        editEmail.setText("");
        editFirstName.setText("");
        editLastName.setText("");
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.exit_activity_1, R.anim.exit_activity_2);
    }
}

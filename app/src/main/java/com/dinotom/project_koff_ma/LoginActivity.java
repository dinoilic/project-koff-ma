package com.dinotom.project_koff_ma;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dinotom.project_koff_ma.business_entities.BusinessEntitiesActivity;
import com.squareup.otto.Subscribe;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressBar progressBar;

    boolean ottoRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        KoffGlobal.bus.register(this); // register Otto bus for event observing
        ottoRegistered = true;

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               loginOnClick();
            }
        });

        EditText lastEditText = findViewById(R.id.password_input);
        lastEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                Log.d(TAG, String.format("actionId: %s", actionId));

                if (actionId == 6)
                {
                    loginOnClick();
                    return true;
                }
                return false;
            }
        });

        progressBar = findViewById(R.id.login_progress);

        Toolbar businessEntitiesListToolbar = (Toolbar) findViewById(R.id.login_appbar);
        businessEntitiesListToolbar.setTitle(getBaseContext().getResources().getString(R.string.login_appbar_title));

        setSupportActionBar(businessEntitiesListToolbar);
    }

    private void loginOnClick()
    {
        String username = ((EditText)findViewById(R.id.username_input)).getText().toString();
        String password = ((EditText)findViewById(R.id.password_input)).getText().toString();

        String warningForEmpty = getBaseContext().getResources().getString(R.string.login_empty_warning);

        if(username.isEmpty() || password.isEmpty())
            Toast.makeText(getApplicationContext(), warningForEmpty, Toast.LENGTH_SHORT).show();

        UserUtilities.fetchNewToken(username, password);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void ottoLoginActivitySubscriber(String event)
    {
        if(event.equals(getBaseContext().getResources().getString(R.string.login_successful_event)))
        {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else if(event.equals(getBaseContext().getResources().getString(R.string.login_failure_event)))
        {
            String loginFailureInfo = getBaseContext().getResources().getString(R.string.login_failure_info);
            Toast.makeText(getApplicationContext(),loginFailureInfo , Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(!ottoRegistered) KoffGlobal.bus.register(this);
        ottoRegistered = true;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(ottoRegistered) KoffGlobal.bus.unregister(this);
        ottoRegistered = false;
    }

    @Override
    public void onBackPressed() { }
}


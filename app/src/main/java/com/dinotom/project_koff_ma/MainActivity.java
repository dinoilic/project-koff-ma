package com.dinotom.project_koff_ma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.dinotom.project_koff_ma.pojo.UserToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mainText = findViewById(R.id.mainText);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<UserToken> call1 = apiInterface.getUserToken("superuser", "nevionevio");

        call1.enqueue(new Callback<UserToken>()
        {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response)
            {
                UserToken token1 = response.body();
                Toast.makeText(getApplicationContext(), token1.getToken(), Toast.LENGTH_LONG).show();
                //mainText.setText(token1.getToken());
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Nije uspjesno!", Toast.LENGTH_LONG).show();
                //mainText.setText("Nije uspjelo!");
            }
        });
    }
}

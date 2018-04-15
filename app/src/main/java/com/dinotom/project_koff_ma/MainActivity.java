package com.dinotom.project_koff_ma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dinotom.project_koff_ma.pojo.UserToken;
import com.dinotom.project_koff_ma.pojo.category.Category;
import com.dinotom.project_koff_ma.UserUtilities;

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
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        String currentUserToken = UserUtilities.getCurrentUserToken(getApplicationContext());
        if(currentUserToken == null || currentUserToken.isEmpty()) // token is not stored in SharedPreferences
        {
            apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<UserToken> userTokenCall = apiInterface.getUserToken("superuser", "superuser"); // add login procedure here
            userTokenCall.enqueue(new Callback<UserToken>() {
                @Override
                public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                    UserToken token = response.body();
                    UserUtilities.setNewUserToken(getApplicationContext(), token.getToken());
                    //createCategoryGridView(token.getToken());
                }

                @Override
                public void onFailure(Call<UserToken> call, Throwable t) {
                    call.cancel();
                    Toast.makeText(getApplicationContext(), "User token fetching unsuccessful!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else // token is stored in SharedPreferences
        {
            Toast.makeText(getApplicationContext(), "Token is stored: " + currentUserToken, Toast.LENGTH_SHORT).show();
        }
    }

    private void createCategoryGridView(final String token)
    {
        Call<Category> categoryCall = apiInterface.getMainCategories("Token " + token);

        categoryCall.enqueue(new Callback<Category>()
        {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response)
            {
                Category mainCategories = response.body(); //dobiven Category POJO
                Toast.makeText(getApplicationContext(), mainCategories.results.get(0).name, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Nije uspjesno dohvacanje kategorija!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.dinotom.project_koff_ma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dinotom.project_koff_ma.pojo.category.Category;
import com.squareup.otto.Subscribe;

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

        apiInterface = APIClient.getClient().create(APIInterface.class); // initialize default REST interface, with default authorization headers
        KoffGlobal.bus.register(this); // register Otto bus for event observing

        String currentUserToken = UserUtilities.getCurrentUserToken(); // get current User Auth Token from shared preferences
        if(currentUserToken == null || currentUserToken.isEmpty())
            UserUtilities.fetchNewToken();
        else
            UserUtilities.checkTokenValidity(currentUserToken); // checks validity of the current token; if invalid, fetches new token
    }

    @Subscribe
    public void initializeActivity(String event)
    {
        if(event.equals(KoffGlobal.getAppContext().getResources().getString(R.string.main_activity_event))) // if the event is intended for this activity
        {
            createCategoryGridView();
        }
    }

    private void createCategoryGridView()
    {
        Call<Category> categoryCall = apiInterface.getMainCategories();
        categoryCall.enqueue(new Callback<Category>()
        {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response)
            {
                Category mainCategories = response.body(); // Category pojo is fetched
                Toast.makeText(getApplicationContext(), mainCategories.results.get(0).name, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Category fetching unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

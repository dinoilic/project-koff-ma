package com.dinotom.project_koff_ma;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.dinotom.project_koff_ma.business_entities.BusinessEntitiesActivity;
import com.dinotom.project_koff_ma.business_entities.BusinessEntitiesUtilities;
import com.dinotom.project_koff_ma.business_entities.BusinessEntityInfoActivity;
import com.dinotom.project_koff_ma.business_entities.IBusinessEntitiesView;
import com.dinotom.project_koff_ma.pojo.category.Category;
import com.dinotom.project_koff_ma.pojo.category.Result;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    public static final String MAIN_CATEGORY_PK = "com.dinotom.project_koff_ma.MAIN_CATEGORY_PK";
    public static final String MAIN_CATEGORY_NAME = "com.dinotom.project_koff_ma.MAIN_CATEGORY_NAME";
    public static final String MAIN_CATEGORY_CHILDREN = "com.dinotom.project_koff_ma.MAIN_CATEGORY_CHILDREN";

    private static final int KOFF_COARSE_LOCATION = 1;

    APIInterface apiInterface;
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;

    List<Result> categoryList;

    boolean ottoRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.enter_activity_1, R.anim.enter_activity_2);

        recyclerView = (RecyclerView) findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new GridLayoutManager(KoffGlobal.getAppContext(), 3));

        BusinessEntitiesActivity.resetSortAndFilterData();

        // Request Location Permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        KOFF_COARSE_LOCATION);
        }

        Toolbar businessEntitiesListToolbar = (Toolbar) findViewById(R.id.mainactivity_appbar);
        setSupportActionBar(businessEntitiesListToolbar);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        apiInterface = APIClient.getClient().create(APIInterface.class); // initialize default REST interface, with default authorization headers
        KoffGlobal.bus.register(this); // register Otto bus for event observing
        ottoRegistered = true;

        String currentUserToken = UserUtilities.getCurrentUserToken(); // get current User Auth Token from shared preferences
        if(currentUserToken == null || currentUserToken.isEmpty())
            UserUtilities.fetchNewToken();
        else
            UserUtilities.checkTokenValidity(currentUserToken); // checks validity of the current token; if invalid, fetches new token
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

                categoryList = new ArrayList<Result>();
                for (Result category:
                     mainCategories.getResults()) {
                    categoryList.add(category);
                }

                //categoryList = mainCategories.getResults();

                categoryAdapter = new CategoryAdapter(KoffGlobal.getAppContext(), categoryList);
                recyclerView.setAdapter(categoryAdapter);
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Category fetching unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        final SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String searchTerm)
            {
                Intent intent = new Intent(getBaseContext(), BusinessEntitiesActivity.class);
                intent.putExtra("SEARCH_TERM_FROM_OUTSIDE", searchTerm);
                getBaseContext().startActivity(intent);

                searchView.setIconified(true);
                searchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}

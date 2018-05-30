package com.dinotom.project_koff_ma.business_entities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.dinotom.project_koff_ma.APIClient;
import com.dinotom.project_koff_ma.APIInterface;
import com.dinotom.project_koff_ma.CategoryAdapter;
import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntity;
import com.dinotom.project_koff_ma.pojo.business_entities.DayWorkingHours;
import com.dinotom.project_koff_ma.pojo.category.Category;
import com.dinotom.project_koff_ma.pojo.category.Result;
import com.dinotom.project_koff_ma.pojo.search.SearchPage;
import com.dinotom.project_koff_ma.pojo.search.SearchResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;


public class BusinessEntitiesActivity extends AppCompatActivity implements IBusinessEntitiesView
{
    private static final String TAG = BusinessEntitiesActivity.class.getSimpleName();

    RecyclerView recyclerView;

    BusinessEntitiesAdapter businessEntitiesAdapter;
    BusinessEntitiesPresenter businessEntitiesPresenter;

    Paginate paginate;

    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    //FusedLocationProviderClient mFusedLocationClient;

    APIInterface apiInterface;
    ArrayList<Integer> searchResultList;

    Integer subcategoryPk;

    private void getSearchResults(String searchQuery, final IBusinessEntitiesView view) {
        Call<SearchPage> categoryCall = apiInterface.getSearchPage(searchQuery);
        categoryCall.enqueue(new Callback<SearchPage>()
        {
            @Override
            public void onResponse(Call<SearchPage> call, Response<SearchPage> response)
            {
                SearchPage mainSearchPage = response.body();
                String searchIds = "";

                for(int i = 0; i < mainSearchPage.getResults().size(); ++i)
                {
                    if(i == 0)
                        searchIds = searchIds.concat(mainSearchPage.getResults().get(0).getId().toString());
                    else
                    {
                        searchIds = searchIds.concat(String.format(",%s", mainSearchPage.getResults().get(i).getId().toString()));
                    }
                }

                Log.d(TAG, String.format("searchIds: %s", searchIds));

                Context context = KoffGlobal.getAppContext();
                String fieldName = context.getResources().getString(R.string.business_activities_search_ids);
                String sharedPreferencesFile = KoffGlobal.getAppContext().getResources().getString(R.string.temporary_file);

                SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(fieldName, searchIds);
                editor.commit(); // use "apply" if we want asynchronous later
                recreate();
            }

            @Override
            public void onFailure(Call<SearchPage> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ne radi search!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView)item.getActionView();
        final IBusinessEntitiesView view = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                getSearchResults(s, view);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        setContentView(R.layout.activity_businessentitites);

        overridePendingTransition(R.anim.enter_activity_1, R.anim.enter_activity_2);

        recyclerView = (RecyclerView) findViewById(R.id.rv_businessentities);
        recyclerView.setLayoutManager(new LinearLayoutManager(KoffGlobal.getAppContext()));

        businessEntitiesAdapter = new BusinessEntitiesAdapter(KoffGlobal.getAppContext());
        recyclerView.setAdapter(businessEntitiesAdapter);

        Intent intent = getIntent();
        subcategoryPk = intent.getIntExtra("SUBCATEGORY_PK", 0);
        String subcategoryName = intent.getStringExtra("SUBCATEGORY_NAME");

        String preferenceFileName = getBaseContext().getResources().getString(R.string.temporary_file);
        String searchIdsKey = getBaseContext().getResources().getString(R.string.business_activities_search_ids);
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
        String searchIds = sharedPref.getString(searchIdsKey, "");

        Log.d(TAG, String.format("searchIds: %s", searchIds));

        if(searchIds.isEmpty())
            businessEntitiesPresenter = new BusinessEntitiesPresenter(this, subcategoryPk, null);
        else
        {
            businessEntitiesPresenter = new BusinessEntitiesPresenter(this, subcategoryPk, searchIds);
            SharedPreferences.Editor editor = sharedPref.edit();

            // STAVITI U ON ACTIVITY EXIT ILI TAKO NEŠTO
            editor.putString(searchIdsKey, "");
            editor.commit();
        }

        paginate = new PaginateBuilder()
                .with(recyclerView)
                .setOnLoadMoreListener(businessEntitiesPresenter)
                .setLoadingTriggerThreshold(5) // malo se igrati s ovime
                .build();

        Button sortButton = (Button) findViewById(R.id.sort_button);
        sortButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showSortDialog();
            }
        });

        Button filterButton = (Button) findViewById(R.id.filter_button);
        filterButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startFilterSettingsActivity();
            }
        });

        preferences = BusinessEntitiesUtilities.getSharedPrefs();
        listener =
                new SharedPreferences.OnSharedPreferenceChangeListener()
                {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
                    {
                        if(prefs.equals(preferences))
                            recreate();
                    }
                };
        preferences.registerOnSharedPreferenceChangeListener(listener);

        BusinessEntitiesUtilities.getLastLocation(getBaseContext(), this);

       /* Toolbar myAppBar = (Toolbar) findViewById(R.id.businessentity_appbar);
        setSupportActionBar(myAppBar);

        getSupportActionBar().setTitle(subcategoryName);*/


    }

    private void showSortDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = BusinessEntitiesUtilities.getStringFromStringResources(R.string.businessentity_sort_dialog_title);

        CharSequence[] currentSortModeNames = BusinessEntitiesUtilities.SortModeNames.clone();
        final BusinessEntitiesUtilities.SortMode[] sortModeEnumValues = BusinessEntitiesUtilities.SortMode.values();

        for(int i = 0; i < sortModeEnumValues.length; ++i)
        {
            if(sortModeEnumValues[i].toString().equals(BusinessEntitiesUtilities.getSortMode()))
            {
                SpannableString selectedSort = new SpannableString(currentSortModeNames[i]);
                selectedSort.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, currentSortModeNames[i].length(),0);
                currentSortModeNames[i] = selectedSort;
            }
        }

        builder.setTitle(title);
        builder.setItems(currentSortModeNames, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                BusinessEntitiesUtilities.setStringSetting(
                        R.string.business_activities_sort_mode,
                        sortModeEnumValues[which].toString()
                );
                recreate();
            }
        });
        builder.show();
    }

    private void startFilterSettingsActivity()
    {
        Intent intent = new Intent(getBaseContext(), FilterSettingsActivity.class);
        getBaseContext().startActivity(intent);
    }

    public static void resetSortAndFilterData()
    {
        BusinessEntitiesUtilities.setStringSetting(
                R.string.business_activities_sort_mode,
                BusinessEntitiesUtilities.SortMode.DISTANCE.toString()
        );
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.exit_activity_1, R.anim.exit_activity_2);
    }

    @Override
    public void addItems(List<BusinessEntity> items)
    {
        businessEntitiesAdapter.addItems(items);
    }

    @Override
    public int getItemsNum()
    {
        return businessEntitiesAdapter.getItemCount();
    }

    @Override
    public void showPaginateLoading(boolean isPaginateLoading) {
        paginate.showLoading(isPaginateLoading);
    }

    @Override
    public void showPaginateError(boolean isPaginateError) {
        paginate.showError(isPaginateError);
    }

    @Override
    public void setPaginateNoMoreData(boolean isNoMoreItems) {
        paginate.setNoMoreItems(isNoMoreItems);
    }

    @Override
    public void onDestroy() {
        paginate.unbind();
        super.onDestroy();
    }
}

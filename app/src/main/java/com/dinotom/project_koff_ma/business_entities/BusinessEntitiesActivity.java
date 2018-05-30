package com.dinotom.project_koff_ma.business_entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinotom.project_koff_ma.APIClient;
import com.dinotom.project_koff_ma.APIInterface;
import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntity;
import com.dinotom.project_koff_ma.pojo.search.SearchPage;

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

    APIInterface apiInterface;

    Integer subcategoryPk;

    private void getSearchResults(String searchQuery)
    {
        Call<SearchPage> categoryCall = apiInterface.getSearchPage(searchQuery);
        categoryCall.enqueue(new Callback<SearchPage>()
        {
            @Override
            public void onResponse(Call<SearchPage> call, Response<SearchPage> response)
            {
                SearchPage mainSearchPage = response.body();
                String searchIds = mainSearchPage.getResults().size() == 0 ? "NO_RESULTS" : "";

                for(int i = 0; i < mainSearchPage.getResults().size(); ++i)
                {
                    if(i == 0)
                        searchIds = searchIds.concat(mainSearchPage.getResults().get(0).getId().toString());
                    else
                        searchIds = searchIds.concat(String.format(",%s", mainSearchPage.getResults().get(i).getId().toString()));
                }

                Log.d(TAG, String.format("searchIds: %s", searchIds));

                BusinessEntitiesUtilities.setStringSetting(
                        R.string.business_activities_search_ids,
                        searchIds,
                        R.string.temporary_file
                );
                recreate();
            }

            @Override
            public void onFailure(Call<SearchPage> call, Throwable t)
            {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Search unsuccessful!", Toast.LENGTH_SHORT).show();
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

        String searchTerm = BusinessEntitiesUtilities.getSearchTerm();

        if(!searchTerm.isEmpty())
        {
            searchView.setIconified(false);
            searchView.clearFocus();
            searchView.setQuery(searchTerm, false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String searchTerm)
            {
                BusinessEntitiesUtilities.setStringSetting(
                        R.string.business_activities_search_term,
                        searchTerm,
                        R.string.temporary_file
                );
                getSearchResults(searchTerm);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) { return false; }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        apiInterface = APIClient.getClient().create(APIInterface.class);
        overridePendingTransition(R.anim.enter_activity_1, R.anim.enter_activity_2);
        setContentView(R.layout.activity_businessentitites);

        String searchIds = BusinessEntitiesUtilities.getIds();

        String searchTermFromOutside = intent.getStringExtra("SEARCH_TERM_FROM_OUTSIDE");
        if(searchTermFromOutside != null && searchIds.isEmpty())
        {
            BusinessEntitiesUtilities.setStringSetting(
                    R.string.business_activities_search_term,
                    searchTermFromOutside,
                    R.string.temporary_file
            );

            getSearchResults(searchTermFromOutside);
            return;
        }

        recyclerView = (RecyclerView) findViewById(R.id.rv_businessentities);
        recyclerView.setLayoutManager(new LinearLayoutManager(KoffGlobal.getAppContext()));

        businessEntitiesAdapter = new BusinessEntitiesAdapter(KoffGlobal.getAppContext());
        recyclerView.setAdapter(businessEntitiesAdapter);

        subcategoryPk = intent.getIntExtra("SUBCATEGORY_PK", -1);
        String subcategoryName = intent.getStringExtra("SUBCATEGORY_NAME");

        Log.d(TAG, String.format("searchIds: %s", searchIds));

        if(searchIds.equals("NO_RESULTS"))
            businessEntitiesPresenter = null;
        else if(searchIds.isEmpty())
            businessEntitiesPresenter = new BusinessEntitiesPresenter(this, subcategoryPk, null);
        else
            businessEntitiesPresenter = new BusinessEntitiesPresenter(this, subcategoryPk, searchIds);

        if(businessEntitiesPresenter != null)
        {
            TextView noResultsTextView = findViewById(R.id.no_results_textview);
            noResultsTextView.setVisibility(View.GONE);

            paginate = new PaginateBuilder()
                    .with(recyclerView)
                    .setOnLoadMoreListener(businessEntitiesPresenter)
                    .setLoadingTriggerThreshold(5) // malo se igrati s ovime
                    .build();
        }

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

        preferences = BusinessEntitiesUtilities.getSharedPrefs(R.string.business_activities_file);
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

        Toolbar businessEntitiesListToolbar = (Toolbar) findViewById(R.id.business_entities_list_appbar);

        if(subcategoryName != null)
            businessEntitiesListToolbar.setTitle(subcategoryName);
        else
            businessEntitiesListToolbar.setTitle("Rezultati");

        setSupportActionBar(businessEntitiesListToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void showSortDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = BusinessEntitiesUtilities.getFromStringResources(R.string.businessentity_sort_dialog_title);

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
                        sortModeEnumValues[which].toString(),
                        R.string.business_activities_file
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
                BusinessEntitiesUtilities.SortMode.DISTANCE.toString(),
                R.string.business_activities_file
        );
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

        // Reset search results
        BusinessEntitiesUtilities.setStringSetting(
                R.string.business_activities_search_ids,
                "",
                R.string.temporary_file
        );

        // Reset search term
        BusinessEntitiesUtilities.setStringSetting(
                R.string.business_activities_search_term,
                "",
                R.string.temporary_file
        );
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
    public void onDestroy()
    {
        if(paginate != null)
            paginate.unbind();

        super.onDestroy();
    }
}

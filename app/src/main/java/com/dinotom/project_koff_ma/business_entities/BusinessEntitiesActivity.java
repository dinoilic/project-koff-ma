package com.dinotom.project_koff_ma.business_entities;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;

import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntity;
import com.dinotom.project_koff_ma.pojo.business_entities.DayWorkingHours;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businessentitites);

        overridePendingTransition(R.anim.enter_activity_1, R.anim.enter_activity_2);

        recyclerView = (RecyclerView) findViewById(R.id.rv_businessentities);
        recyclerView.setLayoutManager(new LinearLayoutManager(KoffGlobal.getAppContext()));

        businessEntitiesAdapter = new BusinessEntitiesAdapter();
        recyclerView.setAdapter(businessEntitiesAdapter);

        Intent intent = getIntent();
        Integer subcategoryPk = intent.getIntExtra("SUBCATEGORY_PK", 0);
        String subcategoryName = intent.getStringExtra("SUBCATEGORY_NAME");

        businessEntitiesPresenter = new BusinessEntitiesPresenter(this, subcategoryPk);

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

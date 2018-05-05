package com.dinotom.project_koff_ma.business_entities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntity;

import java.util.List;

import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;


public class BusinessEntitiesActivity extends AppCompatActivity implements IBusinessEntitiesView
{
    RecyclerView recyclerView;

    BusinessEntitiesAdapter businessEntitiesAdapter;
    BusinessEntitiesPresenter businessEntitiesPresenter;

    Paginate paginate;

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

       /* Toolbar myAppBar = (Toolbar) findViewById(R.id.businessentity_appbar);
        setSupportActionBar(myAppBar);

        getSupportActionBar().setTitle(subcategoryName);*/
    }

    private void showSortDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = BusinessEntitiesUtilities.getStringFromStringResources(R.string.businessentity_sort_dialog_title);
        builder.setTitle(title);
        builder.setItems(BusinessEntitiesUtilities.SortModeNames, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                BusinessEntitiesUtilities.setStringSetting(
                        R.string.business_activities_sort_mode,
                        BusinessEntitiesUtilities.SortMode.values()[which].toString()
                );
                recreate();
            }
        });
        builder.show();
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

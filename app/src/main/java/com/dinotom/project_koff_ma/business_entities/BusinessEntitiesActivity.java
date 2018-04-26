package com.dinotom.project_koff_ma.business_entities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.MainActivity;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.SubcategoryAdapter;
import com.dinotom.project_koff_ma.pojo.business_entities.BusinessEntity;
import com.dinotom.project_koff_ma.pojo.category.Child;

import java.util.ArrayList;
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

        businessEntitiesPresenter = new BusinessEntitiesPresenter(this, subcategoryPk);

        paginate = new PaginateBuilder()
                .with(recyclerView)
                .setOnLoadMoreListener(businessEntitiesPresenter)
                .setLoadingTriggerThreshold(5)
                .build();
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

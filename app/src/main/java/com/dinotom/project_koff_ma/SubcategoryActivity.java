package com.dinotom.project_koff_ma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dinotom.project_koff_ma.pojo.category.Child;

import java.util.ArrayList;

public class SubcategoryActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    SubcategoryAdapter subcategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);

        overridePendingTransition(R.anim.enter_activity_1, R.anim.enter_activity_2);

        recyclerView = (RecyclerView) findViewById(R.id.rv_subcategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(KoffGlobal.getAppContext()));

        Intent intent = getIntent();
        String categoryName = intent.getStringExtra(MainActivity.MAIN_CATEGORY_NAME);
        ArrayList<Child> categoryChildren = (ArrayList<Child>)intent.getSerializableExtra(MainActivity.MAIN_CATEGORY_CHILDREN);

        subcategoryAdapter = new SubcategoryAdapter(getApplicationContext(), categoryChildren);
        recyclerView.setAdapter(subcategoryAdapter);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.exit_activity_1, R.anim.exit_activity_2);
    }
}

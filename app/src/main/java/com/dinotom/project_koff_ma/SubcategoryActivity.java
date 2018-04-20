package com.dinotom.project_koff_ma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dinotom.project_koff_ma.pojo.category.Child;

import java.util.ArrayList;

public class SubcategoryActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String categoryName = intent.getStringExtra(MainActivity.MAIN_CATEGORY_NAME);
        ArrayList<Child> categoryChildren = (ArrayList<Child>)intent.getSerializableExtra(MainActivity.MAIN_CATEGORY_CHILDREN);

        Toast.makeText(getApplicationContext(), categoryChildren.get(0).getName(), Toast.LENGTH_LONG).show();
    }
}

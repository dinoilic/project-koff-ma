package com.dinotom.project_koff_ma.business_entities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dinotom.project_koff_ma.R;

public class BusinessEntityInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.businessentity_info);

        Intent intent = getIntent();
        int entityPk = intent.getIntExtra("ENTITY_PK", -1);

        System.out.println(entityPk);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

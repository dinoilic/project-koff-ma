package com.dinotom.project_koff_ma.business_entities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dinotom.project_koff_ma.R;

public class FilterLocationSettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_location_settings_activity);

        Toolbar filterActivityToolbar = (Toolbar) findViewById(R.id.filter_location_activity_appbar);

        filterActivityToolbar.setTitle("Lokacija");

        setSupportActionBar(filterActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
    }
}

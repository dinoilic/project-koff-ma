package com.dinotom.project_koff_ma.business_entities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FilterSettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new FilterSettingsFragment())
                .commit();
    }
}

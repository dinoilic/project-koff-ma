package com.dinotom.project_koff_ma.business_entities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;

public class FilterSettingsFragment extends PreferenceFragment
{
    private static final String TAG = FilterSettingsFragment.class.getSimpleName();
    private SeekBarPreference _seekBarPref;

    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Define the settings file to use by this settings fragment
        String sharedPreferencesFile = KoffGlobal.getAppContext().getResources().getString(R.string.business_activities_file);
        getPreferenceManager().setSharedPreferencesName(sharedPreferencesFile);

        addPreferencesFromResource(R.xml.filter_preferences);

        String seekbarKey = BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_radius);

        // Get widgets :
        _seekBarPref = (SeekBarPreference) this.findPreference(seekbarKey);

        // Set listener :
        preferences = BusinessEntitiesUtilities.getSharedPrefs(R.string.business_activities_file);
        listener =
                new SharedPreferences.OnSharedPreferenceChangeListener()
                {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
                    {
                        Log.d(TAG, " onSharedPreferenceChanged called.");
                        refreshLocationSummary();
                        refreshRadiusSummary();
                    }
                };
        preferences.registerOnSharedPreferenceChangeListener(listener);

        refreshLocationSummary();
        refreshRadiusSummary();
    }

    private void refreshRadiusSummary()
    {
        String seekbarKey = BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_radius);

        // Set seekbar summary :
        int radius = preferences.getInt(seekbarKey, 50);
        String unit = BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_radius_unit);
        _seekBarPref.setSummary(String.format("%d %s", radius, unit));
    }

    private void refreshLocationSummary()
    {
        String locationKey = BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_location);

        //_locationScreen.setSummary();
    }
}

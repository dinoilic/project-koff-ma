package com.dinotom.project_koff_ma.business_entities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;

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

        String seekbarKey = BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_radius);

        // Get widgets :
        _seekBarPref = (SeekBarPreference) this.findPreference(seekbarKey);

        // Set listener :
        preferences = BusinessEntitiesUtilities.getSharedPrefs();
        listener =
                new SharedPreferences.OnSharedPreferenceChangeListener()
                {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
                    {
                        Log.d(TAG, " onSharedPreferenceChanged called.");
                        refreshSummary();
                    }
                };
        preferences.registerOnSharedPreferenceChangeListener(listener);

        // Set seekbar summary :
        int radius = preferences.getInt(seekbarKey, 50);
        String summary = BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_radius_summary);
        _seekBarPref.setSummary(summary.replace("$1", ""+radius));
    }

    private void refreshSummary()
    {
        String seekbarKey = BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_radius);

        // Set seekbar summary :
        int radius = preferences.getInt(seekbarKey, 50);
        String summary = BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_radius_summary);
        _seekBarPref.setSummary(summary.replace("$1", ""+radius));
    }
}

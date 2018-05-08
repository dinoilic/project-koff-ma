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
    private PreferenceScreen _locationScreen;

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
        String locationKey = BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_location);

        // Get widgets :
        _seekBarPref = (SeekBarPreference) this.findPreference(seekbarKey);
        _locationScreen = (PreferenceScreen) this.findPreference(locationKey);

        // Set listener :
        preferences = BusinessEntitiesUtilities.getSharedPrefs();
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

        _locationScreen= (PreferenceScreen) this.findPreference(
                BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_location));

        Preference chooseLocation = _locationScreen.findPreference(
                BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_location_choose));

        chooseLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int PLACE_PICKER_REQUEST = 1234;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        Preference currentLocation = _locationScreen.findPreference(
                BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_location_current));

        currentLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                BusinessEntitiesUtilities.setBoolSetting(
                        R.string.business_activities_filter_location_is_custom, false);

                BusinessEntitiesUtilities.getLastLocation(KoffGlobal.getAppContext(), getActivity());

                PreferenceScreen ps = (PreferenceScreen)findPreference(
                        BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_location));
                ps.getDialog().dismiss();

                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234)
        {
            if (resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, KoffGlobal.getAppContext());
                BusinessEntitiesUtilities.setBoolSetting(
                        R.string.business_activities_filter_location_is_custom, true);
                String loc = String.format("%f,%f", place.getLatLng().latitude, place.getLatLng().longitude);
                Log.d(TAG, " Custom location" + loc);
                BusinessEntitiesUtilities.setStringSetting(R.string.business_activities_filter_location, loc);

                PreferenceScreen ps = (PreferenceScreen)findPreference(
                        BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_location));
                ps.getDialog().dismiss();
            }
            Log.d(TAG, " Result code" + resultCode);
        }
    }

    private void refreshRadiusSummary()
    {
        String seekbarKey = BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_radius);

        // Set seekbar summary :
        int radius = preferences.getInt(seekbarKey, 50);
        String unit = BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_radius_unit);
        _seekBarPref.setSummary(String.format("%d %s", radius, unit));
    }

    private void refreshLocationSummary()
    {
        String locationKey = BusinessEntitiesUtilities.getStringFromStringResources(R.string.business_activities_filter_location);

        //_locationScreen.setSummary();
    }
}

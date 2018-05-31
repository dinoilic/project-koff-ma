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

public class FilterLocationSettingsFragment extends PreferenceFragment
{
    private static final String TAG = FilterLocationSettingsFragment.class.getSimpleName();

    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String sharedPreferencesFile = KoffGlobal.getAppContext().getResources().getString(R.string.business_activities_file);
        getPreferenceManager().setSharedPreferencesName(sharedPreferencesFile);

        addPreferencesFromResource(R.xml.filter_location_preferences);

        Preference chooseLocation = this.findPreference(
                BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_location_choose));

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

        Preference currentLocation = this.findPreference(
                BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_location_current));

        currentLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                BusinessEntitiesUtilities.setBoolSetting(
                        R.string.business_activities_filter_location_is_custom,
                        false,
                        R.string.business_activities_file
                );

                BusinessEntitiesUtilities.getLastLocation(KoffGlobal.getAppContext(), getActivity());

                getActivity().onBackPressed();

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
                        R.string.business_activities_filter_location_is_custom,
                        true,
                        R.string.business_activities_file
                );
                String loc = String.format("%f,%f", place.getLatLng().latitude, place.getLatLng().longitude);
                Log.d(TAG, " Custom location" + loc);
                BusinessEntitiesUtilities.setStringSetting(
                        R.string.business_activities_filter_location,
                        loc,
                        R.string.business_activities_file
                );

                getActivity().onBackPressed();
            }
            Log.d(TAG, " Result code" + resultCode);
        }
    }
}

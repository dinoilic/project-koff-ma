package com.dinotom.project_koff_ma.business_entities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
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

    private AddressResultReceiver mResultReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Define the settings file to use by this settings fragment
        String sharedPreferencesFile = KoffGlobal.getAppContext().getResources().getString(R.string.business_activities_file);
        getPreferenceManager().setSharedPreferencesName(sharedPreferencesFile);

        addPreferencesFromResource(R.xml.filter_preferences);

        String seekbarKey = BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_radius);
        String locationKey = BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_location);

        // Get widgets :
        _seekBarPref = (SeekBarPreference) this.findPreference(seekbarKey);
        _locationScreen = (PreferenceScreen) this.findPreference(locationKey);

        // Set listener :
        preferences = BusinessEntitiesUtilities.getSharedPrefs(R.string.business_activities_file);
        listener =
                new SharedPreferences.OnSharedPreferenceChangeListener()
                {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
                    {
                        Log.d(TAG, " onSharedPreferenceChanged called.");
                        Log.d(TAG, String.format("Key is %s", key));
                        if(key.equals(BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_location)))
                            refreshLocationSummary();
                        else if(key.equals(BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_radius)))
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
        int radius = preferences.getInt(seekbarKey, 10);
        String unit = BusinessEntitiesUtilities.getFromStringResources(R.string.business_activities_filter_radius_unit);
        _seekBarPref.setSummary(String.format("%d %s", radius, unit));
    }

    private void refreshLocationSummary()
    {
        String location = BusinessEntitiesUtilities.getLocation();

        if(!Geocoder.isPresent())
            return;

        startIntentService(location);
    }

    protected void startIntentService(String location)
    {
        if(mResultReceiver == null)
            mResultReceiver = new AddressResultReceiver(new Handler());

        if(getActivity() != null) {
            Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
            intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
            intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, location);
            getActivity().startService(intent);
        }
    }

    class AddressResultReceiver extends ResultReceiver
    {
        public AddressResultReceiver(Handler handler) { super(handler); }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            if (resultData == null)
                return;

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
            if (mAddressOutput == null)
                mAddressOutput = "";

            _locationScreen.setSummary(mAddressOutput);
        }
    }

}

package com.dinotom.project_koff_ma.business_entities;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.dinotom.project_koff_ma.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService
{
    private static final String TAG = FetchAddressIntentService.class.getSimpleName();

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    protected ResultReceiver mReceiver;

    public FetchAddressIntentService()
    {
        super("Fetching address");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (intent == null)
            return;

        String errorMessage = "";
        String location = intent.getStringExtra(LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(RECEIVER);

        List<Address> addresses = null;
        List<String> locationSplit = Arrays.asList(location.split(","));

        try {
            addresses = geocoder.getFromLocation(
                    Double.parseDouble(locationSplit.get(0)),
                    Double.parseDouble(locationSplit.get(1)),
                    1
            );
        } catch (IOException ioException){
            // Catch network or other I/O problems.
            Log.e(TAG, "Service unavailable!");
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e(TAG, String.format(
                    "Invalid lat and/or long values: %s %s",
                    locationSplit.get(0),
                    locationSplit.get(1)));
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0)
        {
            if (errorMessage.isEmpty())
            {
                errorMessage = "No address found!";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "Address found!");
            deliverResultToReceiver(SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message)
    {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}

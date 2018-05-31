package com.dinotom.project_koff_ma.business_entities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.business_entities.DayWorkingHours;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BusinessEntitiesUtilities
{
    private static final String TAG = BusinessEntitiesUtilities.class.getSimpleName();

    enum SortMode
    {
        DISTANCE("distance"),
        RATING_DESC("rating_desc"),
        RATING_ASC("rating_asc"),
        A_Z("a_z"),
        Z_A("z_a");

        private final String text;

        SortMode(final String text)
        {
            this.text = text;
        }

        @Override
        public String toString()
        {
            return text;
        }
    }

    // Pretvoriti u array Stringova, ali u xml-u
    static CharSequence SortModeNames[] = new CharSequence[]{
            getFromStringResources(R.string.businessentity_sortby_distance),
            getFromStringResources(R.string.businessentity_sortby_rating_desc),
            getFromStringResources(R.string.businessentity_sortby_rating_asc),
            getFromStringResources(R.string.businessentity_sortby_az),
            getFromStringResources(R.string.businessentity_sortby_za)
    };

    static boolean isWorkingNow(List<DayWorkingHours> workingHours) throws ParseException
    {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        String currentDayShort = new SimpleDateFormat("EE", Locale.ENGLISH).format(currentDate.getTime());

        Log.d(TAG, "currentDayShort " + currentDayShort);

        String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        currentDate = new SimpleDateFormat("HH:mm:ss").parse(currentTime);

        for(DayWorkingHours dwh : workingHours)
        {
            if(dwh != null && dwh.getDayShortName().equals(currentDayShort))
            {
                Date startTime = new SimpleDateFormat("HH:mm:ss").parse(dwh.getStartTime());
                Date endTime = new SimpleDateFormat("HH:mm:ss").parse(dwh.getEndTime());

                Log.d(TAG, "currentTime " + currentDate);
                Log.d(TAG, "StartTime " + startTime);
                Log.d(TAG, "EndTime " + endTime);

                if(startTime.before(currentDate) && endTime.after(currentDate))
                    return true;
                else
                    return false;
            }
        }

        return false;
    }

    static String parseTime(String time) throws ParseException
    {
        Date date = new SimpleDateFormat("HH:mm:ss").parse(time);
        DateFormat writeFormat = new SimpleDateFormat( "HH");
        return writeFormat.format(date);
    }

    static String readableWorkingHours(final List<DayWorkingHours> list) throws ParseException
    {
        if(list.size() == 0)
            return "/";
        else if (list.size() == 1)
            return String.format(
                    "%s: %s-%s",
                    list.get(0).getDayShortName(),
                    parseTime(list.get(0).getStartTime()),
                    parseTime(list.get(0).getEndTime()));

        DayWorkingHours last = list.get(0);
        String result = "";

        for(int i = 1; i < list.size(); ++i)
        {
            if(i == list.size() - 1)
            {
                if(last.equalsStartEndTime(list.get(i)))
                {
                    result += String.format(
                            "%s-%s: %s-%s\n",
                            last.getDayShortName(),
                            list.get(i).getDayShortName(),
                            parseTime(last.getStartTime()),
                            parseTime(last.getEndTime()));
                }
            }
            if(!last.equalsStartEndTime(list.get(i)))
            {
                if(i == list.size() - 1)
                {
                    result += String.format(
                            "%s-%s: %s-%s\n",
                            last.getDayShortName(),
                            list.get(i-1).getDayShortName(),
                            parseTime(last.getStartTime()),
                            parseTime(last.getEndTime()));

                    result += String.format(
                            "%s: %s-%s\n",
                            list.get(i).getDayShortName(),
                            parseTime(list.get(i).getStartTime()),
                            parseTime(list.get(i).getEndTime()));
                }
                else if(last.getDayShortName().equals(list.get(i-1).getDayShortName()))
                {
                    result += String.format(
                            "%s: %s-%s\n",
                            last.getDayShortName(),
                            parseTime(last.getStartTime()),
                            parseTime(last.getEndTime()));
                    last = list.get(i);
                }
                else
                {
                    result += String.format(
                            "%s-%s: %s-%s\n",
                            last.getDayShortName(),
                            list.get(i-1).getDayShortName(),
                            parseTime(last.getStartTime()),
                            parseTime(last.getEndTime()));
                    last = list.get(i);
                }
            }
        }


        return result.substring(0, result.length() - 1);
    }

    static String getFromStringResources(int stringID)
    {
        return KoffGlobal.getAppContext().getResources().getString(stringID);
    }

    static SharedPreferences getSharedPrefs(Integer fileID)
    {
        Context context = KoffGlobal.getAppContext();
        String preferenceFileName = context.getResources().getString(fileID); // name of preference file
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);

        return sharedPref;
    }

    static String getSortMode()
    {
        SharedPreferences sharedPrefs = getSharedPrefs(R.string.business_activities_file);
        String sortModeSetting = KoffGlobal.getAppContext().getResources().getString(R.string.business_activities_sort_mode);
        String sortMode = sharedPrefs.getString(sortModeSetting, SortMode.DISTANCE.toString());

        return sortMode;
    }

    static int getIsWorking()
    {
        SharedPreferences sharedPrefs = getSharedPrefs(R.string.business_activities_file);
        String isWorkingSetting = KoffGlobal.getAppContext().getResources().getString(R.string.business_activities_filter_isworking);
        boolean isWorking = sharedPrefs.getBoolean(isWorkingSetting, false);

        return isWorking ? 1 : 0;
    }

    static Integer getRadius()
    {
        SharedPreferences sharedPrefs = getSharedPrefs(R.string.business_activities_file);
        String radiusSetting = KoffGlobal.getAppContext().getResources().getString(R.string.business_activities_filter_radius);
        int radius = sharedPrefs.getInt(radiusSetting, 10);

        return radius;
    }

    static String getLocation()
    {
        SharedPreferences sharedPrefs = getSharedPrefs(R.string.business_activities_file);
        String locationSetting = KoffGlobal.getAppContext().getResources().getString(R.string.business_activities_filter_location);
        String location = sharedPrefs.getString(locationSetting, "45.350127,14.407801");

        return location;
    }

    static Boolean getIsCustomLocation()
    {
        SharedPreferences sharedPrefs = getSharedPrefs(R.string.business_activities_file);
        String isCustomLocationSetting = getFromStringResources(R.string.business_activities_filter_location_is_custom);
        Boolean isCustomLocation = sharedPrefs.getBoolean(isCustomLocationSetting, false);

        return isCustomLocation;
    }

    static String getSearchTerm()
    {
        SharedPreferences sharedPrefs = getSharedPrefs(R.string.temporary_file);
        String searchTermSetting = getFromStringResources(R.string.business_activities_search_term);

        return sharedPrefs.getString(searchTermSetting, "");
    }

    static String getIds()
    {
        SharedPreferences sharedPrefs = getSharedPrefs(R.string.temporary_file);
        String searchIds = getFromStringResources(R.string.business_activities_search_ids);

        return sharedPrefs.getString(searchIds, "");
    }

    static void setStringSetting (int parameterID, String parameter, Integer fileID)
    {
        SharedPreferences.Editor editor = getSharedPrefs(fileID).edit();
        String parameterName = KoffGlobal.getAppContext().getResources().getString(parameterID);
        editor.putString(parameterName, parameter);
        editor.commit(); // use "apply" if we want asynchronous later
    }

    static void setFloatSetting (int parameterID, Float parameter, Integer fileID)
    {
        SharedPreferences.Editor editor = getSharedPrefs(fileID).edit();
        String parameterName = KoffGlobal.getAppContext().getResources().getString(parameterID);
        editor.putFloat(parameterName, parameter);
        editor.commit(); // use "apply" if we want asynchronous later
    }

    static void setIntSetting (int parameterID, Integer parameter, Integer fileID)
    {
        SharedPreferences.Editor editor = getSharedPrefs(fileID).edit();
        String parameterName = KoffGlobal.getAppContext().getResources().getString(parameterID);
        editor.putInt(parameterName, parameter);
        editor.commit(); // use "apply" if we want asynchronous later
    }

    static void setBoolSetting(int parameterID, Boolean parameter, Integer fileID)
    {
        SharedPreferences.Editor editor = getSharedPrefs(fileID).edit();
        String parameterName = KoffGlobal.getAppContext().getResources().getString(parameterID);
        editor.putBoolean(parameterName, parameter);
        editor.commit(); // use "apply" if we want asynchronous later
    }

    static void getLastLocation(Context context, Activity activity)
    {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        if (!BusinessEntitiesUtilities.getIsCustomLocation() &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                String loc = String.format("%f,%f", location.getLatitude(), location.getLongitude());
                                Log.d(TAG, " " + loc);
                                BusinessEntitiesUtilities.setStringSetting(
                                        R.string.business_activities_filter_location,
                                        loc,
                                        R.string.business_activities_file
                                );
                            }
                            else
                            {
                                BusinessEntitiesUtilities.setStringSetting(
                                        R.string.business_activities_filter_location,
                                        "45.350127,14.407801",
                                        R.string.business_activities_file
                                );
                                Log.d(TAG, "Location is null!");
                            }
                        }
                    });
        }
    }
}

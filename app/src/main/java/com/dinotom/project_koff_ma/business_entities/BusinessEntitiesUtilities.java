package com.dinotom.project_koff_ma.business_entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dinotom.project_koff_ma.KoffGlobal;
import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.business_entities.DayWorkingHours;

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

    static CharSequence SortModeNames[] = new CharSequence[]{
            getStringFromStringResources(R.string.businessentity_sortby_distance),
            getStringFromStringResources(R.string.businessentity_sortby_rating_desc),
            getStringFromStringResources(R.string.businessentity_sortby_rating_asc),
            getStringFromStringResources(R.string.businessentity_sortby_az),
            getStringFromStringResources(R.string.businessentity_sortby_za)
    };

    static boolean isWorkingNow(List<DayWorkingHours> workingHours) throws ParseException {
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

    static String getStringFromStringResources(int stringID)
    {
        return KoffGlobal.getAppContext().getResources().getString(stringID);
    }

    private static SharedPreferences getSharedPrefs()
    {
        Context context = KoffGlobal.getAppContext();
        String preferenceFileName = context.getResources().getString(R.string.business_activities_file); // name of preference file
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);

        return sharedPref;
    }

    static String getSortMode()
    {
        SharedPreferences sharedPrefs = getSharedPrefs();
        String sortModeSetting = KoffGlobal.getAppContext().getResources().getString(R.string.business_activities_sort_mode);
        String sortMode = sharedPrefs.getString(sortModeSetting, SortMode.DISTANCE.toString());

        return sortMode;
    }

    static void setStringSetting (int parameterID, String parameter)
    {
        SharedPreferences.Editor editor = getSharedPrefs().edit();
        String parameterName = KoffGlobal.getAppContext().getResources().getString(parameterID);
        editor.putString(parameterName, parameter);
        editor.commit(); // use "apply" if we want asynchronous later
    }

    static void setFloatSetting (int parameterID, Float parameter)
    {
        SharedPreferences.Editor editor = getSharedPrefs().edit();
        String parameterName = KoffGlobal.getAppContext().getResources().getString(parameterID);
        editor.putFloat(parameterName, parameter);
        editor.commit(); // use "apply" if we want asynchronous later
    }

    static void setIntSetting (int parameterID, Integer parameter)
    {
        SharedPreferences.Editor editor = getSharedPrefs().edit();
        String parameterName = KoffGlobal.getAppContext().getResources().getString(parameterID);
        editor.putInt(parameterName, parameter);
        editor.commit(); // use "apply" if we want asynchronous later
    }
}

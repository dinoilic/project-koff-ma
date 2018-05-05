package com.dinotom.project_koff_ma.business_entities;

import android.util.Log;

import com.dinotom.project_koff_ma.pojo.business_entities.DayWorkingHours;

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

    public static boolean isWorkingNow(List<DayWorkingHours> workingHours) throws ParseException {
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
}

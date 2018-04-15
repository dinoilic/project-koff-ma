package com.dinotom.project_koff_ma;

import android.app.Application;
import android.content.Context;

public class KoffGlobal extends Application
{
    private static Context mContext;

    public void onCreate()
    {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return mContext;
    }
}
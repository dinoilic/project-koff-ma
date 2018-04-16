package com.dinotom.project_koff_ma;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class KoffGlobal extends Application
{
    private static Context mContext;
    public static Bus bus = new Bus(ThreadEnforcer.MAIN);

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
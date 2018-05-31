package com.dinotom.project_koff_ma;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class KoffGlobal extends Application
{
    private static Context mContext;
    public static Bus bus;

    public void onCreate()
    {
        super.onCreate();
        bus = new Bus(ThreadEnforcer.MAIN);
        mContext = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return mContext;
    }
}
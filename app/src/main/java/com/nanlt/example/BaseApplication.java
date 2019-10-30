package com.nanlt.example;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.nanlt.hotfixtinker.FixDexUtils;

public class BaseApplication extends MultiDexApplication {
    static BaseApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance =this;
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
//        FixDexUtils.loadFixedDex(this);
    }
}

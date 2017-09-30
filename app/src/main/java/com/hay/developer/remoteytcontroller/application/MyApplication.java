package com.hay.developer.remoteytcontroller.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hay.developer.remoteytcontroller.service.ForegroundService;

/**
 * Created by hay on 18-Sep-17.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        instance = this;
        startService(new Intent(getApplicationContext(), ForegroundService.class));
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static MyApplication getInstance() {
        return instance;
    }
}

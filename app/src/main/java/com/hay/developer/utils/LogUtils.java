package com.hay.developer.utils;

import android.content.Context;
import android.util.Log;

import com.hay.developer.model.Video;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hay on 18-Sep-17.
 */

public class LogUtils {
    private Context context;
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private Video mVideo = Video.getInstance();

    public LogUtils(Context context) {
        this.context = context;
    }

    private void normalLog(String topic, String message) {
        Log.d(context.getClass().getSimpleName(), topic + " : " + message);
        mData.child("Log").child(TimeAndDate.getCurrentDateString()).child(topic).
                child(TimeAndDate.getCurrentTimeString()).setValue(message);
    }

    private void fatalLog(String topic, String message) {
        Log.d(context.getClass().getSimpleName(), topic + " : " + message);
        mData.child("Log").child(topic).
                child(TimeAndDate.getFullCurrentString()).setValue(message);
    }

    private void stateCurrent(String message){
        mVideo.setmState(message);
        mData.child("Current").setValue(mVideo);
    }

    public void info(String message) {
        normalLog("info",message);
    }

    public void warning(String message) {
        normalLog("warning",message);
    }

    public void success(String message) {
        normalLog("success",message);
    }

    public void playBackStatus(String message) {
        this.normalLog("status",message);
        this.stateCurrent(message);
    }

    public void error(String message) {
        fatalLog("error",message);
    }

    public void crash(String message) {
        fatalLog("crash",message);
    }

}

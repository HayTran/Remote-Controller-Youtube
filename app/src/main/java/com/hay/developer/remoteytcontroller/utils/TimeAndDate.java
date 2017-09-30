package com.hay.developer.remoteytcontroller.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Hay Tran on 20-Aug-17.
 */

public class TimeAndDate {
    public static String getCurrentTimeString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        return format.format(date) + "";
    }

    public static String getCurrentDateString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("dd|MM|yyyy");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        return format.format(date) + "";
    }

    public static String getFullCurrentString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss--dd|MM|yyyy ");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        return format.format(date) + "";
    }
}

package com.hay.developer.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.hay.developer.activity.MainActivity;
import com.hay.developer.R;
import com.hay.developer.model.Video;
import com.hay.developer.utils.Constant;
import com.hay.developer.utils.LogUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by hay on 18-Sep-17.
 */

public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    private static final String PACKAGE_NAME = "com.hay.developer";
    private ScheduledFuture trackingConfiguredSchedule;
    private ScheduledExecutorService service;
    private Video mVideo = Video.getInstance();
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private boolean isAutoRestartApp = true;
    private LogUtils mLogUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        startForegroundNotification("Remote Controller Youtube", null);
        videoTrackingSchedule();
        alwaysOnForegroundTracking();
        return START_STICKY;
    }

    public void videoTrackingSchedule() {
        service = Executors.newScheduledThreadPool(1);
        trackingConfiguredSchedule = service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.player == null) {
                    return;
                }
                if (MainActivity.player.isPlaying()) {
                    mVideo.setCurrentMilisecond(MainActivity.player.getCurrentTimeMillis());
                    mData.child("Current").setValue(mVideo);
                }
            }
        }, 0, Constant.TIME_LOOP_VIDEO_TRACKING, TimeUnit.MILLISECONDS);
    }

    public void alwaysOnForegroundTracking() {
        // Start your (polling) task
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // If you wish to stop the task/polling
                mData.child("IsAutoRestartApp").addValueEventListener(listener);
                if (isAutoRestartApp == false) {
                    return;
                }
                // The first in the list of RunningTasks is always the foreground task.
                ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
                String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

                // Check foreground app: If it is not in the foreground... bring it!
                if (!foregroundTaskPackageName.equals(PACKAGE_NAME)) {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
                    startActivity(LaunchIntent);
                }

                // Check whether or not youtube is stop. If yes, continue playing
                try {
                    if (MainActivity.player.isPlaying() == false) {
                        MainActivity.player.play();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, Constant.TIME_LOOP_APP_ALWAY_FOREGROUND);
    }

    private void alertManager(final String title, final String content) {
        if (isAppOnForeground(getApplicationContext(), PACKAGE_NAME) == true) {
            new Handler(Looper.getMainLooper()).post(
                    new Runnable() {
                        public void run() {
                        }
                    }
            );
        } else {
            startNotification(title, content);
        }
    }

    private boolean isAppOnForeground(Context context, String appPackageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = appPackageName;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void startForegroundNotification(String title, String content) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent).build();
        startForeground(1994, notification);
    }


    private void startNotification(String title, String content) {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1995, notification);
    }

    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            isAutoRestartApp = Boolean.valueOf(dataSnapshot.getValue().toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
}


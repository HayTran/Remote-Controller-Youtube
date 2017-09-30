package com.hay.developer.remoteytcontroller.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hay.developer.remoteytcontroller.activity.MainActivity;

/**
 * Created by Hay Tran on 20-Aug-17.
 */

public class StartMyActivityAtBootReceiver extends BroadcastReceiver {
    private static final String TAG = "StartMyActivityAtBootRe";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
            Log.d(TAG, "onReceive: ");
        }
    }
}

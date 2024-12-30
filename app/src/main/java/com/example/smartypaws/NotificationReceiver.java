package com.example.smartypaws;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "my_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android O and above
            notification = new Notification.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("SMARTY PAWS")
                    .setContentText("Time to study!")
                    .setAutoCancel(true)
                    .build();
        } else {
            // For devices below Android O
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("SMARTY PAWS")
                    .setContentText("Time to study!")
                    .setAutoCancel(true)
                    .build();
        }

        int notificationId = intent.getIntExtra("notification_id", 0);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }
    }
}

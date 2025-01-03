package com.example.smartypaws;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "my_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "onReceive triggered");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Intent to open the app when the notification is clicked
        Intent notificationIntent = new Intent(context, NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android O and above
            notification = new Notification.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("SMARTY PAWS")
                    .setContentText("Time to study!")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        } else {
            // For devices below Android O
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("SMARTY PAWS")
                    .setContentText("Time to study!")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        // Default notification ID if not provided
        int notificationId = intent.getIntExtra("notification_id", 1);

        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }
    }
}

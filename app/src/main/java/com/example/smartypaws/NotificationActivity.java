package com.example.smartypaws;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "my_channel";
    private static final int NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        timePicker = findViewById(R.id.timePicker);

        // Set the default time to 9:00 AM
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(9);
            timePicker.setMinute(0);
        } else {
            timePicker.setCurrentHour(9);
            timePicker.setCurrentMinute(0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Back button listener
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Set button listener
        findViewById(R.id.setButton).setOnClickListener(v -> scheduleNotification());

        // Test notification button listener
        findViewById(R.id.testNotificationButton).setOnClickListener(this::showNotification);
    }


    private void scheduleNotification() {
        Calendar calendar = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= 23) {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        }
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            // Schedule for the next day if the time has already passed.
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, // Repeat daily
                    pendingIntent
            );
            Toast.makeText(this, "Notification scheduled for: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "AlarmManager is not available", Toast.LENGTH_SHORT).show();
        }
    }




    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Study Reminder Channel";
            String channelDescription = "Channel for daily study reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(View view) {
        try {
            Intent intent = new Intent(this, NotificationActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            Notification.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("SMARTY PAWS")
                        .setContentText("Time to study!")
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                Notification notification = builder.build();
                notificationManager.notify(NOTIFICATION_ID, notification);
                Log.d("NotificationActivity", "Notification sent successfully!");
            } else {
                Log.e("NotificationActivity", "Notifications are not supported on this device version.");
            }
        } catch (Exception e) {
            Log.e("NotificationActivity", "Error showing notification", e);
        }
    }
}

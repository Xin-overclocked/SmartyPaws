package com.example.smartypaws;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "my_channel";
    private static final int NOTIFICATION_ID = 1;

    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String PREF_HOUR = "NotificationHour";
    private static final String PREF_MINUTE = "NotificationMinute";

    private NotificationManager notificationManager;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        timePicker = findViewById(R.id.timePicker);

        // Retrieve saved time from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedHour = preferences.getInt(PREF_HOUR, 9); // Default to 9 AM
        int savedMinute = preferences.getInt(PREF_MINUTE, 0); // Default to 0 minutes

        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(savedHour);
            timePicker.setMinute(savedMinute);
        } else {
            timePicker.setCurrentHour(savedHour);
            timePicker.setCurrentMinute(savedMinute);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.setButton).setOnClickListener(v -> scheduleNotification());
        findViewById(R.id.testNotificationButton).setOnClickListener(this::showNotification);
    }

    /*private void scheduleNotification() {
        Calendar calendar = Calendar.getInstance();
        int selectedHour, selectedMinute;

        // Retrieve selected time from the TimePicker
        if (Build.VERSION.SDK_INT >= 23) {
            selectedHour = timePicker.getHour();
            selectedMinute = timePicker.getMinute();
        } else {
            selectedHour = timePicker.getCurrentHour();
            selectedMinute = timePicker.getCurrentMinute();
        }

        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);

        // Adjust time if it's in the past
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Schedule for the next day
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

            // Save selected time in SharedPreferences
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(PREF_HOUR, selectedHour);
            editor.putInt(PREF_MINUTE, selectedMinute);
            editor.apply();

            Toast.makeText(this, "Notification scheduled for: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "AlarmManager is not available", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void scheduleNotification() {
        // Get the time from the TimePicker
        Calendar calendar = Calendar.getInstance();
        int selectedHour, selectedMinute;

        // Retrieve selected time from the TimePicker
        if (Build.VERSION.SDK_INT >= 23) {
            selectedHour = timePicker.getHour();
            selectedMinute = timePicker.getMinute();
        } else {
            selectedHour = timePicker.getCurrentHour();
            selectedMinute = timePicker.getCurrentMinute();
        }

        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);

        // Adjust time if it's in the past (schedule for the next day)
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Calculate the delay in milliseconds until the scheduled time
        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();

        // Create a PeriodicWorkRequest
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)  // Delay until the notification time
                .build();

        // Enqueue the PeriodicWorkRequest with WorkManager
        WorkManager.getInstance(this).enqueue(workRequest);

        // Save selected time in SharedPreferences (optional)
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_HOUR, selectedHour);
        editor.putInt(PREF_MINUTE, selectedMinute);
        editor.apply();

        Toast.makeText(this, "Notification scheduled for: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
    }




    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Reminder",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for daily notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    public void showNotification(View view) {
        try {
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("notification_id", NOTIFICATION_ID);

            // Trigger the NotificationReceiver
            sendBroadcast(intent);

            Log.d("NotificationActivity", "Broadcast sent to NotificationReceiver");
        } catch (Exception e) {
            Log.e("NotificationActivity", "Error sending broadcast to NotificationReceiver", e);
        }
    }

}

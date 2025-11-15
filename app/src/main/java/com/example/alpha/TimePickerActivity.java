package com.example.alpha;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class TimePickerActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 101;
    private static final int ALARM_REQUEST_CODE = 102;

    private EditText etNotificationText;
    private TimePicker timePicker;
    private Button btnSetNotification;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        etNotificationText = findViewById(R.id.etNotificationText);
        timePicker = findViewById(R.id.timePicker);
        btnSetNotification = findViewById(R.id.btnSetNotification);

        timePicker.setIs24HourView(true);

        btnSetNotification.setOnClickListener(v -> checkPermissionsAndSetAlarm());
    }

    private void checkPermissionsAndSetAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            } else {
                setAlarm();
            }
        } else {
            setAlarm();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setAlarm();
            } else {
                Toast.makeText(this, "Notification permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setAlarm() {
        String notificationText = etNotificationText.getText().toString();
        if (notificationText.isEmpty()) {
            Toast.makeText(this, "Please enter a text for the notification", Toast.LENGTH_SHORT).show();
            return;
        }

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.NOTIFICATION_TEXT_EXTRA, notificationText);

        alarmIntent = PendingIntent.getBroadcast(
                this,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());
        calendar.set(Calendar.SECOND, 0);

        // If the time is in the past, set it for the next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmMgr.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmIntent
        );

        Toast.makeText(this, "Reminder set!", Toast.LENGTH_SHORT).show();
    }
}

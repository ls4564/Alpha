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
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Calendar;

public class TimePickerActivity extends BaseActivity {
    private TimePicker timePicker;
    private EditText etNotificationText;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private static final int NOTIFICATION_PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        timePicker = findViewById(R.id.timePicker);
        etNotificationText = findViewById(R.id.etNotificationText);

        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Notification Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification Permission Denied. Notifications may not show.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void createNotification(View view) {
        String text = etNotificationText.getText().toString();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();
        
        calSet.set(Calendar.HOUR_OF_DAY, hour);
        calSet.set(Calendar.MINUTE, minute);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if(calSet.compareTo(calNow) <= 0) {
            calSet.add(Calendar.DATE, 1);
        }
        setAlarm(calSet, text);
    }

    private void setAlarm(Calendar calSet, String text) {
        int ALARM_RQST_CODE = (int) System.currentTimeMillis() / 1000;
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("text", text);
        alarmIntent = PendingIntent.getBroadcast(this, ALARM_RQST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC, calSet.getTimeInMillis(), alarmIntent);

        Toast.makeText(this, String.valueOf(ALARM_RQST_CODE)+" Notification in "+String.valueOf(calSet.getTime()), Toast.LENGTH_LONG).show();
	}
}

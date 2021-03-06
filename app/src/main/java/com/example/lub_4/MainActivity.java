package com.example.lub_4;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    CalendarView calendarView;
    TextView myDate;
    Button button1;
    String data;
    int dayOfMonth1;
    int month1;
    int year1;
    Button button2;
    String s;
    public static final String SHARED_PREFS = "prefs";
    public static final String KEY_BUTTON_TEXT = "keyButtonText";
    private static final String CHANNEL_ID = "channel_id01";
    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        myDate = (TextView) findViewById(R.id.myDate);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.example_widget_button);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if (month / 10 >= 1) {
                    data = dayOfMonth + "/" + (month + 1) + "/" + year;
                } else data = dayOfMonth + "/" + ("0" + (month + 1)) + "/" + year;
                dayOfMonth1 = dayOfMonth;
                year1 = year;
                month1 = month;
                myDate.setText(data);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataChooser dataChooser = new DataChooser();
                int dataLeft = dataChooser.dataLeft(dayOfMonth1, (month1 + 1), year1);
                dataLeft = dataLeft * (-1);
                s = String.valueOf(dataLeft);
                myDate.setText("Дней осталось: " + s);
                Intent intent = new Intent(MainActivity.this, WidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);
                SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_BUTTON_TEXT, s);
                editor.apply();
                showNotification();

                final Calendar calendar = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= 23){
                    calendar.set(
                            year1,
                            month1,
                            dayOfMonth1,
                            9,0,0
                            );
                } else {
                    return;
                }

                setAlarm(calendar.getTimeInMillis());
            }


        });
    }

    private void setAlarm(long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, MyAlarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
    }

    private void showNotification() {

        createNotificationChannel();

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mainPIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.time);
        builder.setContentTitle("Дней осталось:");
        builder.setContentText(s);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(mainPIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            CharSequence name = "My Notification";
            String description = "My notification description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}

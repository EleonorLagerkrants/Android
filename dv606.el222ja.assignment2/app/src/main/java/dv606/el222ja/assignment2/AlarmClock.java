package dv606.el222ja.assignment2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmClock extends AppCompatActivity {
    TextView setAlarmTime;
    PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Current time is set and updated every 5 seconds
        getCurrentTime();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCurrentTime();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();


        setAlarmTime = (TextView)findViewById(R.id.alarmTime);
        setAlarmTime.setText(":");


        //Buttons for setting and removing an alarm is created and a listener is added to each
        Button setAlarm = (Button)findViewById(R.id.buttonAlarm);
        setAlarm.setOnClickListener(setNewAlarmListener);
        Button removeAlarm = (Button)findViewById(R.id.removeAlarm);
        removeAlarm.setOnClickListener(removeAlarmListener);


    }
    //Listener for setAlarm Button, starts new activity
    private OnClickListener setNewAlarmListener = new OnClickListener() {
        public void onClick(View v) {
            startActivityForResult(new Intent(AlarmClock.this, SetAlarmTime.class), 1);
        }
    };

    // When the activity SetAlarmTime is finished the results is set to the Current Alarm textview
    // and a pendingintent for the set time is created.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String hour = data.getStringExtra("hour");
            String minute = data.getStringExtra("minute");
            String message = data.getStringExtra("message");
            int alarmHour = Integer.parseInt(hour);
            int alarmMinute =Integer.parseInt(minute);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
            calendar.set(Calendar.MINUTE, alarmMinute);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String time = df.format(calendar.getTime());
            setAlarmTime.setText(time);
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

            Toast.makeText(AlarmClock.this, "Alarm is set", Toast.LENGTH_SHORT).show();
            intent.putExtra("message", message);
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }

    }

    // Returns the current time
    public void getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String time = df.format(cal.getTime());
        TextView currentTime = (TextView)findViewById(R.id.currentTime);
        currentTime.setText(time);
    }

    // OnClickListener for remove alarm, cancels the pendingintent
    private OnClickListener removeAlarmListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            alarmIntent.cancel();
            setAlarmTime.setText(":");
            Toast.makeText(AlarmClock.this, "Alarm is removed", Toast.LENGTH_SHORT).show();
        }
    };



}

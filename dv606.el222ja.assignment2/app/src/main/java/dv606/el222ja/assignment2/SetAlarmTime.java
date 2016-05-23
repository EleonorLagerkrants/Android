package dv606.el222ja.assignment2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class SetAlarmTime extends AppCompatActivity {

    TimePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        picker = (TimePicker)findViewById(R.id.timePicker);
        picker.setIs24HourView(true);
        Button setAlarm = (Button)findViewById(R.id.setTime);
        setAlarm.setOnClickListener(setAlarmListener);

    }

    // Sends the picked time and message back to the AlarmClock activity
    private View.OnClickListener setAlarmListener = new View.OnClickListener() {
        public void onClick(View v) {
            picker.clearFocus();
            int hour = picker.getCurrentHour();
            int minute = picker.getCurrentMinute();
            String h = String.valueOf(hour);
            String m = String.valueOf(minute);

            EditText message = (EditText)findViewById(R.id.message);
            String mess = message.getText().toString();

            Intent results = new Intent();
            results.putExtra("hour", h );
            results.putExtra("minute", m );
            results.putExtra("message", mess);
            setResult(SetAlarmTime.RESULT_OK, results);
            finish();
        }
    };

}

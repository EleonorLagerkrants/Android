package dv606.el222ja.assignment2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DialogActivity extends AppCompatActivity {

    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        message = i.getStringExtra("message");

        //Creates a new AlertDialog that contains the chosen alarm message
        // as well as instructions on how to turn of the alarm
        // The sound is disabled by opening the notification, while pressing the OK button
        // in the alertdialog cancels the pendingintent.
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.dialog_title));
        alertDialog.setMessage(message + "\n\n" + getString(R.string.dialog_message));
        alertDialog.setButton(getString(R.string.dialog_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                intent.putExtra("message", message);
                PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                sender.cancel();
                am.cancel(sender);
                finish();
            }
        });
        alertDialog.show();
    }

}


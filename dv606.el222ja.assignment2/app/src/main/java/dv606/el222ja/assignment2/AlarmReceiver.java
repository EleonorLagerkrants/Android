package dv606.el222ja.assignment2;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Eleonor on 2016-04-15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    String message;

    @Override
    public void onReceive(Context context, Intent intent) {
        message = intent.getStringExtra("message");

        // Creates notification for the alarm,
        // the chosen alarm message is displayed and an alarm sound is played
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Alarm Clock");
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.alarm);
        try {
            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.getPackageName() + "/" + R.raw.siren);
            builder.setSound(sound);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

        // Starts a new activity, that contains a dialog
        Intent i = new Intent();
        i.setClassName("dv606.el222ja.assignment2", "dv606.el222ja.assignment2.DialogActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("message", message);
        context.startActivity(i);
    }
}



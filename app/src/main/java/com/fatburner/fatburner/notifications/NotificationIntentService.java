package com.fatburner.fatburner.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.fatburner.fatburner.DatabaseHelper;
import com.fatburner.fatburner.Diet;
import com.fatburner.fatburner.R;

import com.fatburner.fatburner.NotificationActivity;
//import klogi.com.notificationbyschedule.R;
import com.fatburner.fatburner.Utils;
import com.fatburner.fatburner.Water;
import com.fatburner.fatburner.broadcast_receivers.NotificationEventReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.fatburner.fatburner.broadcast_receivers.NotificationEventReceiver.cancelAlarm;
import static com.fatburner.fatburner.broadcast_receivers.NotificationEventReceiver.setupAlarm;

/**
 * Created by klogi
 *
 *
 */
public class NotificationIntentService extends IntentService {

    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {

        DatabaseHelper databaseHelper;
        SQLiteDatabase db;
        Cursor userCursor;

        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        boolean foodNotificationValue = false;
        db = databaseHelper.open();
        userCursor =  db.rawQuery("select * from APP_SETTINGS", null);
        userCursor.moveToFirst();
        if(userCursor.getInt(9) == 1)
        {foodNotificationValue = true;}
        userCursor.close();
        db.close();

        if(foodNotificationValue) {


            int NOTIFICATION_ID = 1;

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Fat burner")
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText("Прием пищи")
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(alarmSound)
                    .setSmallIcon(R.drawable.ic_dish);

            Intent mainIntent = new Intent(this, Diet.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID,
                    mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

            final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            manager.notify(NOTIFICATION_ID, builder.build());

            cancelAlarm(getApplicationContext());
            Calendar nextNotificationTime = Utils.getNotificationTime(getApplicationContext(), Utils.incrementTime(1));
            setupAlarm(getApplicationContext(), nextNotificationTime);

        }
    }
}

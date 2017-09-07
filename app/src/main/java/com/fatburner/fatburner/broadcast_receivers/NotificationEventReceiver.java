package com.fatburner.fatburner.broadcast_receivers;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.fatburner.fatburner.DatabaseHelper;
import com.fatburner.fatburner.Utils;
import com.fatburner.fatburner.notifications.NotificationIntentService;

/**
 * Created by klogi
 *
 * WakefulBroadcastReceiver used to receive intents fired from the AlarmManager for showing notifications
 * and from the notification itself if it is deleted.
 */
public class NotificationEventReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";

    private static final int NOTIFICATIONS_INTERVAL_IN_HOURS = 3;

    public static void setupAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(context);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTriggerAt(new Date()), NOTIFICATIONS_INTERVAL_IN_HOURS * AlarmManager.INTERVAL_HOUR, alarmIntent); //use 1000 delay for debug
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(context);
        alarmManager.cancel(alarmIntent);
    }

    private static long getTriggerAt(Date now) {
        //get Start Time for current date.
        return Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis();
    }

    private static PendingIntent getStartPendingIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent serviceIntent = null;
        if (ACTION_START_NOTIFICATION_SERVICE.equals(action)) {
            Log.i(getClass().getSimpleName(), "onReceive from alarm, starting notification service");


            //Don't know if it helps
            ////// - getting time
            DatabaseHelper databaseHelper;
            SQLiteDatabase db;
            Cursor userCursor;

            databaseHelper = new DatabaseHelper(context);
            databaseHelper.getWritableDatabase();
            db = databaseHelper.open();
            int dayId = Utils.getCurrentDayID()-1;

            userCursor = db.query("MEAL_SETTINGS", null, "DAY = ?", new String[]{String.valueOf(dayId)}, null, null, null);

            List<String> timesList = new ArrayList<>();
            List<Integer> days = new ArrayList<>();


            Integer a = userCursor.getCount();

            if (userCursor.getCount() == 0)
            {
                return;
            }

            if (userCursor.moveToFirst()) {
                do {
                    timesList.add(userCursor.getString(4));
                    days.add(userCursor.getInt(0));
                } while (userCursor.moveToNext());
            }

            userCursor.close();
            db.close();

            boolean foodNotificationValue = false;
            db = databaseHelper.open();
            userCursor =  db.rawQuery("select * from APP_SETTINGS", null);
            userCursor.moveToFirst();
            if(userCursor.getInt(9) == 1)
            {foodNotificationValue = true;}
            userCursor.close();
            db.close();

            String time = timesList.get(0);


            Integer hour  = Integer.valueOf(time.substring(0, time.indexOf(":")));


            int currentTime = Calendar.getInstance(TimeZone.getDefault()).getTime().getHours();


            Integer lastHour  = Integer.valueOf(timesList.get(4).substring(0, timesList.get(4).indexOf(":")));

            if(foodNotificationValue) {
                if (hour -1 < currentTime) {
                    if (currentTime < lastHour + 1) {
                        serviceIntent = NotificationIntentService.createIntentStartNotificationService(context);
                    }
                }
            }
            //

            //serviceIntent = NotificationIntentService.createIntentStartNotificationService(context);
        } else if (ACTION_DELETE_NOTIFICATION.equals(action)) {
            Log.i(getClass().getSimpleName(), "onReceive delete notification action, starting notification service to handle delete");

            //commented
            //serviceIntent = NotificationIntentService.createIntentDeleteNotification(context);
        }

        if (serviceIntent != null) {
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, serviceIntent);
        }
    }
}

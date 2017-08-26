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

        ////// - getting time
        DatabaseHelper databaseHelper;
        SQLiteDatabase db;
        Cursor userCursor;

        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();
        int dayId = Utils.getCurrentDayID();

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

        String time = timesList.get(0);


        Integer hour  = Integer.valueOf(time.substring(0, time.indexOf(":")));
        Integer min  = Integer.valueOf(time.substring(time.indexOf(":")+1, time.length()));

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, dayId-1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int scheduleTime = calendar.HOUR;

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int currentTime = calendar.HOUR;

        int NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        // Do something. For example, fetch fresh data from backend to create a rich notification?

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Fat burner")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText("Прием пищи")
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
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

        Integer lastHour  = Integer.valueOf(timesList.get(4).substring(0, timesList.get(4).indexOf(":")));

        if( (hour-1 < currentTime) && (currentTime < lastHour - 1)) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }

        timesList.clear();

    }
}

package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.fatburner.fatburner.broadcast_receivers.NotificationEventReceiver;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.fatburner.fatburner.GlobalVariables.LOAD_ARRAY;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_DAYS;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_ID;

public class MainActivity extends Menu {

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    Intent intent;

    List<String> dates = new ArrayList<>();
    List<Integer> lastDateInDB = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // создаем базу данных
        //databaseHelper = new DatabaseHelper(getApplicationContext());
        //databaseHelper.create_db();

        // Implements menu visibility
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

        String appKey = "5ea0f9120f32340d9a53490829a3e2df63da3f72a50fc69d";
        Appodeal.initialize(this, appKey, Appodeal.SKIPPABLE_VIDEO | Appodeal.BANNER);
        Appodeal.disableNetwork(this, "cheetah");


        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        userCursor = db.query("CALENDAR", null, null, null, null, null, null);

            if (userCursor.moveToFirst()) {
                do {
                    dates.add(userCursor.getString(userCursor.getColumnIndex("DATE")));
                } while (userCursor.moveToNext());
            }

       int day =  Utils.getParsedDate("day");
       int month = Utils.getParsedDate("month"); //apparently current month returned as previous
       int year = Utils.getParsedDate("year");

        if(dates.size() != 0)
        {lastDateInDB = Utils.normalizeDateForColoring(dates.get(dates.size()-1));}
        else{lastDateInDB = Utils.normalizeDateForColoring(year + "-" + month + "-" + (day-1));}

        if(year == lastDateInDB.get(0) && month == lastDateInDB.get(1) && day > lastDateInDB.get(2) )
        {
            for(int i = 1; i < 31 - lastDateInDB.get(2) + 1; i++){
                ContentValues cv = new ContentValues();
                cv.put("DATE", year + "-" + month + "-" + (lastDateInDB.get(2) + i));
                cv.put("TRAINING_STATUS", "0");
                cv.put("WATER_STATUS","0");
                cv.put("FOOD_STATUS","0");
                cv.put("PROGRAMM_STATUS","0");
                cv.put("TRAINING_NAME","Нет данных");
                cv.put("PROGRAMM_NAME","Нет данных");
                db.insert("CALENDAR", null, cv);
            }
        }

        if(year == lastDateInDB.get(0) && month > lastDateInDB.get(1))
        {
            for(int i = 1; i < 32; i++){
                ContentValues cv = new ContentValues();
                cv.put("DATE", year + "-" + month + "-" + i);
                cv.put("TRAINING_STATUS", "0");
                cv.put("WATER_STATUS","0");
                cv.put("FOOD_STATUS","0");
                cv.put("PROGRAMM_STATUS","0");
                cv.put("TRAINING_NAME","");
                cv.put("PROGRAMM_NAME","0");
                db.insert("CALENDAR", null, cv);
            }
        }

        if(year > lastDateInDB.get(0))
        {
            for(int i = 1; i < 32; i++){
                ContentValues cv = new ContentValues();
                cv.put("DATE", year + "-" + month + "-" + i);
                cv.put("TRAINING_STATUS", "0");
                cv.put("WATER_STATUS","0");
                cv.put("FOOD_STATUS","0");
                cv.put("PROGRAMM_STATUS","0");
                cv.put("TRAINING_NAME","");
                cv.put("PROGRAMM_NAME","0");
                db.insert("CALENDAR", null, cv);
            }
        }




        /*
        userCursor = db.query("CALENDAR", null, "DATE = ?", new String[] {Utils.getCurrentDate()}, null, null, null);
        int itemsCount = userCursor.getCount();

        if(itemsCount == 0){
            ContentValues cv = new ContentValues();
            cv.put("DATE", Utils.getCurrentDate());
            cv.put("TRAINING_STATUS", "0");
            cv.put("WATER_STATUS","0");
            cv.put("FOOD_STATUS","0");
            cv.put("PROGRAMM_STATUS","0");
            cv.put("TRAINING_NAME","");
            cv.put("PROGRAMM_NAME","0");
            db.insert("CALENDAR", null, cv);
        }
        */
        //2017-7-28

        userCursor.close();
        db.close();



        db = databaseHelper.open();

        userCursor =  db.rawQuery("select * from APP_SETTINGS", null);

        userCursor.moveToFirst();
        if(userCursor.getInt(0) == 1)
        {
            intent = new Intent(MainActivity.this, TrainingsCalendar.class);
            startActivity(intent);
        }
        else
            {
            intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
            }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // To prevent crash on resuming activity  : interaction with fragments allowed only after Fragments Resumed or in OnCreate
    // http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        // handleIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }


}



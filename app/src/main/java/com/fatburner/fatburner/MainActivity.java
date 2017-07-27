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

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.IOException;
import java.util.Calendar;

import static com.fatburner.fatburner.GlobalVariables.LOAD_ARRAY;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_DAYS;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_ID;

public class MainActivity extends Menu {

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    Intent intent;

    String trainingDaysDB;

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


        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();


        userCursor = db.query("CALENDAR", null, "DATE = ?", new String[] {Utils.getCurrentDate()}, null, null, null);

        if(userCursor.getCount() == 0){
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
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }


}



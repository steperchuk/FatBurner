package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import static com.fatburner.fatburner.TrainingsList.COLUMN_PROGRAMM_NAME;
import static com.fatburner.fatburner.TrainingsList.COLUMN_TRAINING_ID;

public class MainActivity extends Menu {

    static final String COLUMN_COMPLETION_STATUS = "COMPLETION_STATUS";
    static final String COLUMN_IS_CURRENT = "IS_CURRENT";

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


        SharedPreferences value = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastTrainingDate = value.getString("currentDay", "");
        int lastTrainingProgress = loadProgressForLastTrainig();
        int currentTrainingID = getCurrentTrainingID();


        if(!lastTrainingDate.equals(Utils.getCurrentDate()) &&  lastTrainingProgress == 100 &&  currentTrainingID !=0)
        {
                selectNextTraining();
        }

        SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        sp.putString("currentDay", Utils.getCurrentDate());
        sp.commit();

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


    private void selectNextTraining(){
        String currentProgram = getCurrentProgramm();
        int currentTraining = getCurrentTrainingID();
        int trainingsCount = getTrainingsCount();
        if(currentTraining == trainingsCount)
        {
            return;
        }

        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        ContentValues cv = new ContentValues();
        cv.put("IS_CURRENT", 0);
        db.update("TRAININGS", cv, null, null);

        db = databaseHelper.open();
        cv.put("IS_CURRENT", 1);
        db.update("TRAININGS", cv, COLUMN_TRAINING_ID + " = ? and " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{String.valueOf(currentTraining+1), String.valueOf(currentProgram)});

        cv = new ContentValues();
        db = databaseHelper.open();
        cv.put("CURRENT_PROGRAMM", currentProgram);
        cv.put("CURRENT_TRAINING", currentTraining+1);
        db.update("TRAINING_SETTINGS",cv,null,null);

        db = databaseHelper.open();
        cv = new ContentValues();
        cv.put("PROGRAMM_NAME", currentProgram);
        cv.put("TRAINING_NAME", currentTraining+1);
        db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});

        db.close();
        databaseHelper.close();

    }

    private int getTrainingsCount(){

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        userCursor = db.rawQuery("select * from " + "TRAININGS" + " where " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{getCurrentProgramm()});

        List<Integer> trainings = new ArrayList<>();
        List<Integer> isCurrent = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            do {
                trainings.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_TRAINING_ID)));
                isCurrent.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_IS_CURRENT)));
            } while (userCursor.moveToNext());
        }

        db.close();
        userCursor.close();

        return trainings.size();

    }

    private int getCurrentTrainingID(){

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        int trainingId = 0;

        userCursor = db.rawQuery("select TRAINING_ID from TRAININGS where IS_CURRENT = 1", null);
        if(userCursor.moveToFirst()){
            trainingId = userCursor.getInt(0);
        }

        db.close();
        userCursor.close();

        return trainingId;

    }

    private String getCurrentProgramm() {
        db = databaseHelper.open();
        userCursor = db.rawQuery("select " + DatabaseHelper.COLUMN_NAME + " from PROGRAMMS" +
                " where " + DatabaseHelper.COLUMN_IS_CURRENT + " = 1", null);

        List<String> current = new ArrayList<>();
        if (userCursor.moveToFirst()) {
            do {
                current.add(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
            } while (userCursor.moveToNext());
        }

        db.close();
        userCursor.close();
        return current.get(0);
    }

    private int loadProgressForLastTrainig(){

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        int progress = 0;

        userCursor = db.rawQuery("select PROGRESS from TRAININGS where IS_CURRENT = 1", null);
        if(userCursor.moveToFirst()){
            progress = userCursor.getInt(0);
        }

        db.close();
        userCursor.close();

        return progress;
    }




}



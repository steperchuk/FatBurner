package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.SkippableVideoCallbacks;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.color.holo_orange_dark;
import static com.fatburner.fatburner.TrainingsList.COLUMN_PROGRAMM_NAME;
import static com.fatburner.fatburner.TrainingsList.COLUMN_PROGRESS;
import static com.fatburner.fatburner.TrainingsList.COLUMN_TRAINING_ID;

/**
 * Created by sergeyteperchuk on 6/13/17.
 */

public class TrainingCompleted extends Menu {

    DonutProgress doneBtn;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appodeal.show(TrainingCompleted.this, Appodeal.INTERSTITIAL);
        //Appodeal.disableNetwork(this, "cheetah");


        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_training_completed, null, false);
        mDrawerLayout.addView(contentView, 0);

        doneBtn = (DonutProgress) findViewById(R.id.done_btn);
        doneBtn.setFinishedStrokeWidth(15);
        doneBtn.setUnfinishedStrokeWidth(15);
        doneBtn.setText("Готово");
        doneBtn.setFinishedStrokeWidth(15);
        doneBtn.setUnfinishedStrokeWidth(15);
        doneBtn.setTextColor(R.color.OrangeDark);
        doneBtn.setFinishedStrokeColor(R.color.OrangeDark);
        doneBtn.setUnfinishedStrokeColor(R.color.OrangeDark);
        startAnimation ();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                db = databaseHelper.open();
                ContentValues cv = new ContentValues();
                cv.put("PROGRESS", 100);
                db.update("TRAININGS", cv, "IS_CURRENT = ?" , new String[]{String.valueOf(1)});
                db.close();
                databaseHelper.close();

                db = databaseHelper.open();

                cv = new ContentValues();
                cv.put("TRAINING_STATUS", 100);
                db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});

                databaseHelper.close();
                db.close();

                SetProgramProgress();

                    Intent intent = new Intent(TrainingCompleted.this, MainActivity.class);
                    startActivity(intent);
            }
        });
    }


    private void SetProgramProgress(){
        ///Work with DB
        // открываем подключение
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        //DatabaseHelper.TABLE = "TRAININGS";

        List<Integer> allTrainings = new ArrayList<>();
        List<Integer> allLoad = new ArrayList<>();
        db = databaseHelper.open();
        userCursor = db.rawQuery("select * from " + "TRAININGS" + " where " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{getCurrentProgramm()});
        if (userCursor.moveToFirst()) {
            do {
                allTrainings.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_TRAINING_ID)));
                allLoad.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_PROGRESS)));
            } while (userCursor.moveToNext());
        }

        int totalTrainingsLoad = 0;
        for(int i = 0; i < allTrainings.size(); i++){
            totalTrainingsLoad = totalTrainingsLoad + allLoad.get(i);
        }

        if(totalTrainingsLoad != 0) {

            float totalTrainings = (allTrainings.size() * 100);
            float percentValue = totalTrainingsLoad / totalTrainings;
            float programmCompletion = Math.round(percentValue * 100);
            ContentValues cv = new ContentValues();
            cv.put("COMPLETION_STATUS", programmCompletion);
            db = databaseHelper.open();
            db.update("PROGRAMMS", cv, "NAME = ?", new String[]{String.valueOf(getCurrentProgramm())});

            db = databaseHelper.open();
            cv = new ContentValues();
            cv.put("PROGRAMM_STATUS", programmCompletion);
            db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});
        }

        db.close();
        userCursor.close();
    }

    void startAnimation () {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(TrainingCompleted.this, R.animator.progress_anim);
        set.setInterpolator(new DecelerateInterpolator());
        set.setTarget(doneBtn);
        set.start();
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
}

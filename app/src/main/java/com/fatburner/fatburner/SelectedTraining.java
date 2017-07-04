package com.fatburner.fatburner;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fatburner.fatburner.GlobalVariables.LOAD_ARRAY;
import static com.fatburner.fatburner.GlobalVariables.TRAINING;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_ID;

/**
 * Created by sergeyteperchuk on 6/12/17.
 */

public class SelectedTraining extends Menu {

    String TABLE = "EXERCISES_LIST";

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    DonutProgress startButton;

    int trainingId = 1;
    int trainingCompletionPercent;
    int loadArray[];

    String training[] = {"Бег","Берпи","Отжимания","Приседания","Трастеры","Запрыгивания на тумбу", " ", " ", " ", " "};
    String scoreValue = "30";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_selected_training, null, false);
        mDrawerLayout.addView(contentView, 0);


        ///Work with DB
        // открываем подключение
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        //DatabaseHelper.TABLE = "TRAININGS";

        // массив данных
        userCursor = db.rawQuery("select DAY from TRAININGS where IS_CURRENT = 1", null);
        userCursor.moveToFirst();
        int day = userCursor.getInt(0);

        userCursor = db.rawQuery("select PROGRESS from TRAININGS where IS_CURRENT = 1", null);
        userCursor.moveToFirst();
        int progress = userCursor.getInt(0);

        userCursor = db.rawQuery("select TRAINING_ID from TRAININGS where IS_CURRENT = 1", null);
        userCursor.moveToFirst();
        int trainingId = userCursor.getInt(0);

        userCursor =  db.rawQuery("select * from "+ TABLE + " where Day = " + day, null);
        List<String> exercisesInfo = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            do {
                exercisesInfo.add(userCursor.getString(userCursor.getColumnIndex("EXERCISE")) + "\n" +
                userCursor.getString(userCursor.getColumnIndex("EXERCISE_INFO")) + "\n" +
                        "Подходов: " + userCursor.getString(userCursor.getColumnIndex("ATTEMPTS"))  +
                 "   Повторений: " + userCursor.getString(userCursor.getColumnIndex("REPEATS")) + "   Отдых: " +
                userCursor.getInt(userCursor.getColumnIndex("RELAX_TIME"))/60 + " мин");

            } while (userCursor.moveToNext());
        }

        userCursor.close();

        trainingCompletionPercent = progress;

        startButton = (DonutProgress) findViewById(R.id.start_btn);
        ImageButton nextButton = (ImageButton) findViewById(R.id.next_btn);
        ImageButton prevButton = (ImageButton) findViewById(R.id.prev_btn);

        final TextView trainingLabel = (TextView) findViewById(R.id.trainingId);

        TextView exercise1 = (TextView) findViewById(R.id.exercise_1);
        TextView exercise2 = (TextView) findViewById(R.id.exercise_2);
        TextView exercise3 = (TextView) findViewById(R.id.exercise_3);
        TextView exercise4 = (TextView) findViewById(R.id.exercise_4);
        TextView exercise5 = (TextView) findViewById(R.id.exercise_5);
        TextView exercise6 = (TextView) findViewById(R.id.exercise_6);
        TextView exercise7 = (TextView) findViewById(R.id.exercise_7);
        TextView exercise8 = (TextView) findViewById(R.id.exercise_8);
        TextView exercise9 = (TextView) findViewById(R.id.exercise_9);
        TextView exercise10 = (TextView) findViewById(R.id.exercise_10);

        trainingLabel.setText("Training: " + trainingId);

        for(int i = 0; i < exercisesInfo.size(); i++) {
            switch (i) {
                case 0:
                exercise1.setText(exercisesInfo.get(0));
                    break;
                case 1:
                exercise2.setText(exercisesInfo.get(1));
                    break;
                case 2:
                exercise3.setText(exercisesInfo.get(2));
                    break;
                case 3:
                exercise4.setText(exercisesInfo.get(3));
                    break;
                case 4:
                exercise5.setText(exercisesInfo.get(4));
                    break;
                case 5:
                exercise6.setText(exercisesInfo.get(5));
                    break;
                case 6:
                exercise7.setText(exercisesInfo.get(6));
                    break;
                case 7:
                exercise8.setText(exercisesInfo.get(7));
                    break;
                case 8:
                exercise9.setText(exercisesInfo.get(8));
                    break;
                case 9:
                exercise10.setText(exercisesInfo.get(9));
                    break;
            }
        }
        setButtonLabel();

        startButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(SelectedTraining.this, Exercise.class);
                    startActivity(intent);
                }
            });

        /*
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(trainingId < training.length)
                {
                    trainingLabel.setText("Training: " + (trainingId+1));
                    trainingId++;
                    trainingCompletionPercent = loadArray[trainingId];
                    setButtonLabel();
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(trainingId > 0)
                {
                    trainingLabel.setText("Training: " + (trainingId - 1));
                    trainingId--;
                    trainingCompletionPercent = loadArray[trainingId];
                    setButtonLabel();
                }
            }
        });
*/
        }

    void startAnimation ( ) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(SelectedTraining.this, R.animator.progress_anim);
        set.setInterpolator(new DecelerateInterpolator());
        set.setTarget(startButton);
        set.start();

    }

    void setButtonLabel(){
        switch (trainingCompletionPercent){
            case 0:
                startButton.setText("Start");
                break;
            case 100:
                startButton.setText("Repeat");
                break;
            default:
                startButton.setText("Continue");
        }
        startButton.setProgress(trainingCompletionPercent);
    }

    /*
    void saveSettings() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("selectedTraining",String.valueOf(trainingId));
        Set<String> trainingSet = new HashSet<>();
        Collections.addAll(trainingSet, training);
        ed.putStringSet("training", trainingSet);
        ed.commit();
    }
*/

    }


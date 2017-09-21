package com.fatburner.fatburner;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sergeyteperchuk on 6/12/17.
 */

public class SelectedTraining extends Menu {

    ListView exercisesList;

    final String ATTRIBUTE_NAME = "name";
    final String ATTRIBUTE_NAME_INFO = "info";
    final String ATTRIBUTE_NAME_RELAX = "time";

    String TABLE = "EXERCISES_LIST";

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    DonutProgress startButton;

    int trainingCompletionPercent;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_selected_training, null, false);
        mDrawerLayout.addView(contentView, 0);

        blink();

        ///Work with DB
        // открываем подключение
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

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

        userCursor = db.rawQuery("select PROGRAMM_NAME from TRAININGS where IS_CURRENT = 1", null);
        userCursor.moveToFirst();
        String programmName = userCursor.getString(0);


        userCursor = db.query("EXERCISES_LIST", null, "DAY = ? AND PROGRAMM_NAME = ?", new String[] {String.valueOf(day), programmName}, null, null, null);

        final List<String> exercises = new ArrayList<>();
        List<String> exercisesInfo = new ArrayList<>();
        List<String> relaxTimeInfo = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            do {
                exercises.add(userCursor.getString(userCursor.getColumnIndex("EXERCISE")));
                exercisesInfo.add("Подходов: " + userCursor.getString(userCursor.getColumnIndex("ATTEMPTS"))  +
                 "   Повторений: " + userCursor.getString(userCursor.getColumnIndex("REPEATS")));
                relaxTimeInfo.add("Отдых: " + userCursor.getInt(userCursor.getColumnIndex("RELAX_TIME"))/60 + " мин");
            } while (userCursor.moveToNext());
        }

        userCursor.close();
        db.close();

        trainingCompletionPercent = progress; //need to get progress from db

        TextView startLabel = (TextView) findViewById(R.id.startLabel);

        exercisesList = (ListView) findViewById(R.id.exercises);

        final TextView trainingLabel = (TextView) findViewById(R.id.trainingId);
        trainingLabel.setText("Тренировка: " + trainingId);

        // упаковываем данные в понятную для адаптера структуру
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(exercises.size());
        Map<String, Object> m;
        for (int i = 0; i < exercises.size(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME, exercises.get(i));
            m.put(ATTRIBUTE_NAME_INFO, exercisesInfo.get(i));
            m.put(ATTRIBUTE_NAME_RELAX, relaxTimeInfo.get(i));
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME,ATTRIBUTE_NAME_INFO, ATTRIBUTE_NAME_RELAX};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.title, R.id.time, R.id.info };

        // создаем адаптер
        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.list_row_exercises, from, to);
        exercisesList.setAdapter(sAdapter);

        startLabel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(SelectedTraining.this, Exercise.class);
                    startActivity(intent);
                }
            });


        AdapterView.OnItemClickListener mOnListClick = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //get exercise name according to position
                String exerciseName = exercises.get(position);
                final Dialog dialog = new Dialog(SelectedTraining.this);
                dialog.setContentView(R.layout.modal_exercise_info);
                dialog.setCancelable(true);

                //set up text
                TextView info = (TextView) dialog.findViewById(R.id.info);


                db = databaseHelper.open();
                userCursor = db.rawQuery("select * from EXERCISES_INFO where NAME = ?", new String[] {String.valueOf(exerciseName)}, null);

                if(userCursor.getCount() == 0){return;}

                userCursor.moveToFirst();
                String description = userCursor.getString(2);
                String advice = userCursor.getString(3);
                final String video = userCursor.getString(4);

                userCursor.close();
                db.close();



                info.setText(description + "\n" + advice);

                TextView exerciseLabel = (TextView) dialog.findViewById(R.id.exercise_label);
                exerciseLabel.setText(exerciseName);


                ImageButton youtubeBtn = (ImageButton) dialog.findViewById(R.id.youtubeButton);
                if (video == null) {
                    youtubeBtn.setVisibility(View.GONE);
                }
                youtubeBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video)));
                    }
                });


                //set up button
                TextView button = (TextView) dialog.findViewById(R.id.Button01);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
            
            };

        exercisesList.setOnItemClickListener(mOnListClick);
        }

    @Override
    public void onBackPressed() {

        Intent intent;
        boolean isOpenedFromMenu = getIntent().getBooleanExtra("openedFromMenu",false);
        if(isOpenedFromMenu){
            intent = new Intent(SelectedTraining.this, TrainingsCalendar.class);
        }
        else {
            intent = new Intent(SelectedTraining.this, TrainingsList.class);
            intent.putExtra("openedFromPrograms", false);
        }

        startActivity(intent);

    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.startLabel);
                        if(txt.getText() == "Нажмите чтобы начать"){
                            txt.setText("");
                        }else{
                            txt.setText("Нажмите чтобы начать");
                        }
                        blink();
                    }
                });
            }
        }).start();
    }


}


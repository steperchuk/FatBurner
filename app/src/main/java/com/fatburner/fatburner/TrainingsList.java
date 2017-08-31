package com.fatburner.fatburner;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.fatburner.fatburner.GlobalVariables.LOAD_ARRAY;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_ID;


public class TrainingsList extends Menu {

    Switch switchCompleted;
    boolean hideCompleted = true;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    static final String TABLE = "TRAININGS";
    static final String COLUMN_PROGRAMM_NAME = "PROGRAMM_NAME";
    static final String COLUMN_TRAINING_ID = "TRAINING_ID";
    static final String COLUMN_DAY = "DAY";
    static final String COLUMN_PROGRESS = "PROGRESS";
    static final String COLUMN_EXPECTED_TIME = "EXPECTED_TIME";
    static final String COLUMN_IS_CURRENT = "IS_CURRENT";


    // имена атрибутов для Map
    final String ATTRIBUTE_NAME_TITLE = "title";
    final String ATTRIBUTE_NAME_INFO = "info";
    final String ATTRIBUTE_NAME_PROGRESS = "progress";
    final String ATTRIBUTE_NAME_PB = "pb";
    final String ATTRIBUTE_ICON = "icon";

    ListView trainings_list;

    List<Integer> trainings;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_trainings_list, null, false);
        mDrawerLayout.addView(contentView, 0);

        switchCompleted = (Switch) findViewById(R.id.hideCompleted);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        hideCompleted = sp.getBoolean("hideCompleted", true);
        switchCompleted.setChecked(hideCompleted);

        loadList();

        switchCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hideCompleted = isChecked;

                SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                sp.putBoolean("hideCompleted", hideCompleted);
                sp.commit();

                loadList();
            }
        });

        AdapterView.OnItemClickListener mOnListClick = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                databaseHelper.getWritableDatabase();
                db = databaseHelper.open();


                ContentValues cv = new ContentValues();
                int selectedTraining = trainings.get(position);
                cv.put("IS_CURRENT", 0);
                db.update(TABLE, cv, null, null);
                cv.put("IS_CURRENT", 1);
                db.update(TABLE, cv, COLUMN_TRAINING_ID + " = ? and " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{String.valueOf(selectedTraining), String.valueOf(getCurrentProgramm())});
                cv = new ContentValues();
                db = databaseHelper.open();
                cv.put("CURRENT_TRAINING", selectedTraining);
                db.update("TRAINING_SETTINGS",cv,null,null);

                cv = new ContentValues();
                cv.put("TRAINING_NAME", selectedTraining);
                db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});

                db.close();
                databaseHelper.close();

                if(position != -1) {
                    Intent intent;
                    intent = new Intent(TrainingsList.this, SelectedTraining.class);
                    startActivity(intent);
                }
            }
        };

        trainings_list.setOnItemClickListener(mOnListClick);

    }


    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        loadList();
    }



    private void loadList(){
        ///Work with DB
        // открываем подключение
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        //DatabaseHelper.TABLE = "TRAININGS";

        // массив данных
        if(hideCompleted)
        {
            userCursor = db.query(TABLE, null, COLUMN_PROGRAMM_NAME + " = ?" + "AND PROGRESS != ?",  new String[]{getCurrentProgramm(), "100"}, null, null, null);
        }
        else
        {
            userCursor = db.rawQuery("select * from " + TABLE + " where " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{getCurrentProgramm()});
        }

        trainings = new ArrayList<>();
        final List<Integer> load = new ArrayList<>();
        List<String> info = new ArrayList<>();
        List<Integer> isCurrent = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            do {
                trainings.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_TRAINING_ID)));
                load.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_PROGRESS)));
                info.add("Время выполнения:" + userCursor.getInt(userCursor.getColumnIndex(COLUMN_EXPECTED_TIME)));
                isCurrent.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_IS_CURRENT)));
            } while (userCursor.moveToNext());
        }

        List<Integer> allTrainings = new ArrayList<>();
        List<Integer> allLoad = new ArrayList<>();
        db = databaseHelper.open();
        userCursor = db.rawQuery("select * from " + TABLE + " where " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{getCurrentProgramm()});
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
            float programmCompletion = percentValue * 100;
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

        // упаковываем данные в понятную для адаптера структуру
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(trainings.size());
        Map<String, Object> m;
        for (int i = 0; i < trainings.size(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_TITLE, "Тренировка: " + trainings.get(i));
            m.put(ATTRIBUTE_NAME_INFO, info.get(i) + " мин");
            m.put(ATTRIBUTE_NAME_PROGRESS, load.get(i) + "%");
            m.put(ATTRIBUTE_NAME_PB, load.get(i));
            if (load.get(i) == 100) {
                m.put(ATTRIBUTE_ICON, R.drawable.ic_trophy);
            } else if (isCurrent.get(i) == 1) {
                m.put(ATTRIBUTE_ICON, R.drawable.ic_done);
            } else {
                if (load.get(i) != 100) {
                    m.put(ATTRIBUTE_ICON, R.drawable.ic_dumbbell);
                }
            }
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME_TITLE,ATTRIBUTE_NAME_INFO, ATTRIBUTE_NAME_PROGRESS, ATTRIBUTE_NAME_PB, ATTRIBUTE_ICON };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.title, R.id.info, R.id.progress, R.id.pbLoad, R.id.list_image };

        // создаем адаптер
        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.list_row, from, to);
        // Указываем адаптеру свой биндер
        sAdapter.setViewBinder(new TrainingsList.MyViewBinder());

        // определяем список и присваиваем ему адаптер
        trainings_list = (ListView) findViewById(R.id.trainings_list);
        trainings_list.setAdapter(sAdapter);
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


    class MyViewBinder implements SimpleAdapter.ViewBinder {

        int red = getResources().getColor(R.color.Red);
        int orange = getResources().getColor(R.color.Orange);
        int green = getResources().getColor(R.color.Green);
        int white = getResources().getColor(R.color.White);

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            int i = 0;
            switch (view.getId()) {
                // ProgressBar 
                case R.id.pbLoad:
                    i = ((Integer) data).intValue();
                    ((ProgressBar)view).setProgress(i);

                    if (i < 40) ((ProgressBar)view).getProgressDrawable().setColorFilter(red, PorterDuff.Mode.MULTIPLY); else
                    if (i < 70) ((ProgressBar)view).getProgressDrawable().setColorFilter(orange, PorterDuff.Mode.MULTIPLY); else
                        ((ProgressBar)view).getProgressDrawable().setColorFilter(green, PorterDuff.Mode.MULTIPLY);
                    return true;
            }
            return false;
        }
    }

}

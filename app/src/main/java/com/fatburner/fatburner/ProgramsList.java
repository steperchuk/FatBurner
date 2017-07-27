package com.fatburner.fatburner;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramsList extends Menu {


    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    List<String> programs;

    // имена атрибутов для Map
    final String ATTRIBUTE_NAME_TITLE = "title";
    final String ATTRIBUTE_NAME_INFO = "info";
    final String ATTRIBUTE_NAME_PROGRESS = "progress";
    final String ATTRIBUTE_NAME_PB = "pb";
    final String ATTRIBUTE_ICON = "icon";

    ListView programms_list;
    Switch switchRecommended;
    boolean showRecommended = true;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_my_programs, null, false);
        mDrawerLayout.addView(contentView, 0);


        switchRecommended = (Switch) findViewById(R.id.isRecommended);
        switchRecommended.setChecked(true);

        ///Work with DB
        // открываем подключение
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();



        databaseHelper.TABLE = "PROGRAMMS";

        fillList();


        switchRecommended.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showRecommended = isChecked;
                fillList();
            }
        });


          AdapterView.OnItemClickListener mOnListClick = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                databaseHelper.getWritableDatabase();
                db = databaseHelper.open();

                ContentValues cv = new ContentValues();
                String selectedProgramm = programs.get(position);
                cv.put(DatabaseHelper.COLUMN_IS_CURRENT, 0);
                db.update(DatabaseHelper.TABLE, cv, null, null);
                cv.put(DatabaseHelper.COLUMN_IS_CURRENT, 1);
                db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_NAME + " = ?" , new String[]{selectedProgramm});
                cv = new ContentValues();
                cv.put("CURRENT_PROGRAMM", selectedProgramm);
                db.update("TRAINING_SETTINGS",cv,null,null);

                cv = new ContentValues();
                cv.put("PROGRAMM_NAME", selectedProgramm);
                db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});

                db.close();
                databaseHelper.close();




                Intent intent;
                intent = new Intent(ProgramsList.this, TrainingsList.class);
                startActivity(intent);
            }
        };

        programms_list.setOnItemClickListener(mOnListClick);


    }

    void fillList(){
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        if(showRecommended)
        {
            userCursor =  db.rawQuery("select * from APP_SETTINGS", null);
            userCursor.moveToFirst();
            Integer gender = userCursor.getInt(1);
            Integer goalValue = userCursor.getInt(2);
            Integer difficultyValue = userCursor.getInt(3);
            String goal = Utils.parseGoalValue(goalValue);
            String difficulty = Utils.parseDifficultyValue(difficultyValue);

            userCursor = db.query(DatabaseHelper.TABLE, null, "GENDER is null AND GOAL = ? AND DIFFICULTY = ? OR GENDER = ? AND GOAL = ? AND DIFFICULTY = ? OR IS_CURRENT = ?", new String[] {String.valueOf(goal), String.valueOf(difficulty), String.valueOf(gender), String.valueOf(goal), String.valueOf(difficulty), String.valueOf(1)}, null, null, "IS_CURRENT desc");

        }
        else
        {
            userCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE + " ORDER BY IS_CURRENT desc", null);
        }


        programs = new ArrayList<>();
        List<Integer> load = new ArrayList<>();
        List<String> info = new ArrayList<>();
        List<Integer> isCurrent = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            do {
                programs.add(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)) );
                load.add(userCursor.getInt(userCursor.getColumnIndex(DatabaseHelper.COLUMN_COMPLETION_STATUS)));
                info.add("Недели: " + userCursor.getInt(userCursor.getColumnIndex(DatabaseHelper.COLUMN_WEEKS)) +
                        "  Дней в неделю: " + String.valueOf(userCursor.getInt(userCursor.getColumnIndex(DatabaseHelper.COLUMN_DAYS_PER_WEEK))) +
                        "  Время: " + userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_HOURS)));
                isCurrent.add(userCursor.getInt(userCursor.getColumnIndex(DatabaseHelper.COLUMN_IS_CURRENT)) );
            } while (userCursor.moveToNext());
        }

        userCursor.close();
        db.close();

        // упаковываем данные в понятную для адаптера структуру
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(load.size());
        Map<String, Object> m;
        for (int i = 0; i < programs.size(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_TITLE, programs.get(i));
            m.put(ATTRIBUTE_NAME_INFO, info.get(i));
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
        String[] from = { ATTRIBUTE_NAME_TITLE,ATTRIBUTE_NAME_INFO, ATTRIBUTE_NAME_PROGRESS, ATTRIBUTE_NAME_PB, ATTRIBUTE_ICON};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.title, R.id.info, R.id.progress, R.id.pbLoad, R.id.list_image};

        // создаем адаптер
        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.list_row, from, to);
        // Указываем адаптеру свой биндер
        sAdapter.setViewBinder(new MyViewBinder());

        // определяем список и присваиваем ему адаптер
        programms_list = (ListView) findViewById(R.id.programms_list);
        programms_list.setAdapter(sAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        fillList();
    }


    class MyViewBinder implements SimpleAdapter.ViewBinder {

        int red = getResources().getColor(R.color.Red);
        int orange = getResources().getColor(R.color.Orange);
        int green = getResources().getColor(R.color.Green);


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
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


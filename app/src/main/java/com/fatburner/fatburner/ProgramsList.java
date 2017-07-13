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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_my_programs, null, false);
        mDrawerLayout.addView(contentView, 0);




        ///Work with DB
        // открываем подключение
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();



        databaseHelper.TABLE = "PROGRAMMS";
        //получаем данные из бд в виде курсора

        userCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE, null);


        programs = new ArrayList<>();
        List<Integer> load = new ArrayList<>();
        List<String> info = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            do {
                programs.add(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)) );
                load.add(userCursor.getInt(userCursor.getColumnIndex(DatabaseHelper.COLUMN_COMPLETION_STATUS)));
                info.add("Недели: " + userCursor.getInt(userCursor.getColumnIndex(DatabaseHelper.COLUMN_WEEKS)) +
                        "  Дней в неделю: " + String.valueOf(userCursor.getInt(userCursor.getColumnIndex(DatabaseHelper.COLUMN_DAYS_PER_WEEK))) +
                        "  Время: " + userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_HOURS)));
            } while (userCursor.moveToNext());
        }

        userCursor.close();

        // упаковываем данные в понятную для адаптера структуру
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(load.size());
        Map<String, Object> m;
        for (int i = 0; i < programs.size(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_TITLE, programs.get(i));
            m.put(ATTRIBUTE_NAME_INFO, info.get(i));
            m.put(ATTRIBUTE_NAME_PROGRESS, load.get(i) + "%");
            m.put(ATTRIBUTE_NAME_PB, load.get(i));
            if(load.get(i) != 100){m.put(ATTRIBUTE_ICON, R.drawable.ic_dumbbell);}
            else{m.put(ATTRIBUTE_ICON, R.drawable.ic_trophy);}
            //need to add case for current training
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

          AdapterView.OnItemClickListener mOnListClick = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ContentValues cv = new ContentValues();
                String selectedProgramm = programs.get(position);
                cv.put(DatabaseHelper.COLUMN_IS_CURRENT, 0);
                db.update(DatabaseHelper.TABLE, cv, null, null);
                cv.put(DatabaseHelper.COLUMN_IS_CURRENT, 1);
                db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_NAME + " = ?" , new String[]{selectedProgramm});

                db.close();
                databaseHelper.close();
                Intent intent;
                intent = new Intent(ProgramsList.this, TrainingsList.class);
                startActivity(intent);
            }
        };

        programms_list.setOnItemClickListener(mOnListClick);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
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


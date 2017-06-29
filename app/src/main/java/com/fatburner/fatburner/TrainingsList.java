package com.fatburner.fatburner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fatburner.fatburner.GlobalVariables.LOAD_ARRAY;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_ID;


public class TrainingsList extends Menu {


    // имена атрибутов для Map
    final String ATTRIBUTE_NAME_TITLE = "title";
    final String ATTRIBUTE_NAME_INFO = "info";
    final String ATTRIBUTE_NAME_PROGRESS = "progress";
    final String ATTRIBUTE_NAME_PB = "pb";

    ListView trainings_list;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_trainings_list, null, false);
        mDrawerLayout.addView(contentView, 0);


        Intent intent = getIntent();
        String selectedProgram = intent.getStringExtra("selectedProgram");
        //add switch for selected programm and fill array according to selectedProgram

        // массив данных
        final int load[] = { 100, 10 };
        String info[] = {"2 недели", "2 дня"};

        // упаковываем данные в понятную для адаптера структуру
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(load.length);
        Map<String, Object> m;
        for (int i = 0; i < load.length; i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_TITLE, "Тренировка: " + (i+1));
            m.put(ATTRIBUTE_NAME_INFO, info[i]);
            m.put(ATTRIBUTE_NAME_PROGRESS, load[i] + "%");
            m.put(ATTRIBUTE_NAME_PB, load[i]);
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME_TITLE,ATTRIBUTE_NAME_INFO, ATTRIBUTE_NAME_PROGRESS, ATTRIBUTE_NAME_PB };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.title, R.id.info, R.id.progress, R.id.pbLoad };

        // создаем адаптер
        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.list_row, from, to);
        // Указываем адаптеру свой биндер
        sAdapter.setViewBinder(new TrainingsList.MyViewBinder());

        // определяем список и присваиваем ему адаптер
        trainings_list = (ListView) findViewById(R.id.trainings_list);
        trainings_list.setAdapter(sAdapter);


        AdapterView.OnItemClickListener mOnListClick = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position != -1) {
                    Intent intent;
                    intent = new Intent(TrainingsList.this, SelectedTraining.class);
                    TRAINING_ID = position;
                    LOAD_ARRAY = load;
                    //intent.putExtra("selectedTraining", Integer.toString(position));
                    //intent.putExtra("percentCompleted", load);
                    startActivity(intent);
                }
            }
        };

        trainings_list.setOnItemClickListener(mOnListClick);


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

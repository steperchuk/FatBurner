package com.fatburner.fatburner;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProgramsList extends Menu {

    // имена атрибутов для Map
    final String ATTRIBUTE_NAME_TITLE = "title";
    final String ATTRIBUTE_NAME_INFO = "info";
    final String ATTRIBUTE_NAME_PROGRESS = "progress";
    final String ATTRIBUTE_NAME_PB = "pb";

    ListView programms_list;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_my_programs, null, false);
        mDrawerLayout.addView(contentView, 0);



        // массив данных
        String programs[] = {"Fat burn", "Gym training"};
        String info[] = {"2 недели", "2 дня"};

        int load[] = { 12,  88 };

        // упаковываем данные в понятную для адаптера структуру
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(load.length);
        Map<String, Object> m;
        for (int i = 0; i < programs.length; i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_TITLE, programs[i]);
            m.put(ATTRIBUTE_NAME_INFO, info[i]);
            m.put(ATTRIBUTE_NAME_PROGRESS, load[i] + "%");
            m.put(ATTRIBUTE_NAME_PB, load[i]);
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME_TITLE,ATTRIBUTE_NAME_INFO, ATTRIBUTE_NAME_PROGRESS, ATTRIBUTE_NAME_PB};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.title, R.id.info, R.id.progress, R.id.pbLoad};

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

                Intent intent;
                switch(position){
                    case 0:
                        GlobalVariables.selectedProgram = "Home fat burner";
                        intent = new Intent(ProgramsList.this, TrainingsList.class);
                        intent.putExtra("selectedProgram", "Home fat burner");
                        startActivity(intent);
                        break;
                    case 1:
                        GlobalVariables.selectedProgram = "Gym fat burner";
                        intent = new Intent(ProgramsList.this, TrainingsList.class);
                        intent.putExtra("selectedProgram", "Gym fat burner");
                        startActivity(intent);
                        break;
                }

            }
        };

        programms_list.setOnItemClickListener(mOnListClick);


    }

    class MyViewBinder implements SimpleAdapter.ViewBinder {

        int red = getResources().getColor(R.color.Red);
        int orange = getResources().getColor(R.color.Orange);
        int green = getResources().getColor(R.color.Green);
        int white = getResources().getColor(R.color.White);

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


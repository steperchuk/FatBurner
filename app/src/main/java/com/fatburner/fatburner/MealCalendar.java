package com.fatburner.fatburner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import static com.fatburner.fatburner.MealCalendar.PAGE_COUNT;

public class MealCalendar extends FragmentActivity {

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 7;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MealCalendarPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(Utils.getCurrentDayID()-1);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean show = sp.getBoolean("showMealAdvice", true);
        if(show) {
            ShowInfoDialog();
        }

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //alertDialogBuilder.setTitle("Выйти из приложения?");
        alertDialogBuilder
                .setMessage("Вы уверены что сохранили все настройки диеты?")
                .setCancelable(false)
                .setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(MealCalendar.this, TrainingsCalendar.class);
                                startActivity(intent);
                            }
                        })

                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void ShowInfoDialog(){
        final Dialog dialog = new Dialog(MealCalendar.this);
        dialog.setContentView(R.layout.modal_advice);
        dialog.setCancelable(true);

        CheckBox checkBoxSave = (CheckBox) dialog.findViewById(R.id.checkBoxSave);
        checkBoxSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
            if (isChecked){
                SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                sp.putBoolean("showMealAdvice", false);
                sp.commit();
            }
        }
        });


        TextView exerciseLabel = (TextView) dialog.findViewById(R.id.label);
        exerciseLabel.setText("1. Вам необходимо сохранить настройки для каждого дня по отдельности.\n\n" +
                "2. Необходимый список продуктов будет отображен исходя из выбраной вами формулы.\n\n" +
                "3. Для выбора будут доступны только продукты из соответсвующих категорий.\n");

        //set up button
        Button button = (Button) dialog.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }


}

    class MealCalendarPagerAdapter extends FragmentPagerAdapter {

        public MealCalendarPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String day = "";
            switch (position){
                case 0:
                    day = "Понедельник";
                    break;
                case 1:
                    day = "Вторник";
                    break;
                case 2:
                    day = "Среда";
                    break;
                case 3:
                    day = "Четверг";
                    break;
                case 4:
                    day = "Пятница";
                    break;
                case 5:
                    day = "Суббота";
                    break;
                case 6:
                    day = "Воскресенье";
                    break;
            }
            return day;
        }

        @Override
        public Fragment getItem(int position) {
            return MealCalendarPageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }

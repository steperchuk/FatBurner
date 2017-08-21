package com.fatburner.fatburner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

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

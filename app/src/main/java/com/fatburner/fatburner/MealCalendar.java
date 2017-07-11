package com.fatburner.fatburner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

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

package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class MealCalendarPageFragment extends Fragment {

    List<String[]> mealCalendar;
    String[] mon = {"01:00","01:00","01:00","01:00","01:00"};
    String[] thu = {"02:00","02:00","02:00","02:00","02:00"};
    String[] wen = {"03:00","03:00","03:00","03:00","03:00"};
    String[] the = {"04:00","04:00","04:00","04:00","04:00"};
    String[] fri = {"05:00","05:00","05:00","05:00","05:00"};
    String[] sat = {"06:00","06:00","06:00","06:00","06:00"};
    String[] sun = {"07:00","07:00","07:00","07:00","07:00"};

    TextClock breakfestTime;
    TextClock secondBreakfestTime;
    TextClock lunchTime;
    TextClock secondLunchTime;
    TextClock dinnerTime;

    ImageButton applyButton;


    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";

    int pageNumber;

    static MealCalendarPageFragment newInstance(int page) {
        MealCalendarPageFragment pageFragment = new MealCalendarPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mealCalendar.add(mon);
        mealCalendar.add(thu);
        mealCalendar.add(wen);
        mealCalendar.add(the);
        mealCalendar.add(fri);
        mealCalendar.add(sat);
        mealCalendar.add(sun);

        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        int savedPageNumber = -1;
        if (savedInstanceState != null) {
            savedPageNumber = savedInstanceState.getInt(SAVE_PAGE_NUMBER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);

        applyButton = (ImageButton) view.findViewById(R.id.applyButton);

        breakfestTime = (TextClock) view.findViewById(R.id.breakfestTime);
        secondBreakfestTime = (TextClock) view.findViewById(R.id.secondBreakfestTime);
        lunchTime = (TextClock) view.findViewById(R.id.lunchTime);
        secondLunchTime = (TextClock) view.findViewById(R.id.secondLunchTime);
        dinnerTime = (TextClock) view.findViewById(R.id.dinnerTime);

        TextView tvPage = (TextView) view.findViewById(R.id.tvPage);
        tvPage.setBackgroundColor(Color.WHITE);

        setMealCalendar(pageNumber);



        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                putMealCalendar(pageNumber);
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PAGE_NUMBER, pageNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setMealCalendar(int pageNumber){
        breakfestTime.setText(mealCalendar.get(pageNumber-1)[0]);
        secondBreakfestTime.setText(mealCalendar.get(pageNumber-1)[1]);
        lunchTime.setText(mealCalendar.get(pageNumber-1)[2]);
        secondLunchTime.setText(mealCalendar.get(pageNumber-1)[3]);
        dinnerTime.setText(mealCalendar.get(pageNumber-1)[4]);
    }

    private void putMealCalendar(int pageNumber){
        String tmpArr[] = { breakfestTime.getText().toString(),
                secondBreakfestTime.getText().toString(),
                lunchTime.getText().toString(),
                secondLunchTime.getText().toString(),
                dinnerTime.getText().toString()};

        mealCalendar.set(pageNumber-1,tmpArr);
    }



}

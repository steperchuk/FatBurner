package com.fatburner.fatburner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

/**
 * Created by sete on 6/19/2017.
 */

public class TrainingsCalendar extends Menu {

    TextView trainingStatusLabel;
    TextView dietStatusLabel;
    TextView waterStatusLabel;

    CalendarView trainingCalendarView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_trainings_calendar, null, false);
        mDrawerLayout.addView(contentView, 0);

        trainingStatusLabel = (TextView) findViewById(R.id.trainingStatusLabel);
        dietStatusLabel = (TextView) findViewById(R.id.dietStatusLabel);
        waterStatusLabel = (TextView) findViewById(R.id.waterStatusLabel);

        trainingCalendarView = (CalendarView) findViewById(R.id.trainingCalendarView);


         



    }
}

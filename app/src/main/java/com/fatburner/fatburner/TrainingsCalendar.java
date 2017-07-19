package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by sete on 6/19/2017.
 */

public class TrainingsCalendar extends Menu implements OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    DonutProgress startButton;

    TextView trainingStatusLabel;
    TextView dietStatusLabel;
    TextView waterStatusLabel;
    MaterialCalendarView mCalendarView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_trainings_calendar, null, false);
        mDrawerLayout.addView(contentView, 0);

        trainingStatusLabel = (TextView) findViewById(R.id.trainingStatusLabel);
        dietStatusLabel = (TextView) findViewById(R.id.dietStatusLabel);
        waterStatusLabel = (TextView) findViewById(R.id.waterStatusLabel);

        mCalendarView = (MaterialCalendarView) findViewById(R.id.trainingCalendarView);

        ArrayList<CalendarDay> greenDates = new ArrayList<>();
        greenDates.add(CalendarDay.from(2017,6,10));

        ArrayList<CalendarDay> yellowDates = new ArrayList<>();
        yellowDates.add(CalendarDay.from(2017,6,14));

        ArrayList<CalendarDay> redDates = new ArrayList<>();
        redDates.add(CalendarDay.from(2017,6,12));

        mCalendarView.addDecorator(new CircleDecorator(getApplicationContext(), R.drawable.calendar_date_green, greenDates));
        mCalendarView.addDecorator(new CircleDecorator(getApplicationContext(), R.drawable.calendar_date_yellow, yellowDates));
        mCalendarView.addDecorator(new CircleDecorator(getApplicationContext(), R.drawable.calendar_date_red, redDates));

        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.setOnMonthChangedListener(this);

        mCalendarView.setSelectedDate(CalendarDay.today());

        trainingStatusLabel.setText(getSelectedDatesString());


        ImageButton playerButton = (ImageButton) findViewById(R.id.playerButton);

        startButton = (DonutProgress) findViewById(R.id.start_btn);
        startButton.setText("Начать");
        startButton.setFinishedStrokeWidth(15);
        startButton.setUnfinishedStrokeWidth(15);
        startAnimation();

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TrainingsCalendar.this, Exercise.class);
                if(currentTraining != "") {
                    startActivity(intent);
                }
                else{
                    ModalDialogNotSelected dialog = new ModalDialogNotSelected();
                    dialog.show(getSupportFragmentManager(), "custom");
                    return;
                }
            }
        });


        playerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


    }

    void startAnimation ( ) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(TrainingsCalendar.this, R.animator.progress_anim);
        set.setInterpolator(new DecelerateInterpolator());
        set.setTarget(startButton);
        set.start();

    }



    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        trainingStatusLabel.setText(getSelectedDatesString());
    }


    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {
        CalendarDay date = mCalendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }

}


class CircleDecorator implements DayViewDecorator {

    private HashSet<CalendarDay> dates;
    private Drawable drawable;

    public CircleDecorator(Context context, int resId, Collection<CalendarDay> dates) {
        drawable = ContextCompat.getDrawable(context, resId);
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
        view.setSelectionDrawable(drawable);
    }
}

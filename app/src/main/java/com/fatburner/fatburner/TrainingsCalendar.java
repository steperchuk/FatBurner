package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.fatburner.fatburner.TrainingsList.COLUMN_PROGRAMM_NAME;
import static com.fatburner.fatburner.TrainingsList.COLUMN_PROGRESS;
import static com.fatburner.fatburner.TrainingsList.COLUMN_TRAINING_ID;

/**
 * Created by sete on 6/19/2017.
 */

public class TrainingsCalendar extends Menu implements OnDateSelectedListener, OnMonthChangedListener {

    static final String COLUMN_COMPLETION_STATUS = "COMPLETION_STATUS";
    static final String COLUMN_IS_CURRENT = "IS_CURRENT";

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    String selectedDay = "";
    String programName = "Нет данных";
    String programStatus = "0";
    String trainingName = "Не данных";
    String trainingStatus = "0";
    String waterStatus = "0";
    String dietStatus = "выкл";


    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    DonutProgress startButton;
    MaterialCalendarView mCalendarView;

    ArrayList<CalendarDay> greenDates = new ArrayList<>();
    ArrayList<CalendarDay> yellowDates = new ArrayList<>();
    ArrayList<CalendarDay> redDates = new ArrayList<>();
    ArrayList<CalendarDay> grayDates = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_trainings_calendar, null, false);
        mDrawerLayout.addView(contentView, 0);

        mCalendarView = (MaterialCalendarView) findViewById(R.id.trainingCalendarView);

        fillDayColors();

        mCalendarView.addDecorator(new CircleDecorator(getApplicationContext(), R.drawable.calendar_date_gray, grayDates));
        mCalendarView.addDecorator(new CircleDecorator(getApplicationContext(), R.drawable.calendar_date_green, greenDates));
        mCalendarView.addDecorator(new CircleDecorator(getApplicationContext(), R.drawable.calendar_date_yellow, yellowDates));
        mCalendarView.addDecorator(new CircleDecorator(getApplicationContext(), R.drawable.calendar_date_red, redDates));

        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.setOnMonthChangedListener(this);

        mCalendarView.setSelectedDate(CalendarDay.today());

        LoadProgress();

        SharedPreferences value = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastTrainingDate = value.getString("currentDay", "");
        int lastTrainingProgress = loadProgressForLastTrainig();
        int currentTrainingID = getCurrentTrainingID();


        if(!lastTrainingDate.equals(Utils.getCurrentDate()) &&  lastTrainingProgress == 100 &&  currentTrainingID !=0)
        {
            selectNextTraining();
        }

        if(!lastTrainingDate.equals(Utils.getCurrentDate()) &&  lastTrainingProgress != 100 &&  currentTrainingID !=0)
        {
            selectNextSameTraining();
        }

        SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        sp.putString("currentDay", Utils.getCurrentDate());
        sp.commit();

        mCalendarView.setSelectedDate(CalendarDay.today());

        /*
        List<String> info = loadProgressForSpecificDay(getSelectedDatesString());
        trainingStatusLabel.setText(" - " + info.get(0) + "%");
        if(info.get(1).equals("выкл"))
        {
            dietStatusLabel.setText(" - " + info.get(1));
        }
        else
            {
                dietStatusLabel.setText(" - " + info.get(1) +"%");
            }
        waterStatusLabel.setText(" - " + info.get(2) + "%");
        clearChash();
        */

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
                try {
                    Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                catch (Exception e){
                    ModalDialogUnableOpenPlayer dialog = new ModalDialogUnableOpenPlayer();
                    dialog.show(getSupportFragmentManager(), "custom");
                }
            }
        });

        /*
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(TrainingsCalendar.this);
                dialog.setContentView(R.layout.modal_info);
                dialog.setCancelable(true);

                clearChash();
                loadProgressForSpecificDay(getSelectedDatesString());

                //set up text
                TextView date = (TextView) dialog.findViewById(R.id.date);
                List<Integer> dateForIncrement = Utils.normalizeDateForColoring(getSelectedDatesString());
                String dateToShow = dateForIncrement.get(0) + "-" + (dateForIncrement.get(1)+1) + "-" + dateForIncrement.get(2);
                date.setText(dateToShow);

                TextView programNameLabel = (TextView) dialog.findViewById(R.id.programName);
                programNameLabel.setText(programName);

                TextView programProgressLabel = (TextView) dialog.findViewById(R.id.programStatus);
                programProgressLabel.setText(Math.round(Float.valueOf(programStatus)) + "%");

                TextView trainingNameLabel = (TextView) dialog.findViewById(R.id.trainingName);
                trainingNameLabel.setText(trainingName);

                TextView trainingProgressLabel = (TextView) dialog.findViewById(R.id.trainingStatus);
                trainingProgressLabel.setText(trainingStatus + "%");

                TextView waterStatusLabel = (TextView) dialog.findViewById(R.id.waterStatus);
                waterStatusLabel.setText(waterStatus + "%");

                TextView dietStatusLabel = (TextView) dialog.findViewById(R.id.dietStatus);
                if(dietStatus.equals("выкл")){dietStatusLabel.setText(dietStatus);}
                else{dietStatusLabel.setText(dietStatus + "%");}

                //set up button
                ImageButton button = (ImageButton) dialog.findViewById(R.id.Button01);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                        clearChash();
                    }
                });

                dialog.show();

            }
        });

        */


    }


    @Override
    public void onResume(){
        super.onResume();

        LoadProgress();
        
        SharedPreferences value = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastTrainingDate = value.getString("currentDay", "");
        int lastTrainingProgress = loadProgressForLastTrainig();
        int currentTrainingID = getCurrentTrainingID();


        if(!lastTrainingDate.equals(Utils.getCurrentDate()) &&  lastTrainingProgress == 100 &&  currentTrainingID !=0)
        {
            selectNextTraining();
        }

        if(!lastTrainingDate.equals(Utils.getCurrentDate()) &&  lastTrainingProgress != 100 &&  currentTrainingID !=0)
        {
            selectNextSameTraining();
        }

        SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        sp.putString("currentDay", Utils.getCurrentDate());
        sp.commit();

        mCalendarView.setSelectedDate(CalendarDay.today());
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Выйти из приложения?");
        alertDialogBuilder
                .setMessage("Нажмите 'Да' для выхода!")
                .setCancelable(false)
                .setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                homeIntent.addCategory( Intent.CATEGORY_HOME );
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);

                                /* code to kill app can be used instead of above
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                                */
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



    void startAnimation ( ) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(TrainingsCalendar.this, R.animator.progress_anim);
        set.setInterpolator(new DecelerateInterpolator());
        set.setTarget(startButton);
        set.start();

    }



    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        LoadProgress();
    }


    private void LoadProgress(){
        clearChash();
        loadProgressForSpecificDay(getSelectedDatesString());

        //set up text
        TextView dateLabel = (TextView) findViewById(R.id.date);
        List<Integer> dateForIncrement = Utils.normalizeDateForColoring(getSelectedDatesString());
        String dateToShow = dateForIncrement.get(0) + "-" + (dateForIncrement.get(1)+1) + "-" + dateForIncrement.get(2);
        dateLabel.setText(dateToShow);

        TextView programNameLabel = (TextView) findViewById(R.id.programName);
        if(programName.contains("0") || programName.isEmpty()){programName = "Нет данных";}
        programNameLabel.setText(programName);

        TextView programProgressLabel = (TextView) findViewById(R.id.programStatus);
        programProgressLabel.setText(Math.round(Float.valueOf(programStatus)) + "%");

        TextView trainingNameLabel = (TextView) findViewById(R.id.trainingName);
        if(trainingName.isEmpty()){trainingName = "Нет данных";}
        trainingNameLabel.setText(trainingName);

        TextView trainingProgressLabel = (TextView) findViewById(R.id.trainingStatus);
        trainingProgressLabel.setText(trainingStatus + "%");

        TextView waterStatusLabel = (TextView) findViewById(R.id.waterStatus);
        waterStatusLabel.setText(waterStatus + "%");

        TextView dietStatusLabel = (TextView) findViewById(R.id.dietStatus);
        if(dietStatus.equals("выкл")){dietStatusLabel.setText(dietStatus);}
        else{dietStatusLabel.setText(dietStatus + "%");}

        clearChash();
    }


    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {
        CalendarDay date = mCalendarView.getSelectedDate();
        return String.valueOf(date).replace("CalendarDay", "").replace("}","").replace("{","");
    }


    private List<String> loadProgressForSpecificDay(String day){

        selectedDay = day;
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        userCursor = db.query("CALENDAR", null, "DATE = ?", new String[] {day}, null, null, null);

        if(userCursor.getCount() != 0) {

            userCursor.moveToFirst();
            programName = userCursor.getString(1);
            programStatus = userCursor.getString(2);
            trainingName = userCursor.getString(3);
            trainingStatus = userCursor.getString(4);
            dietStatus = userCursor.getString(5);
            waterStatus = userCursor.getString(6);

            /* //commented to show statistics only for current date
            trainingStatusLabel.setText(" - " + trainingStatus + "%");
            dietStatusLabel.setText(" - " + dietStatus + "%");
            waterStatusLabel.setText(" - " + waterStatus +"%");


        }
        else
        {
            trainingStatusLabel.setText(" - 0%");
            dietStatusLabel.setText(" - 0%");
            waterStatusLabel.setText(" - 0%");
        }

        */
        }
            userCursor.close();
            db.close();


            db = databaseHelper.open();
            userCursor =  db.rawQuery("select * from APP_SETTINGS", null);
            userCursor.moveToFirst();
            if(userCursor.getInt(6) == 0)
            {
                //dietStatusLabel.setText(" - выкл");
                dietStatus = "выкл";
            }

            List<String> info = new ArrayList<>();
            info.add(trainingStatus);
            info.add(dietStatus);
            info.add(waterStatus);


        return info;
    }

    private void clearChash(){
         selectedDay = "";
         programName = "Нет данных";
         programStatus = "0";
         trainingName = "Нет данных";
         trainingStatus = "0";
         waterStatus = "0";
         dietStatus = "0";
    }

    private void selectNextTraining(){
        String currentProgram = getCurrentProgramm();
        int currentTraining = getCurrentTrainingID();
        int trainingsCount = getTrainingsCount();
        if(currentTraining == trainingsCount)
        {
            return;
        }

        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        ContentValues cv = new ContentValues();
        cv.put("IS_CURRENT", 0);
        db.update("TRAININGS", cv, null, null);

        db = databaseHelper.open();
        cv.put("IS_CURRENT", 1);
        db.update("TRAININGS", cv, COLUMN_TRAINING_ID + " = ? and " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{String.valueOf(currentTraining+1), String.valueOf(currentProgram)});

        cv = new ContentValues();
        db = databaseHelper.open();
        cv.put("CURRENT_PROGRAMM", currentProgram);
        cv.put("CURRENT_TRAINING", currentTraining+1);
        db.update("TRAINING_SETTINGS",cv,null,null);

        db = databaseHelper.open();
        cv = new ContentValues();
        cv.put("PROGRAMM_NAME", currentProgram);
        cv.put("TRAINING_NAME", currentTraining+1);
        db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});

        db.close();
        databaseHelper.close();

        SetProgramProgress();

    }

    private void selectNextSameTraining(){
        String currentProgram = getCurrentProgramm();
        int currentTraining = getCurrentTrainingID();
        int trainingsCount = getTrainingsCount();
        if(currentTraining == trainingsCount)
        {
            return;
        }

        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        ContentValues cv = new ContentValues();
        cv.put("IS_CURRENT", 0);
        db.update("TRAININGS", cv, null, null);

        db = databaseHelper.open();
        cv.put("IS_CURRENT", 1);
        db.update("TRAININGS", cv, COLUMN_TRAINING_ID + " = ? and " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{String.valueOf(currentTraining), String.valueOf(currentProgram)});

        cv = new ContentValues();
        db = databaseHelper.open();
        cv.put("CURRENT_PROGRAMM", currentProgram);
        cv.put("CURRENT_TRAINING", currentTraining);
        db.update("TRAINING_SETTINGS",cv,null,null);

        db = databaseHelper.open();
        cv = new ContentValues();
        cv.put("PROGRAMM_NAME", currentProgram);
        cv.put("TRAINING_NAME", currentTraining);
        db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});

        db.close();
        databaseHelper.close();

        SetProgramProgress();

    }

    private void SetProgramProgress(){
        ///Work with DB
        // открываем подключение
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        //DatabaseHelper.TABLE = "TRAININGS";

        List<Integer> allTrainings = new ArrayList<>();
        List<Integer> allLoad = new ArrayList<>();
        db = databaseHelper.open();
        userCursor = db.rawQuery("select * from " + "TRAININGS" + " where " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{getCurrentProgramm()});
        if (userCursor.moveToFirst()) {
            do {
                allTrainings.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_TRAINING_ID)));
                allLoad.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_PROGRESS)));
            } while (userCursor.moveToNext());
        }

        int totalTrainingsLoad = 0;
        for(int i = 0; i < allTrainings.size(); i++){
            totalTrainingsLoad = totalTrainingsLoad + allLoad.get(i);
        }

        if(totalTrainingsLoad != 0) {

            float totalTrainings = (allTrainings.size() * 100);
            float percentValue = totalTrainingsLoad / totalTrainings;
            float programmCompletion = Math.round(percentValue * 100);
            ContentValues cv = new ContentValues();

            db = databaseHelper.open();
            cv = new ContentValues();
            cv.put("PROGRAMM_STATUS", programmCompletion);
            db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});
        }

        db.close();
        userCursor.close();
    }

    private int getTrainingsCount(){

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        userCursor = db.rawQuery("select * from " + "TRAININGS" + " where " + COLUMN_PROGRAMM_NAME + " = ?", new String[]{getCurrentProgramm()});

        List<Integer> trainings = new ArrayList<>();
        List<Integer> isCurrent = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            do {
                trainings.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_TRAINING_ID)));
                isCurrent.add(userCursor.getInt(userCursor.getColumnIndex(COLUMN_IS_CURRENT)));
            } while (userCursor.moveToNext());
        }

        db.close();
        userCursor.close();

        return trainings.size();

    }

    private int getCurrentTrainingID(){

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        int trainingId = 0;

        userCursor = db.rawQuery("select TRAINING_ID from TRAININGS where IS_CURRENT = 1", null);
        if(userCursor.moveToFirst()){
            trainingId = userCursor.getInt(0);
        }

        db.close();
        userCursor.close();

        return trainingId;

    }

    private String getCurrentProgramm() {
        db = databaseHelper.open();
        userCursor = db.rawQuery("select " + DatabaseHelper.COLUMN_NAME + " from PROGRAMMS" +
                " where " + DatabaseHelper.COLUMN_IS_CURRENT + " = 1", null);

        List<String> current = new ArrayList<>();
        if (userCursor.moveToFirst()) {
            do {
                current.add(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
            } while (userCursor.moveToNext());
        }

        db.close();
        userCursor.close();
        return current.get(0);
    }

    private int loadProgressForLastTrainig(){

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        int progress = 0;

        userCursor = db.rawQuery("select PROGRESS from TRAININGS where IS_CURRENT = 1", null);
        if(userCursor.moveToFirst()){
            progress = userCursor.getInt(0);
        }

        db.close();
        userCursor.close();

        return progress;
    }


    private void fillDayColors(){

            databaseHelper = new DatabaseHelper(this);
            databaseHelper.getWritableDatabase();
            db = databaseHelper.open();

        List<String> dates = new ArrayList<>();
        List<String> statuses = new ArrayList<>();


        userCursor = db.query("CALENDAR", null, null, null, null, null, null);
        int itemsCount = userCursor.getCount();

        if (userCursor.moveToFirst()) {
            do {
                dates.add(userCursor.getString(userCursor.getColumnIndex("DATE")));
                statuses.add(userCursor.getString(userCursor.getColumnIndex("TRAINING_STATUS")));
            } while (userCursor.moveToNext());
        }

        /*
            if(itemsCount != 0) {

                userCursor.moveToFirst();
                dates.add(userCursor.getString(0));
                statuses.add(userCursor.getString(4));
            }
*/
            userCursor.close();
            db.close();


            String trainingDays = null;
            db = databaseHelper.open();
            userCursor =  db.rawQuery("select * from APP_SETTINGS", null);
            userCursor.moveToFirst();

            if(userCursor.getCount() != 0)
            {
                 trainingDays = userCursor.getString(4);
            }

        for (int i =0; i < dates.size(); i++) {

            String day = Utils.getSpecifiedDayName(dates.get(i));
            if (trainingDays.contains(day)) {

                int status = Integer.parseInt(statuses.get(i));
                List<Integer> normalizedDate = Utils.normalizeDateForColoring(dates.get(i));
                int currentDay = Utils.getParsedDate("day");

                if (normalizedDate.get(2) > currentDay) {
                    grayDates.add(CalendarDay.from(normalizedDate.get(0), normalizedDate.get(1), normalizedDate.get(2)));
                } else {
                    if (status == 100) {
                        greenDates.add(CalendarDay.from(normalizedDate.get(0), normalizedDate.get(1), normalizedDate.get(2)));
                    } else {
                        if (status == 0) {
                            redDates.add(CalendarDay.from(normalizedDate.get(0), normalizedDate.get(1), normalizedDate.get(2)));
                        } else {
                            yellowDates.add(CalendarDay.from(normalizedDate.get(0), normalizedDate.get(1), normalizedDate.get(2)));
                        }
                    }


                }

            }

        }
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

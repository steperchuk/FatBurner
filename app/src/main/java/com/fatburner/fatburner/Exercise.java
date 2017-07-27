package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import static com.fatburner.fatburner.GlobalVariables.TRAINING;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_ID;
import static java.lang.Thread.sleep;

/**
 * Created by sergeyteperchuk on 6/12/17.
 */

public class Exercise extends Menu {

    ImageView imageView;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    CountDownTimer waitTimer;

    int i = 0;
    int currentAttemptId = 1;
    int relaxTimerValue = 1000; // 1 sec for test purposes
    float progress = 0;
    DonutProgress doneBtn;
    boolean animationFinished = true;
    boolean relaxNow = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_exercise, null, false);
        mDrawerLayout.addView(contentView, 0);

        //Intent intent = getIntent();
        //final String currentExercise[] = intent.getStringArrayExtra("training");

        /*
        final String currentExercise[] = TRAINING;
        int trainingId = TRAINING_ID;
        for (int i = 0; i < currentExercise.length; i++) {
            if (!currentExercise[i].equals(" ")) {
                exercisesCount++;
            }
        }
        */

        final TextView exerciseLabel = (TextView) findViewById(R.id.exercise_label);
        final TextView repeatsLabel = (TextView) findViewById(R.id.repeats);
        final TextView infoLabel = (TextView) findViewById(R.id.info);
        final TextView attemptsLabel = (TextView) findViewById(R.id.attempts);
        final TextView attemptsCounterLabel = (TextView) findViewById(R.id.attemptsCounter);
        final TextView timerStrop = (TextView) findViewById(R.id.time);
        final ImageButton infoBtn = (ImageButton) findViewById(R.id.infoBtn);
        final ImageButton playButton = (ImageButton) findViewById(R.id.playerButton);


        List<String> exercises = new ArrayList<>();
        List<String> infos = new ArrayList<>();
        List<String> attempts = new ArrayList<>();
        List<String> repeats = new ArrayList<>();
        List<Integer> relaxTime = new ArrayList<>();


        imageView = (ImageView) findViewById(R.id.imageView);


        ///Work with DB
        // открываем подключение
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();


        userCursor = db.rawQuery("select DAY from TRAININGS where IS_CURRENT = 1", null);
        userCursor.moveToFirst();
        int day = userCursor.getInt(0);

        userCursor = db.rawQuery("select PROGRAMM_NAME from TRAININGS where IS_CURRENT = 1", null);
        userCursor.moveToFirst();
        String programmName = userCursor.getString(0);

        userCursor = db.query("EXERCISES_LIST", null, "DAY = ? AND PROGRAMM_NAME = ?", new String[]{String.valueOf(day), programmName}, null, null, null);


        if (userCursor.moveToFirst()) {
            do {
                exercises.add(userCursor.getString(userCursor.getColumnIndex("EXERCISE")));
                infos.add(userCursor.getString(userCursor.getColumnIndex("EXERCISE_INFO")));
                attempts.add(userCursor.getString(userCursor.getColumnIndex("ATTEMPTS")));
                repeats.add(userCursor.getString(userCursor.getColumnIndex("REPEATS")));
                relaxTime.add(userCursor.getInt(userCursor.getColumnIndex("RELAX_TIME")));
            } while (userCursor.moveToNext());
        }

        userCursor.close();
        db.close();


        final List<String> exerciseList = new ArrayList<>();
        final List<String> infoList = new ArrayList<>();
        final List<String> attemptsList = new ArrayList<>();
        final List<String> repeatsList = new ArrayList<>();
        final List<Integer> relaxTimeList = new ArrayList<>();


        for (int i = 0; i < exercises.size(); i++) {
            for (int j = 0; j < Integer.parseInt(attempts.get(i)); j++) {
                exerciseList.add(exercises.get(i));
                infoList.add(infos.get(i));
                attemptsList.add(attempts.get(i));
                repeatsList.add(repeats.get(i));
                relaxTimeList.add(relaxTime.get(i));
                exerciseList.add("Отдых");
                infoList.add(" ");
                attemptsList.add(" ");
                repeatsList.add(" ");
                relaxTimeList.add(relaxTime.get(i));
            }
        }


        doneBtn = (DonutProgress) findViewById(R.id.done_btn);
        doneBtn.setFinishedStrokeWidth(15);
        doneBtn.setUnfinishedStrokeWidth(15);

        //Imageloading
        db = databaseHelper.open();
        userCursor = db.rawQuery("select * from EXERCISES_INFO where NAME = ?", new String[]{String.valueOf(exerciseList.get(i))}, null);
        userCursor.moveToFirst();
        if (userCursor.getCount() != 0)
        {   String filename = userCursor.getString(1);
            loadImage(filename);
        }
        userCursor.close();
        db.close();
        ///


            exerciseLabel.setText(exerciseList.get(i));
            infoLabel.setText(infoList.get(i));
            attemptsLabel.setText("Подходов: " + attemptsList.get(i));
            repeatsLabel.setText("Повторений: " + repeatsList.get(i));
            attemptsCounterLabel.setText("Подход: " + currentAttemptId + "/" + attemptsList.get(i));
            i = 1;
            doneBtn.setText("Далее");
            startAnimation(false);
            doneBtn.setProgress(progress);
            doneBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (animationFinished) {
                        String filename = "";
                        exerciseLabel.setText(exerciseList.get(i));
                        //Imageloading
                        db = databaseHelper.open();
                        userCursor = db.rawQuery("select * from EXERCISES_INFO where NAME = ?", new String[] {String.valueOf(exerciseList.get(i))}, null);
                        userCursor.moveToFirst();
                        if(exerciseList.get(i) != "Отдых")
                        {
                            if(userCursor.getCount() != 0) {
                                filename = userCursor.getString(1);

                            }
                            else {filename = "working-out-silhouette.png";}
                                relaxNow = false;
                        }
                        else {filename = "relax.png"; relaxNow = true;}
                        loadImage(filename);
                        userCursor.close();
                        db.close();
                        ///
                        infoLabel.setText(infoList.get(i));
                        if (!exerciseList.get(i).equals("Отдых")) {
                            repeatsLabel.setText("Повторений: " + repeatsList.get(i));
                            currentAttemptId++;

                            if(!exerciseList.get(i).equals("Отдых")) {
                                attemptsCounterLabel.setVisibility(View.VISIBLE);
                                if (currentAttemptId > Integer.parseInt(attemptsList.get(i))) {
                                    currentAttemptId = 1;
                                    attemptsCounterLabel.setText("Подход: " + currentAttemptId + "/" + attemptsList.get(i));
                                } else {
                                    attemptsCounterLabel.setText("Подход: " + currentAttemptId + "/" + attemptsList.get(i));
                                }
                            }

                        } else {
                            repeatsLabel.setText(" ");
                            attemptsCounterLabel.setVisibility(View.INVISIBLE);
                        }

                        if (!exerciseList.get(i).equals("Отдых")) {
                            attemptsLabel.setText("Подходов: " + attemptsList.get(i));
                        } else {
                            attemptsLabel.setText(" ");
                        }
                        relaxTimerValue = relaxTimeList.get(i) * 1000;    // comment this row for debug purposes

                        if (!exerciseList.get(i).equals("Отдых")) {

                            progress = progress + 100 / (exerciseList.size() / 2);
                            doneBtn.setText(String.valueOf(progress).substring(0, 2) + "%");
                            doneBtn.setProgress(progress);
                        } else {
                            startAnimation(true);
                        }
                    }

                    if (i == exerciseList.size() - 1) {
                        Intent intent = new Intent(Exercise.this, TrainingCompleted.class);
                        startActivity(intent);
                    }

                    i++;

                    db = databaseHelper.open();
                    ContentValues cv = new ContentValues();
                    cv.put("PROGRESS", (int) progress);
                    db.update("TRAININGS", cv, "IS_CURRENT = ?" , new String[]{String.valueOf(1)});
                    db.close();
                    databaseHelper.close();

                }
            });

        //listeners

        timerStrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(waitTimer != null) {
                    waitTimer.cancel();
                    waitTimer = null;
                }

                animationFinished = true;
                doneBtn.performClick();

            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!relaxNow) {
                    final Dialog dialog = new Dialog(Exercise.this);
                    dialog.setContentView(R.layout.modal_exercise_info);
                    String exerciseName = exerciseLabel.getText().toString();
                    dialog.setCancelable(true);

                    //set up text
                    TextView info = (TextView) dialog.findViewById(R.id.info);


                    db = databaseHelper.open();
                    userCursor = db.rawQuery("select * from EXERCISES_INFO where NAME = ?", new String[]{String.valueOf(exerciseName)}, null);

                    userCursor.moveToFirst();
                    if(userCursor.getCount() == 0){return;}
                    String description = userCursor.getString(2);
                    String advice = userCursor.getString(3);
                    final String video = userCursor.getString(4);

                    userCursor.close();
                    db.close();


                    info.setText(description + "\n" + advice);

                    TextView exerciseLabel = (TextView) dialog.findViewById(R.id.exercise_label);
                    exerciseLabel.setText(exerciseName);


                    ImageButton youtubeBtn = (ImageButton) dialog.findViewById(R.id.youtubeButton);
                    if (video == null){
                        youtubeBtn.setVisibility(View.GONE);
                    }
                    youtubeBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video)));
                        }
                    });


                    //set up button
                    ImageButton button = (ImageButton) dialog.findViewById(R.id.Button01);
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
                else { return; }
            }
        });

    }

    void loadImage(String filename){
        InputStream inputStream = null;
        try{
            inputStream = getApplicationContext().getAssets().open(filename);
            Drawable d = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(d);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }



        void startAnimation (boolean timer){

            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(Exercise.this, R.animator.progress_anim);
            set.setInterpolator(new DecelerateInterpolator());
            set.setTarget(doneBtn);
            if (!timer) {
                set.start();
            } else {
                animationFinished = false;
                waitTimer = new CountDownTimer(relaxTimerValue, 1000) {
                    public void onFinish() {
                        doneBtn.setText("Далее");
                        animationFinished = true;
                    }

                    public void onTick(long millisUntilFinished) {
                        doneBtn.setText(millisUntilFinished / 1000 + " sec");
                        doneBtn.setMax(relaxTimerValue/1000);
                        doneBtn.setProgress(relaxTimerValue/1000 - millisUntilFinished/1000);
                    }
                }.start();

                }
            }
        }



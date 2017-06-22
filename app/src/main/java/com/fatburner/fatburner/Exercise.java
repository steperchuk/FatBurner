package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

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

    int i = 1;
    int iterations = 5;
    int relaxTime = 5000; // 5 sec
    float progress = 0;
    float exercisesCount = 0;
    DonutProgress doneBtn;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_exercise, null, false);
        mDrawerLayout.addView(contentView, 0);

        //Intent intent = getIntent();
        //final String currentExercise[] = intent.getStringArrayExtra("training");

        final String currentExercise[] = TRAINING;
        int trainingId = TRAINING_ID;
        for (int i = 0; i < currentExercise.length; i++) {
            if (!currentExercise[i].equals(" ")) {
                exercisesCount++;
            }
        }


        final TextView exerciseLabel = (TextView) findViewById(R.id.exercise_label);
        final TextView iterationsLabel = (TextView) findViewById(R.id.iterations);
        final TextView weightLabel = (TextView) findViewById(R.id.weight);


        iterationsLabel.setText("Повторов: " + iterations);
        weightLabel.setText("Вес: 15 кг");

        doneBtn = (DonutProgress) findViewById(R.id.done_btn);

        exerciseLabel.setText(currentExercise[0]);
        doneBtn.setText("Done");
        startAnimation(false);
        doneBtn.setProgress(progress);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startAnimation(true);
                    if (!currentExercise[i].equals(" ")) {
                        exerciseLabel.setText(currentExercise[i]);
                        progress = progress + 100 / exercisesCount;
                        doneBtn.setText(String.valueOf(progress).substring(0, 2) + "%");
                        doneBtn.setProgress(progress);
                        i++;
                    } else {
                        Intent intent = new Intent(Exercise.this, TrainingCompleted.class);
                        startActivity(intent);
                    }

            }
        });

    }


        void startAnimation ( boolean timer){
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(Exercise.this, R.animator.progress_anim);
            set.setInterpolator(new DecelerateInterpolator());
            set.setTarget(doneBtn);
            if (!timer) {
                set.start();
            } else {
                int counter = 0;
                new CountDownTimer(relaxTime, 1000) {
                    int counter = 0;
                    public void onFinish() {
                        doneBtn.setText("Start");
                    }

                    public void onTick(long millisUntilFinished) {
                        doneBtn.setText(millisUntilFinished / 1000 + " sec");
                        doneBtn.setProgress(100 / (millisUntilFinished/1000));
                    }
                }.start();

                }
            }
        }



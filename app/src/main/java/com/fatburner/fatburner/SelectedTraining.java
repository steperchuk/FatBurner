package com.fatburner.fatburner;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.fatburner.fatburner.GlobalVariables.LOAD_ARRAY;
import static com.fatburner.fatburner.GlobalVariables.TRAINING;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_ID;

/**
 * Created by sergeyteperchuk on 6/12/17.
 */

public class SelectedTraining extends Menu {

    SharedPreferences sPref;

    DonutProgress startButton;

    int trainingId = 1;
    int trainingCompletionPercent;
    int loadArray[];

    String training[] = {"Бег","Берпи","Отжимания","Приседания","Трастеры","Запрыгивания на тумбу", " ", " ", " ", " "};
    String scoreValue = "30";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_selected_training, null, false);
        mDrawerLayout.addView(contentView, 0);

        Intent intent = getIntent();
        final int selectedTraining = TRAINING_ID;
        loadArray = LOAD_ARRAY;
        //final int selectedTraining = Integer.valueOf(intent.getStringExtra("selectedTraining"));
        //loadArray = intent.getIntArrayExtra("percentCompleted");
        trainingCompletionPercent = loadArray[selectedTraining];

        startButton = (DonutProgress) findViewById(R.id.start_btn);
        ImageButton nextButton = (ImageButton) findViewById(R.id.next_btn);
        ImageButton prevButton = (ImageButton) findViewById(R.id.prev_btn);

        final TextView trainingLabel = (TextView) findViewById(R.id.trainingId);
        TextView score = (TextView) findViewById(R.id.scoreLabel);

        TextView exercise1 = (TextView) findViewById(R.id.exercise_1);
        TextView exercise2 = (TextView) findViewById(R.id.exercise_2);
        TextView exercise3 = (TextView) findViewById(R.id.exercise_3);
        TextView exercise4 = (TextView) findViewById(R.id.exercise_4);
        TextView exercise5 = (TextView) findViewById(R.id.exercise_5);
        TextView exercise6 = (TextView) findViewById(R.id.exercise_6);
        TextView exercise7 = (TextView) findViewById(R.id.exercise_7);
        TextView exercise8 = (TextView) findViewById(R.id.exercise_8);
        TextView exercise9 = (TextView) findViewById(R.id.exercise_9);
        TextView exercise10 = (TextView) findViewById(R.id.exercise_10);

        trainingId = selectedTraining;
        trainingLabel.setText("Training: " + trainingId);
        score.setText("Score: " + scoreValue);


        exercise1.setText(training[0]);
        exercise2.setText(training[1]);
        exercise3.setText(training[2]);
        exercise4.setText(training[3]);
        exercise5.setText(training[4]);
        exercise6.setText(training[5]);
        exercise7.setText(training[6]);
        exercise8.setText(training[7]);
        exercise9.setText(training[8]);
        exercise10.setText(training[9]);

        setButtonLabel();

        startButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(SelectedTraining.this, Exercise.class);
                    //intent.putExtra("training", training);
                    //saveSettings();
                    TRAINING = training;
                    TRAINING_ID = trainingId;
                    startActivity(intent);
                }
            });

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(trainingId < training.length)
                {
                    trainingLabel.setText("Training: " + (trainingId+1));
                    trainingId++;
                    trainingCompletionPercent = loadArray[trainingId];
                    setButtonLabel();
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(trainingId > 0)
                {
                    trainingLabel.setText("Training: " + (trainingId - 1));
                    trainingId--;
                    trainingCompletionPercent = loadArray[trainingId];
                    setButtonLabel();
                }
            }
        });

        }

    void startAnimation ( ) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(SelectedTraining.this, R.animator.progress_anim);
        set.setInterpolator(new DecelerateInterpolator());
        set.setTarget(startButton);
        set.start();

    }

    void setButtonLabel(){
        switch (trainingCompletionPercent){
            case 0:
                startButton.setText("Start");
                break;
            case 100:
                startButton.setText("Repeat");
                break;
            default:
                startButton.setText("Continue");
        }
        startButton.setProgress(trainingCompletionPercent);
    }

    /*
    void saveSettings() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("selectedTraining",String.valueOf(trainingId));
        Set<String> trainingSet = new HashSet<>();
        Collections.addAll(trainingSet, training);
        ed.putStringSet("training", trainingSet);
        ed.commit();
    }
*/

    }


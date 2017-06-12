package com.fatburner.fatburner;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by sergeyteperchuk on 6/12/17.
 */

public class SelectedTraining extends Menu {


    String training[] = {"Бег","Берпи","Отжимания","Приседания","Трастеры","Запрыгивания на тумбу", " ", " ", " ", " "};
    String scoreValue = "30";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_selected_training);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_selected_training, null, false);
        mDrawerLayout.addView(contentView, 0);

        Intent intent = getIntent();
        String selectedTraining = intent.getStringExtra("selectedTraining");


        Button startButton = (Button) findViewById(R.id.start_btn);

        TextView trainingLabel = (TextView) findViewById(R.id.trainingId);
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


        trainingLabel.setText("Training: " + selectedTraining);
        score.setText("Get " + scoreValue + " scores!");


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



        startButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(SelectedTraining.this, Exercise.class);
                    intent.putExtra("training", training);
                    startActivity(intent);

                }
            });


        }

    }


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

public class Exercise extends Menu {

    int i = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_exercise, null, false);
        mDrawerLayout.addView(contentView, 0);

        Intent intent = getIntent();
        final String currentExercise[] = intent.getStringArrayExtra("training");


        final TextView exerciseLabel = (TextView) findViewById(R.id.exercise_label);
        Button doneBtn = (Button) findViewById(R.id.done_btn);
        exerciseLabel.setText(currentExercise[0]);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!currentExercise[i].equals(" ")) {
                    exerciseLabel.setText(currentExercise[i]);
                    i++;
                }
                else {
                    Intent intent = new Intent(Exercise.this, TrainingCompleted.class);
                    startActivity(intent);
                }
            }
        });

    }
}

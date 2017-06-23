package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import static com.fatburner.fatburner.GlobalVariables.LOAD_ARRAY;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_ID;

public class MainActivity extends Menu {

    DonutProgress startButton;
    ImageButton playerButton;
    TextView currentTraining;

    boolean playerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Implements menu visibility
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

        playerButton = (ImageButton) findViewById(R.id.music_btn);

        currentTraining = (TextView) findViewById(R.id.current_training);
        currentTraining.setText("Training: " + TRAINING_ID);

        startButton = (DonutProgress) findViewById(R.id.start_btn);
        startAnimation();

                if(TRAINING_ID == 0)
                {startButton.setText("Start");}
                else {
                    if (LOAD_ARRAY[TRAINING_ID] > 0) {
                        startButton.setText("Continue");
                    }
                }

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    if(TRAINING_ID == 0){
                    Intent intent = new Intent(MainActivity.this, MyPrograms.class);
                    startActivity(intent);}
                else{
                        Intent intent = new Intent(MainActivity.this, Exercise.class);
                        startActivity(intent);
                }


            }
        });

        playerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!playerStarted)
                {
                    playerButton.setImageResource(R.drawable.ic_play_button);
                    //invoke player here
                }
                else
                    {
                    //stop playing here
                        playerButton.setImageResource(R.drawable.ic_pause_button);
                    }
            }
        });


    }

    void startAnimation ( ) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.progress_anim);
        set.setInterpolator(new DecelerateInterpolator());
        set.setTarget(startButton);
        set.start();

    }

}



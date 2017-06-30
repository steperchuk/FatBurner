package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.IOException;

import static com.fatburner.fatburner.GlobalVariables.LOAD_ARRAY;
import static com.fatburner.fatburner.GlobalVariables.TRAINING_ID;

public class MainActivity extends Menu {

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    DonutProgress startButton;
    ImageButton playerButton;
    TextView currentTraining;

    final String DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/music.mp3";
    MediaPlayer mediaPlayer;
    AudioManager am;

    boolean playerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // создаем базу данных
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.create_db();


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
                    Intent intent = new Intent(MainActivity.this, ProgramsList.class);
                    startActivity(intent);}
                else{
                        Intent intent = new Intent(MainActivity.this, Exercise.class);
                        startActivity(intent);
                }


            }
        });

        am = (AudioManager) getSystemService(AUDIO_SERVICE);


        playerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void startAnimation ( ) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.progress_anim);
        set.setInterpolator(new DecelerateInterpolator());
        set.setTarget(startButton);
        set.start();

    }

}



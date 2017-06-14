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

import com.github.lzyzsd.circleprogress.DonutProgress;

public class MainActivity extends Menu {

    DonutProgress startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Implements menu visibility
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);


        startButton = (DonutProgress) findViewById(R.id.start_btn);
        startButton.setText("Start");
        startAnimation();

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*
                if(startButton.getText().equals("Start"))
                {startButton.setText("Done");}
                else
                {startButton.setText("Start");}
                */

                    Intent intent = new Intent(MainActivity.this, MyPrograms.class);
                    startActivity(intent);

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



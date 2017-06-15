package com.fatburner.fatburner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;

/**
 * Created by sete on 6/15/2017.
 */

public class Water extends Menu implements View.OnClickListener {

    TextView waterProgressLabel;
    ImageButton small;
    ImageButton medium;
    ImageButton big;
    ImageButton biggest;
    CircleProgress waterProgress;

    float progress;
    int waterDailyNorm = 2500;
    int selectedAmount = 200;
    float increment = 0;
    int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_water, null, false);
        mDrawerLayout.addView(contentView, 0);

        waterProgressLabel = (TextView) findViewById(R.id.waterAmount);

        small = (ImageButton) findViewById(R.id.smallWaterBtn);
        small.setOnClickListener(this);
        medium = (ImageButton) findViewById(R.id.mediumWaterBtn);
        medium.setOnClickListener(this);
        big = (ImageButton) findViewById(R.id.bigWaterBtn);
        big.setOnClickListener(this);
        biggest = (ImageButton) findViewById(R.id.biggestWaterBtn);
        biggest.setOnClickListener(this);



        waterProgress = (CircleProgress) findViewById(R.id.waterProgress);
        waterProgress.setTextSize(36);





        waterProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                amount = amount + selectedAmount;
                float a = Math.round(selectedAmount);
                float b = Math.round(waterDailyNorm);
                float c = a/b;

                increment = (c * 100);
                progress = progress + increment;

                if(progress < 120 && progress > 100)
                {
                    waterProgress.setMax(Math.round(progress));
                    waterProgress.setFinishedColor(Color.YELLOW);
                    waterProgress.setTextColor(Color.BLACK);
                }
                if(progress > 120)
                {
                    waterProgress.setMax(Math.round(progress));
                    waterProgress.setFinishedColor(Color.RED);
                    waterProgress.setTextColor(Color.WHITE);
                }

                waterProgress.setProgress(Math.round(progress));
                waterProgressLabel.setText(amount + " / " + waterDailyNorm + " ml");
            }
        });


    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.smallWaterBtn:
                selectedAmount = 100;

                break;

            case R.id.mediumWaterBtn:
                selectedAmount = 200;

                break;

            case R.id.bigWaterBtn:
                selectedAmount = 300;

                break;

            case R.id.biggestWaterBtn:
                selectedAmount = 400;
                
                break;
        }

    }
}

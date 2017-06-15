package com.fatburner.fatburner;

import android.content.Context;
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

    int progress = 0;
    int waterDailyNorm = 2500;
    int selectedAmount = 0;
    int increment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_diet, null, false);
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


        increment = (waterDailyNorm/selectedAmount * 100);

        waterProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progress = progress + increment;
                waterProgress.setProgress(progress);
                waterProgressLabel.setText(progress*100 + " / " + waterDailyNorm + " ml");
            }
        });

        if(progress < 120 && progress > 100)
        {
            waterProgress.setMax(progress);
            waterProgress.setBackgroundColor(Color.YELLOW);
        }
        if(progress > 120)
        {
            waterProgress.setMax(progress);
            waterProgress.setBackgroundColor(Color.RED);
        }

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

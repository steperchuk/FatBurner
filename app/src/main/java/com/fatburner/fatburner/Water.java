package com.fatburner.fatburner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;

import java.io.IOException;
import java.io.InputStream;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        ImageView imageView = (ImageView) findViewById(R.id.imageView) ;
        String filename = "drop.png";
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

        //setting ImageView size
        imageView.getLayoutParams().height = 50;
        imageView.getLayoutParams().width = 50;
        imageView.requestLayout();

        waterProgress = (CircleProgress) findViewById(R.id.waterProgress);
        waterProgress.setTextSize(100);


        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setMax(10);
        seekBar.setProgress(3,true);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress >= 0 && progress <= seekBar.getMax()) {

                        String progressString = String.valueOf(progress * 50);
                        //seekBar.setText(progressString); // the TextView Reference
                        seekBar.setSecondaryProgress(progress);
                    }
                }

            }
        });


        waterProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                amount = amount + selectedAmount;
                float a = Math.round(selectedAmount);
                float b = Math.round(waterDailyNorm);
                float c = a/b;

                increment = (c * 100);
                progress = progress + increment;

                if(progress > 130)
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
        TextView textView11 = (TextView) findViewById(R.id.textView11);
        TextView textView12 = (TextView) findViewById(R.id.textView12);
        TextView textView13 = (TextView) findViewById(R.id.textView13);
        TextView textView14 = (TextView) findViewById(R.id.textView14);

        textView11.setTextColor(Color.BLACK);
        textView12.setTextColor(Color.BLACK);
        textView13.setTextColor(Color.BLACK);
        textView14.setTextColor(Color.BLACK);

        switch (v.getId()) {
            case R.id.smallWaterBtn:
                selectedAmount = 100;
                textView11.setTextColor(Color.BLUE);
                break;

            case R.id.mediumWaterBtn:
                selectedAmount = 200;
                textView12.setTextColor(Color.BLUE);
                break;

            case R.id.bigWaterBtn:
                selectedAmount = 300;
                textView13.setTextColor(Color.BLUE);
                break;

            case R.id.biggestWaterBtn:
                selectedAmount = 400;
                textView14.setTextColor(Color.BLUE);
                break;
        }

    }
}

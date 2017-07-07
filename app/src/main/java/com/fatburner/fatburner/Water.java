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

public class Water extends Menu {

    TextView waterProgressLabel;
    TextView selectedAmountLabel;
    ImageView imageView;
    CircleProgress waterProgress;

    float progress;
    int waterDailyNorm = 2500;
    int selectedAmount = 150;
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

        imageView = (ImageView) findViewById(R.id.imageView) ;
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

        selectedAmountLabel = (TextView) findViewById(R.id.selectedAmount);
        selectedAmountLabel.setText(String.valueOf(selectedAmount));

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
                        selectedAmountLabel.setText(progressString + " ml");
                        imageView.getLayoutParams().height = progress * 50;
                        imageView.getLayoutParams().width = progress * 50;
                        imageView.requestLayout();
                        seekBar.setSecondaryProgress(progress);
                        selectedAmount = progress * 50;
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

                waterProgress.setProgress(Math.round(progress));
                waterProgressLabel.setText(amount + " / " + waterDailyNorm + " ml");
            }
        });

    }
}

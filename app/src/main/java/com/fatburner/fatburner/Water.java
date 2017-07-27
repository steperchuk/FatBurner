package com.fatburner.fatburner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
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

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    TextView waterProgressLabel;
    TextView selectedAmountLabel;
    ImageView imageView;
    CircleProgress waterProgress;

    float progress = 0;
    int waterDailyNorm = 2500;
    int selectedAmount = 150;
    float increment = 0;
    int progressBarState = 3;
    int amount = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_water, null, false);
        mDrawerLayout.addView(contentView, 0);

        loadData();

        waterProgressLabel = (TextView) findViewById(R.id.waterAmount);
        waterProgressLabel.setText(amount + " / " + waterDailyNorm + " мл");

        imageView = (ImageView) findViewById(R.id.imageView);
        String filename = "drop.png";
        InputStream inputStream = null;
        try {
            inputStream = getApplicationContext().getAssets().open(filename);
            Drawable d = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(d);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        //setting ImageView size
        imageView.getLayoutParams().height = (progressBarState * 50) + 100;
        imageView.getLayoutParams().width = (progressBarState * 50) + 100;
        imageView.requestLayout();

        waterProgress = (CircleProgress) findViewById(R.id.waterProgress);
        waterProgress.setTextSize(100);
        if(amount >= waterDailyNorm)
        {
            waterProgress.setProgress(100);
        }
        else
        {
            waterProgress.setProgress(Math.round(progress));
        }

        waterProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(amount >= waterDailyNorm)
                {
                    ModalDialogWater dialog = new ModalDialogWater();
                    dialog.show(getSupportFragmentManager(), "custom");
                    waterProgress.setProgress(100);
                    return;
                }

                amount = amount + selectedAmount;
                float a = Math.round(selectedAmount);
                float b = Math.round(waterDailyNorm);
                float c = a / b;

                increment = (c * 100);
                progress = progress + increment;

                if(amount >= waterDailyNorm)
                {waterProgress.setProgress(100);}
                else
                    {
                    waterProgress.setProgress(Math.round(progress));
                    }
                waterProgressLabel.setText(amount + " / " + waterDailyNorm + " мл");
                saveData();
            }
        });

        selectedAmountLabel = (TextView) findViewById(R.id.selectedAmount);
        selectedAmountLabel.setText(String.valueOf(progressBarState * 50 + " мл"));


        SeekBar seekBar = (SeekBar) findViewById(R.id.mySeekBar);
        seekBar.setMax(10);
        seekBar.setProgress(progressBarState);


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
                        if(progress <3){seekBar.setProgress(3); progress = 3;}
                        progressBarState = progress;
                        String progressString = String.valueOf(progress * 50);
                        selectedAmountLabel.setText(progressString + " мл");
                        imageView.getLayoutParams().height = (progress * 50) + 100;
                        imageView.getLayoutParams().width = (progress * 50) + 100;
                        imageView.requestLayout();
                        seekBar.setSecondaryProgress(progress);
                        selectedAmount = progress * 50;
                    }
                }

            }
        });


        }

         void loadData(){

            databaseHelper = new DatabaseHelper(this);
            databaseHelper.getWritableDatabase();
            db = databaseHelper.open();

            userCursor =  db.rawQuery("select * from WATER_INFO", null);

            userCursor.moveToFirst();
            waterDailyNorm = userCursor.getInt(0);
            amount = userCursor.getInt(1);
            progressBarState = userCursor.getInt(2);
            progress = userCursor.getInt(3);

             userCursor.close();
             db.close();
        }


        public void saveData() {

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        ContentValues cv = new ContentValues();
        cv.put("CURRENT_AMOUNT", amount);
        cv.put("SELECTED_AMOUNT", progressBarState);
        cv.put("PROGRESS", Math.round(progress));

        db.update("WATER_INFO", cv, null, null);

        userCursor.close();
        db.close();

            db = databaseHelper.open();

            cv = new ContentValues();
            cv.put("WATER_STATUS", Math.round(progress));
            db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});

            db.close();


        }
}


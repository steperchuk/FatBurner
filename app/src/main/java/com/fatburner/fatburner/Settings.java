package com.fatburner.fatburner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import java.security.Provider;

public class Settings extends Menu {

    SettingsProvider settings = new SettingsProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final DatabaseHandler db = new DatabaseHandler(this);
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        mDrawerLayout.addView(contentView, 0);


        final Button applyButton = (Button) findViewById(R.id.applyButton);
        final Switch gender = (Switch) findViewById(R.id.gender);
        final EditText age = (EditText) findViewById(R.id.age);
        final EditText weight = (EditText) findViewById(R.id.weight);
        final EditText dayStartTime = (EditText) findViewById(R.id.dayStart);
        final EditText trainingStart = (EditText) findViewById(R.id.trainingStart);
        final CheckBox monday = (CheckBox) findViewById(R.id.monday);
        final CheckBox thuesday = (CheckBox) findViewById(R.id.thuesday);
        final CheckBox wensday = (CheckBox) findViewById(R.id.wensday);
        final CheckBox thursday = (CheckBox) findViewById(R.id.thursday);
        final CheckBox friday = (CheckBox) findViewById(R.id.friday);
        final CheckBox saturday = (CheckBox) findViewById(R.id.saturday);
        final CheckBox sunday = (CheckBox) findViewById(R.id.sunday);
        final Switch switchWaterNotification = (Switch) findViewById(R.id.switchWaterNotification);
        final Switch switchFoodNotification = (Switch) findViewById(R.id.switchFoodNotification);
        final Switch switchSleepNotification = (Switch) findViewById(R.id.switchSleepNotification);

        SettingsProvider initialSettings = db.getSettings();

        /*
        ////////////////////////// values from db ////////////////

        //switchFoodNotification.setChecked(initialSettings.getShowWaterNotification());
        //switchFoodNotification.setChecked(initialSettings.getShowFoodNotification());
        //switchFoodNotification.setChecked(initialSettings.getShowSleepNotification());
        //gender.setChecked(initialSettings.getGender());

        //////////////////////////
         */

        if (initialSettings._dayStartTime == null) {
            applyButton.setText("Apply");
        }


        gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setGender(isChecked);
            }
        });

        switchWaterNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setShowWaterNotification(isChecked);
            }
        });

        switchFoodNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setShowFoodNotification(isChecked);
            }
        });

        switchSleepNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setShowSleepNotification(isChecked);
            }
        });

        final String[] trainingDays = new String [7];

        monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    trainingDays[0] = "Mon";
                }
                else
                {
                    trainingDays[0] = "";
                }
            }
        });

        thuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    trainingDays[1] = "Tue";
                }
                else
                {
                    trainingDays[1] = "";
                }

            }
        });

        wensday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    trainingDays[2] = "Wen";
                }
                else
                {
                    trainingDays[2] = "";
                }

            }
        });

        thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    trainingDays[3] = "Thu";
                }
                else
                {
                    trainingDays[3] = "";
                }

            }
        });

        friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    trainingDays[4] = "Fri";
                }
                else
                {
                    trainingDays[4] = "";
                }

            }
        });

        saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    trainingDays[5] = "Sat";
                }
                else
                {
                    trainingDays[5] = "";
                }

            }
        });

        sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    trainingDays[6] = "Sun";
                }
                else
                {
                    trainingDays[6] = "";
                }

            }
        });


        settings.setTrainingDays(trainingDays.toString());
        settings.setDayStartTime(dayStartTime.getText().toString());
        settings.setTrainingStartTime(trainingStart.getText().toString());
        settings.setAge(age.getText().toString());
        settings.setWeight(weight.getText().toString());

        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (applyButton.getText().equals("Apply")) {
                    applyButton.setText("Edit");
                } else {
                    applyButton.setText("Apply");
                }

                db.addSettings(settings);
                //Intent intent = new Intent(Settings.this, MainActivity.class);
                //startActivity(intent);

            }
        });




    }
}

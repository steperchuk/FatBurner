package com.fatburner.fatburner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;


public class Settings extends Menu {

    Calendar calander = Calendar.getInstance();

    String days[] = { "Monday", "Thuesday", "Wensday", "Thursday", "Friday", "Saturday", "Sunday" };
    boolean chkd[] = { false, false, false, false, false, false, false };
    String daysSelected = "";

    int DAY_START_TIME = 1;
    int TRAIN_START_TIME = 2;
    final int DIALOG_DAYS = 3;
    int myHour =  calander.get(Calendar.HOUR);
    int myMinute = calander.get(Calendar.MINUTE);
    EditText dayStartTime;
    EditText trainingStart;
    EditText trainingDays;


    SettingsProvider settings;

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
        dayStartTime = (EditText) findViewById(R.id.dayStart);
        trainingStart = (EditText) findViewById(R.id.trainingStart);
        trainingDays = (EditText) findViewById(R.id.trainingDays);
        final Switch switchWaterNotification = (Switch) findViewById(R.id.switchWaterNotification);
        final Switch switchFoodNotification = (Switch) findViewById(R.id.switchFoodNotification);
        final Switch switchSleepNotification = (Switch) findViewById(R.id.switchSleepNotification);

        dayStartTime.setInputType(InputType.TYPE_NULL);
        trainingStart.setInputType(InputType.TYPE_NULL);
        trainingDays.setInputType(InputType.TYPE_NULL);


        dayStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                showDialog(DAY_START_TIME);
            }
        });


        trainingStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                showDialog(TRAIN_START_TIME);
            }
        });

        trainingDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                trainingDays.setText("");
                daysSelected = "";
                showDialog(DIALOG_DAYS);
            }
        });



        SettingsProvider initialSettings = db.getSettings();
        settings = initialSettings;

        age.setText(initialSettings.getAge());
        weight.setText(initialSettings.getWeight());
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
                if(gender.getText().equals("Male"))
                {gender.setText("Female");}
                else {gender.setText("Male");}
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





        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (applyButton.getText().equals("Apply")) {
                    applyButton.setText("Edit");
                } else {
                    applyButton.setText("Apply");
                }

                settings.setAge(age.getText().toString());
                settings.setWeight(weight.getText().toString());
                settings.setTrainingDays(trainingDays.toString());
                settings.setDayStartTime(dayStartTime.getText().toString());
                settings.setTrainingStartTime(trainingStart.getText().toString());


                db.addSettings(settings);
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);

            }
        });



    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        if (id == DAY_START_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, myHour, myMinute, false);
            return tpd;
        }
        if (id == TRAIN_START_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, mySecondCallBack, myHour, myMinute, false);
            return tpd;
        }
        if (id == DIALOG_DAYS)
        {
            adb.setTitle(R.string.items);
            adb.setMultiChoiceItems(days, chkd, myItemsMultiClickListener);
            adb.setPositiveButton(R.string.ok, myBtnClickListener);
            return adb.create();
        }

        return super.onCreateDialog(id);
    };

    DialogInterface.OnMultiChoiceClickListener myItemsMultiClickListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            ListView lv = ((AlertDialog) dialog).getListView();
            //Log.d( "tag" , "which = " + which + ", isChecked = " + isChecked);
        }
    };

    DialogInterface.OnClickListener myBtnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            SparseBooleanArray sbArray = ((AlertDialog)dialog).getListView().getCheckedItemPositions();
            for (int i = 0; i < sbArray.size(); i++) {
                int key = sbArray.keyAt(i);
                if (sbArray.get(key))
                    daysSelected = daysSelected + days[key].substring(0,3) + ", ";
                    // Log.d("qwe", "checked: " + key);
            }
            daysSelected = daysSelected.substring(0, daysSelected.length() - 2);
            trainingDays.setText(daysSelected);
        }
    };

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            String normalizedMinute = "";
            if(minute<10)
            {normalizedMinute = "0" + Integer.toString(myMinute);}
            else
            {normalizedMinute = Integer.toString(myMinute);}
            dayStartTime.setText(Integer.toString(myHour) + ":" + normalizedMinute);
        }
    };

    TimePickerDialog.OnTimeSetListener mySecondCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            String normalizedMinute = "";
            if(minute<9)
            {normalizedMinute = "0" + Integer.toString(myMinute);}
            else
            {normalizedMinute = Integer.toString(myMinute);}
            trainingStart.setText(Integer.toString(myHour) + ":" + normalizedMinute);
        }
    };


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Settings.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

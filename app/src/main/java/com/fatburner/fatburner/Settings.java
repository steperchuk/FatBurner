package com.fatburner.fatburner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.fatburner.fatburner.R.id.parent;
import static com.fatburner.fatburner.R.id.phase;


public class Settings extends Menu {

    Calendar calander = Calendar.getInstance();

    String phases[] = {"Фаза 1", "Фаза 2", "Фаза 3"};
    String days[] = { "Monday", "Thuesday", "Wensday", "Thursday", "Friday", "Saturday", "Sunday" };
    boolean chkd[] = { false, false, false, false, false, false, false };
    String daysSelected = "";

    int DAY_START_TIME = 1;
    int TRAIN_START_TIME = 2;
    final int DIALOG_DAYS = 3;
    int myHour =  calander.get(Calendar.HOUR);
    int myMinute = calander.get(Calendar.MINUTE);
    TextView dayStartTime;
    TextView trainingStart;
    TextView trainingDays;
    TextView age;
    TextView weight;
    Spinner phase;
    Switch gender;
    Switch switchDietType;
    Switch switchWaterNotification;
    Switch switchFoodNotification;
    Switch switchSleepNotification;


    SharedPreferences sPref;
    String ageValue;
    String weightValue;
    boolean genderValue;
    int phaseValue;
    String dayStartTimeValue;
    String trainingStartTimeValue;
    String trainingDaysValue;
    boolean dietTypeValue;
    boolean waterNotificationValue;
    boolean sleepNotificationValue;
    boolean foodNotificationValue;
    boolean isFirstStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        mDrawerLayout.addView(contentView, 0);


        final ImageButton applyButton = (ImageButton) findViewById(R.id.applyBtn);
        gender = (Switch) findViewById(R.id.gender);
        age = (TextView) findViewById(R.id.age);
        weight = (TextView) findViewById(R.id.weight);
        dayStartTime = (TextView) findViewById(R.id.dayStart);
        trainingStart = (TextView) findViewById(R.id.trainingStart);
        trainingDays = (TextView) findViewById(R.id.trainingDays);
        switchWaterNotification = (Switch) findViewById(R.id.switchWaterNotification);
        switchFoodNotification = (Switch) findViewById(R.id.switchFoodNotification);
        switchSleepNotification = (Switch) findViewById(R.id.switchSleepNotification);
        switchDietType = (Switch) findViewById(R.id.diet_type);

        dayStartTime.setInputType(InputType.TYPE_NULL);
        trainingStart.setInputType(InputType.TYPE_NULL);
        trainingDays.setInputType(InputType.TYPE_NULL);

        phase = (Spinner) findViewById(R.id.phase);

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

        loadSettings();

        if(dietTypeValue){
            switchDietType.setText("Углеводная");
        }

        gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(gender.getText().equals("Male"))
                {gender.setText("Female");}
                else {gender.setText("Male");}
                genderValue = isChecked;
            }
        });

        switchWaterNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                waterNotificationValue = isChecked;
            }
        });

        switchFoodNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                foodNotificationValue = isChecked;
            }
        });

        switchSleepNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sleepNotificationValue = isChecked;
            }
        });

        switchDietType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchDietType.getText().equals("Белковая"))
                {switchDietType.setText("Углеводная");}
                else {switchDietType.setText("Белковая");}
                dietTypeValue = isChecked;
            }
        });


        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, phases);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        phase.setAdapter(adapter);
        phase.setSelection(phaseValue);

        phase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = phase.getSelectedItem().toString();
                List<String> wordList = Arrays.asList(phases);
                phaseValue = wordList.indexOf(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ageValue = age.getText().toString();
                weightValue = weight.getText().toString();


                isFirstStart = false;
                saveSettings();
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);

            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSettings();
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
        }
    };

    DialogInterface.OnClickListener myBtnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            SparseBooleanArray sbArray = ((AlertDialog)dialog).getListView().getCheckedItemPositions();
            for (int i = 0; i < sbArray.size(); i++) {
                int key = sbArray.keyAt(i);
                if (sbArray.get(key))
                    daysSelected = daysSelected + days[key].substring(0,3) + ", ";
            }
            daysSelected = daysSelected.substring(0, daysSelected.length() - 2);
            trainingDaysValue = daysSelected;
            trainingDays.setText(trainingDaysValue);
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
            dayStartTimeValue = Integer.toString(myHour) + ":" + normalizedMinute;
            dayStartTime.setText(dayStartTimeValue);
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
            trainingStartTimeValue = Integer.toString(myHour) + ":" + normalizedMinute;
            trainingStart.setText(trainingStartTimeValue);
        }
    };


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Settings.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void saveSettings() {

        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("AGE",ageValue);
        ed.putString("WEIGHT", weightValue);
        ed.putString("DAY_START", dayStartTimeValue);
        ed.putString("TRAINING_START", trainingStartTimeValue);
        ed.putString("TRAINING_DAYS", trainingDaysValue);
        ed.putInt("PHASE", phaseValue);
        ed.putBoolean("IS_FIRST_RUN", isFirstStart);
        ed.putBoolean("GENDER", genderValue);
        ed.putBoolean("DIET_TYPE", dietTypeValue);
        ed.putBoolean("FOOD_NOTIFICATON", foodNotificationValue);
        ed.putBoolean("SLEEP_NOTIFICATON", sleepNotificationValue);
        ed.putBoolean("WATER_NOTIFICATON", waterNotificationValue);
        ed.commit();
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }

   public void loadSettings() {
        sPref = getPreferences(MODE_PRIVATE);

        ageValue = sPref.getString("AGE","");
        weightValue = sPref.getString("WEIGHT","");
        dayStartTimeValue = sPref.getString("DAY_START","");
        trainingStartTimeValue = sPref.getString("TRAINING_START","");
        trainingDaysValue = sPref.getString("TRAINING_DAYS","");
        isFirstStart = sPref.getBoolean("IS_FIRST_RUN",false);
        genderValue = sPref.getBoolean("GENDER", false);
        foodNotificationValue = sPref.getBoolean("FOOD_NOTIFICATON", false);
        sleepNotificationValue = sPref.getBoolean("SLEEP_NOTIFICATON", false);
        waterNotificationValue = sPref.getBoolean("WATER_NOTIFICATON", false);

       dietTypeValue = sPref.getBoolean("DIET_TYPE", false);
       phaseValue = sPref.getInt("PHASE",0);

       age.setText(ageValue);
        weight.setText(weightValue);
        dayStartTime.setText(dayStartTimeValue);
        trainingStart.setText(trainingStartTimeValue);
        trainingDays.setText(trainingDaysValue);
        gender.setChecked(genderValue);
        switchDietType.setChecked(dietTypeValue);
        switchFoodNotification.setChecked(foodNotificationValue);
        switchSleepNotification.setChecked(sleepNotificationValue);
        switchWaterNotification.setChecked(waterNotificationValue);

        Toast.makeText(this, "Settings loaded", Toast.LENGTH_SHORT).show();
    }

}

package com.fatburner.fatburner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.fatburner.fatburner.GlobalVariables.selectedPhase;
import static com.fatburner.fatburner.GlobalVariables.selectedDiet;


public class Settings extends Menu {


    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;


    String phases[] = {"Фаза 1", "Фаза 2", "Фаза 3"};
    String days[] = { "Monday", "Thuesday", "Wensday", "Thursday", "Friday", "Saturday", "Sunday" };
    String goals[] = {"Увеличение силы","Поддержание формы","Подготовительная","Набор Массы","Жиросжигание"};
    String difficulties[] = {"Новичок","Любитель","Профессионал"};
    boolean chkd[] = { false, false, false, false, false, false, false };
    String daysSelected = "";


    final int DIALOG_DAYS = 3;
    TextView waterDayNorm;
    TextView trainingDays;
    Spinner goal;
    Spinner difficulty;
    Switch switchUseCustomDiet;
    Spinner phase;
    Switch gender;
    Switch switchDietType;
    Switch switchWaterNotification;
    Switch switchFoodNotification;
    Switch switchSleepNotification;


    boolean genderValue;
    boolean useCustomDietValue;
    int phaseValue;
    int goalValue;
    int difficultyValue;
    String trainingDaysValue;
    String waterNormValue;
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

        waterDayNorm = (TextView) findViewById(R.id.waterDayNorm);
        trainingDays = (TextView) findViewById(R.id.trainingDays);

        goal = (Spinner) findViewById(R.id.goal);
        difficulty = (Spinner) findViewById(R.id.difficulty);
        phase = (Spinner) findViewById(R.id.phase);

        gender = (Switch) findViewById(R.id.gender);
        switchUseCustomDiet = (Switch) findViewById(R.id.customDietUsage);
        switchWaterNotification = (Switch) findViewById(R.id.switchWaterNotification);
        switchFoodNotification = (Switch) findViewById(R.id.switchFoodNotification);
        switchSleepNotification = (Switch) findViewById(R.id.switchSleepNotification);
        switchDietType = (Switch) findViewById(R.id.diet_type);

        trainingDays.setInputType(InputType.TYPE_NULL);

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

        if(switchUseCustomDiet.isChecked()){
            switchUseCustomDiet.setText("Yes");
            switchDietType.setVisibility(View.INVISIBLE);
            phase.setVisibility(View.INVISIBLE);
        }

        gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(gender.getText().equals("Male"))
                {gender.setText("Female");}
                else {gender.setText("Male");}
                genderValue = isChecked;
            }
        });

        switchUseCustomDiet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchUseCustomDiet.getText().equals("No"))
                {switchUseCustomDiet.setText("Yes");
                    switchDietType.setVisibility(View.INVISIBLE);
                    phase.setVisibility(View.INVISIBLE);
                }
                else {switchUseCustomDiet.setText("No");
                    switchDietType.setVisibility(View.VISIBLE);
                    phase.setVisibility(View.VISIBLE);
                }
                useCustomDietValue = isChecked;
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
        adapter = new ArrayAdapter<String>(this, R.layout.my_spinner, phases);
        adapter.setDropDownViewResource(R.layout.my_spinner);
        phase.setAdapter(adapter);
        phase.setSelection(phaseValue);


        adapter = new ArrayAdapter<String>(this, R.layout.my_spinner, goals);
        goal.setAdapter(adapter);
        goal.setSelection(goalValue);

        adapter = new ArrayAdapter<String>(this, R.layout.my_spinner, difficulties);
        difficulty.setAdapter(adapter);
        difficulty.setSelection(difficultyValue);

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


        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = difficulty.getSelectedItem().toString();
                List<String> wordList = Arrays.asList(difficulties);
                difficultyValue = wordList.indexOf(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        goal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = goal.getSelectedItem().toString();
                List<String> wordList = Arrays.asList(goals);
                goalValue = wordList.indexOf(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Settings.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void loadSettings() {

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        userCursor =  db.rawQuery("select * from APP_SETTINGS", null);

        userCursor.moveToFirst();
        if(userCursor.getInt(0) == 1)
        {isFirstStart = true;}
        else{isFirstStart = false;}

        if(userCursor.getInt(1) == 1)
        {genderValue = true;}
        else{genderValue = false;}

        goalValue = userCursor.getInt(2);
        difficultyValue = userCursor.getInt(3);
        trainingDaysValue = userCursor.getString(4);
        waterNormValue = userCursor.getString(5);

        if(userCursor.getInt(6) == 1)
        {useCustomDietValue = true;}
        else{useCustomDietValue = false;}


        if(userCursor.getInt(7) == 1)
        {dietTypeValue = true;}
        else{dietTypeValue = false;}

        phaseValue = userCursor.getInt(8);

        if(userCursor.getInt(9) == 1)
        {foodNotificationValue = true;}
        else{foodNotificationValue = false;}

        if(userCursor.getInt(10) == 1)
        {sleepNotificationValue = true;}
        else{sleepNotificationValue = false;}


        if(userCursor.getInt(11) == 1)
        {waterNotificationValue = true;}
        else{waterNotificationValue = false;}

        userCursor.close();
        db.close();

        trainingDays.setText(trainingDaysValue);
        waterDayNorm.setText(waterNormValue);
        gender.setChecked(genderValue);
        switchDietType.setChecked(dietTypeValue);
        switchUseCustomDiet.setChecked(useCustomDietValue);
        switchFoodNotification.setChecked(foodNotificationValue);
        switchSleepNotification.setChecked(sleepNotificationValue);
        switchWaterNotification.setChecked(waterNotificationValue);


        selectedPhase = phaseValue;  //should be removed in future
        selectedDiet = dietTypeValue; //should be removed in future

        Toast.makeText(this, "Settings loaded", Toast.LENGTH_SHORT).show();
    }

   public void saveSettings() {

       databaseHelper = new DatabaseHelper(this);
       databaseHelper.getWritableDatabase();
       db = databaseHelper.open();

       ContentValues cv = new ContentValues();
       cv.put("FIRST_START", 1);
       if(genderValue){cv.put("GENDER", 1);}
       else{cv.put("GENDER", 0);}

       cv.put("GOAL", goalValue);
       cv.put("DIFFICULTY", difficultyValue);
       cv.put("TRAINING_DAYS", trainingDaysValue);
       cv.put("WATER_NORM", waterNormValue);
       if(useCustomDietValue){cv.put("USE_CUSTOM_DIET", 1);}
       else{cv.put("USE_CUSTOM_DIET", 0);}
       if(dietTypeValue){ cv.put("DIET_TYPE", 1);}
       else{cv.put("DIET_TYPE", 0);}
       cv.put("PHASE", phaseValue);
       if(foodNotificationValue){cv.put("FOOD_NOTIFICATON", 1);}
       else{cv.put("FOOD_NOTIFICATON", 0);}
       if(sleepNotificationValue){cv.put("SLEEP_NOTIFICATON", 1);}
       else{cv.put("SLEEP_NOTIFICATON", 0);}
       if(waterNotificationValue){cv.put("WATER_NOTIFICATON", 1);}
       else{cv.put("WATER_NOTIFICATON", 0);}

       db.update("APP_SETTINGS", cv, null, null);

       userCursor.close();
       db.close();

       Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

       selectedPhase = phaseValue;  //should be removed in future
       selectedDiet = dietTypeValue; //should be removed in future
    }

}

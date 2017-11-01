package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fatburner.fatburner.broadcast_receivers.NotificationEventReceiver;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;
import static com.fatburner.fatburner.GlobalVariables.dietListViewMode;
import static com.fatburner.fatburner.GlobalVariables.selectedDiet;
import static com.fatburner.fatburner.GlobalVariables.selectedPhase;
import static com.fatburner.fatburner.GlobalVariables.selectedDayId;
import static com.fatburner.fatburner.GlobalVariables.selectedMealId;


public class MealCalendarPageFragment extends Fragment implements View.OnClickListener {


    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    String breakfestItems[];
    String secondBreakFestItems[];
    String lunchItems[];
    String secondLunchItems[];
    String dinnerItems[];

    Intent intent;

    int hour;
    int min;
    int timerIdForApply;

    String legend = "Категории продуктов:\n П - мясо/рыба/яйца \n К - каши/крупы \n М - молочные \n Ф - фрукты/ягоды \n О - орехи \n Ж - масла/жиры";

    Spinner breakfestShema;
    Spinner secondBreakfestShema;
    Spinner lunchShema;
    Spinner secondLunchShema;
    Spinner dinnerShema;

    TextView breakfestTime;
    TextView secondBreakfestTime;
    TextView lunchTime;
    TextView secondLunchTime;
    TextView dinnerTime;
    TextView legendLabel;

    TextView breakfestCalLabel;
    TextView secondBreakfestCalLabel;
    TextView lunchCalLabel;
    TextView secondLunchCalLabel;
    TextView dinnerCalLabel;

    ImageButton applyButton;
    ImageButton breakfestButton;
    ImageButton secondBreakfestButton;
    ImageButton lunchButton;
    ImageButton secondLunchButton;
    ImageButton dinnerButton;


    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";

    int pageNumber;

    static MealCalendarPageFragment newInstance(int page) {
        MealCalendarPageFragment pageFragment = new MealCalendarPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        int savedPageNumber = -1;
        if (savedInstanceState != null) {
            savedPageNumber = savedInstanceState.getInt(SAVE_PAGE_NUMBER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meal_settings_fragment, null);

        applyButton = (ImageButton) view.findViewById(R.id.applyMealCalendarBtn);
        applyButton.setOnClickListener(this);


        breakfestCalLabel = (TextView) view.findViewById(R.id.breakfestCalLabel);
        secondBreakfestCalLabel = (TextView) view.findViewById(R.id.secondBreakfestCalLabel);
        lunchCalLabel = (TextView) view.findViewById(R.id.lunchCalLabel);
        secondLunchCalLabel = (TextView) view.findViewById(R.id.secondLunchCalLabel);
        dinnerCalLabel = (TextView) view.findViewById(R.id.dinnerCalLabel);

        breakfestButton = (ImageButton) view.findViewById(R.id.breakfestBtn);
        breakfestButton.setOnClickListener(this);
        secondBreakfestButton = (ImageButton) view.findViewById(R.id.secondBreakfestBtn);
        secondBreakfestButton.setOnClickListener(this);
        lunchButton = (ImageButton) view.findViewById(R.id.lunchBtn);
        lunchButton.setOnClickListener(this);
        secondLunchButton = (ImageButton) view.findViewById(R.id.secondLunchBtn);
        secondLunchButton.setOnClickListener(this);
        dinnerButton = (ImageButton) view.findViewById(R.id.dinnerBtn);
        dinnerButton.setOnClickListener(this);



        breakfestTime = (TextView) view.findViewById(R.id.breakfestTime);
        breakfestTime.setOnClickListener(this);
        secondBreakfestTime = (TextView) view.findViewById(R.id.secondBreakfestTime);
        secondBreakfestTime.setOnClickListener(this);
        lunchTime = (TextView) view.findViewById(R.id.lunchTime);
        lunchTime.setOnClickListener(this);
        secondLunchTime = (TextView) view.findViewById(R.id.secondLunchTime);
        secondLunchTime.setOnClickListener(this);
        dinnerTime = (TextView) view.findViewById(R.id.dinnerTime);
        dinnerTime.setOnClickListener(this);

        legendLabel = (TextView) view.findViewById(R.id.legendLabel);
        legendLabel.setText(legend);


        breakfestShema = (Spinner) view.findViewById(R.id.breakfestShema);
        secondBreakfestShema = (Spinner) view.findViewById(R.id.secondBreakfestShema);
        lunchShema = (Spinner) view.findViewById(R.id.lunchShema);
        secondLunchShema = (Spinner) view.findViewById(R.id.secondLunchShema);
        dinnerShema = (Spinner) view.findViewById(R.id.dinnerShema);

        setMealShemas();

        setCalValues();

        loadSettings();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PAGE_NUMBER, pageNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void setCalValues(){
        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        userCursor =  db.rawQuery("select * from APP_SETTINGS", null);
        userCursor.moveToFirst();

        Integer useDiet =  userCursor.getInt(6); //useDiet flag
        Integer phase = userCursor.getInt(8); //phase

        db.close();
        userCursor.close();


        if(useDiet == 0) {
            breakfestCalLabel.setVisibility(View.INVISIBLE);
            secondBreakfestCalLabel.setVisibility(View.INVISIBLE);
            lunchCalLabel.setVisibility(View.INVISIBLE);
            secondLunchCalLabel.setVisibility(View.INVISIBLE);
            dinnerCalLabel.setVisibility(View.INVISIBLE);
        }
        else {

            switch (phase){
                case 0:
                    breakfestCalLabel.setText("200 Ккал");
                    secondBreakfestCalLabel.setText("100 Ккал");
                    lunchCalLabel.setText("400 Ккал");
                    secondLunchCalLabel.setText("100 Ккал");
                    dinnerCalLabel.setText("400 Ккал");
                    break;
                case 1:
                    breakfestCalLabel.setText("300 Ккал");
                    secondBreakfestCalLabel.setText("200 Ккал");
                    lunchCalLabel.setText("400 Ккал");
                    secondLunchCalLabel.setText("200 Ккал");
                    dinnerCalLabel.setText("400 Ккал");
                    break;
                case 2:
                    breakfestCalLabel.setText("400 Ккал");
                    secondBreakfestCalLabel.setText("200 Ккал");
                    lunchCalLabel.setText("600 Ккал");
                    secondLunchCalLabel.setText("200 Ккал");
                    dinnerCalLabel.setText("600 Ккал");
                    break;
            }
        }
    }


    private void setMealShemas(){

        //need to add reading from preferences in future.

        if(selectedDiet){
            switch (selectedPhase){
                case 0:
                    breakfestItems = getResources().getStringArray(R.array.carbohydratePhase1Meal1);
                    secondBreakFestItems = getResources().getStringArray(R.array.carbohydratePhase1Meal2);
                    lunchItems = getResources().getStringArray(R.array.carbohydratePhase1Meal3);
                    secondLunchItems = secondBreakFestItems;
                    dinnerItems = lunchItems;
                    break;
                case 1:
                    breakfestItems = getResources().getStringArray(R.array.carbohydratePhase2Meal1);
                    secondBreakFestItems = getResources().getStringArray(R.array.carbohydratePhase2Meal2);
                    lunchItems = getResources().getStringArray(R.array.carbohydratePhase2Meal3);
                    secondLunchItems = secondBreakFestItems;
                    dinnerItems = lunchItems;
                    break;
                case 2:
                    breakfestItems = getResources().getStringArray(R.array.carbohydratePhase3Meal1);
                    secondBreakFestItems = getResources().getStringArray(R.array.carbohydratePhase3Meal2);
                    lunchItems = getResources().getStringArray(R.array.carbohydratePhase3Meal3);
                    secondLunchItems = secondBreakFestItems;
                    dinnerItems = lunchItems;
                    break;
            }
        }
        else{
            switch (selectedPhase){
                case 0:
                    breakfestItems = getResources().getStringArray(R.array.proteinPhase1Meal1);
                    secondBreakFestItems = getResources().getStringArray(R.array.proteinPhase1Meal2);
                    lunchItems = getResources().getStringArray(R.array.proteinPhase1Meal3);
                    secondLunchItems = secondBreakFestItems;
                    dinnerItems = lunchItems;
                    break;
                case 1:
                    breakfestItems = getResources().getStringArray(R.array.proteinPhase2Meal1);
                    secondBreakFestItems = getResources().getStringArray(R.array.proteinPhase2Meal2);
                    lunchItems = getResources().getStringArray(R.array.proteinPhase2Meal3);
                    secondLunchItems = secondBreakFestItems;
                    dinnerItems = lunchItems;
                    break;
                case 2:
                    breakfestItems = getResources().getStringArray(R.array.proteinPhase3Meal1);
                    secondBreakFestItems = getResources().getStringArray(R.array.proteinPhase3Meal2);
                    lunchItems = getResources().getStringArray(R.array.proteinPhase3Meal3);
                    secondLunchItems = secondBreakFestItems;
                    dinnerItems = lunchItems;
                    break;
            }
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner_large_text, breakfestItems);
        adapter.setDropDownViewResource(R.layout.my_spinner_large_text);
        breakfestShema.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner_large_text, secondBreakFestItems);
        secondBreakfestShema.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner_large_text, lunchItems);
        lunchShema.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner_large_text, secondLunchItems);
        secondLunchShema.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.my_spinner_large_text, dinnerItems);
        dinnerShema.setAdapter(adapter);
    }



    public void onClick(View v) {

        Calendar calendar = Calendar.getInstance();
        dietListViewMode = false;
        String shema = null;

        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        selectedDayId = pageNumber;
        intent = new Intent(getActivity(), Products.class);

        switch (v.getId()){
            case R.id.breakfestTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show(); //true parameter for 24h format
                timerIdForApply = 1;
                break;
            case R.id.secondBreakfestTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show();
                timerIdForApply = 2;
                break;
            case R.id.lunchTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show();
                timerIdForApply = 3;
                break;
            case R.id.secondLunchTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show();
                timerIdForApply = 4;
                break;
            case R.id.dinnerTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show();
                timerIdForApply = 5;
                break;
            case R.id.breakfestBtn:
                //code for defining parameters of view
                shema = breakfestShema.getSelectedItem().toString();
                selectedMealId = 0;
                Utils.applyShemaProductsList(shema);
                startActivity(intent);
                break;
            case R.id.secondBreakfestBtn:
                //code for defining parameters of view
                shema = secondBreakfestShema.getSelectedItem().toString();
                selectedMealId = 1;
                Utils.applyShemaProductsList(shema);
                startActivity(intent);
                break;
            case R.id.lunchBtn:
                //code for defining parameters of view
                shema = lunchShema.getSelectedItem().toString();
                selectedMealId = 2;
                Utils.applyShemaProductsList(shema);
                startActivity(intent);
                break;
            case R.id.secondLunchBtn:
                //code for defining parameters of view
                shema = secondLunchShema.getSelectedItem().toString();
                selectedMealId = 3;
                Utils.applyShemaProductsList(shema);
                startActivity(intent);
                break;
            case R.id.dinnerBtn:
                //code for defining parameters of view
                shema = dinnerShema.getSelectedItem().toString();
                selectedMealId = 4;
                Utils.applyShemaProductsList(shema);
                startActivity(intent);
                break;

            case R.id.applyMealCalendarBtn:
                saveMealSettings();
                NotificationEventReceiver.cancelAlarm(getContext());
                NotificationEventReceiver.setupAlarm(getContext(), Utils.getNotificationTime(getContext(), Utils.getCurrentTime()));

                break;
        }
    }

    TimePickerDialog.OnTimeSetListener onStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            hour = hourOfDay;
            min = minute;
            setTimeValues();
        }
    };


    private void setTimeValues(){

        String incrementedHour;
        String decrementedHour;

        String h = String.valueOf(hour);
        String m = String.valueOf(min);


        if(hour < 10)
        {h = "0" + h;}
        if(min < 10)
        {m = "0" + m;}
        switch (timerIdForApply){
            case 1:
                breakfestTime.setText(h + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 3, true);
                secondBreakfestTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 6, true);
                lunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 9, true);
                secondLunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 12, true);
                dinnerTime.setText(incrementedHour + ":" + m);
                break;
            case 2:
                decrementedHour = Utils.twoHoursIncDec(hour, 3, false);
                breakfestTime.setText(decrementedHour + ":" + m);
                secondBreakfestTime.setText(h + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 3, true);
                lunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 6, true);
                secondLunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 9, true);
                dinnerTime.setText(incrementedHour + ":" + m);
                break;
            case 3:
                lunchTime.setText(h + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 3, true);
                secondLunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 6, true);
                dinnerTime.setText(incrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 3, false);
                secondBreakfestTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 6, false);
                breakfestTime.setText(decrementedHour + ":" + m);

                break;
            case 4:
                secondLunchTime.setText(h + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 3, true);
                dinnerTime.setText(incrementedHour + ":" + m);

                decrementedHour = Utils.twoHoursIncDec(hour, 3, false);
                lunchTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 6, false);
                secondBreakfestTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 9, false);
                breakfestTime.setText(decrementedHour + ":" + m);

                break;
            case 5:
                dinnerTime.setText(h + ":" + m);

                decrementedHour = Utils.twoHoursIncDec(hour, 3, false);
                secondLunchTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 6, false);
                lunchTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 9, false);
                secondBreakfestTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 12, false);
                breakfestTime.setText(decrementedHour + ":" + m);

                break;
        }

    }

    private void saveMealSettings(){

        Integer dayId = selectedDayId;

        List<Integer> shemasList = new ArrayList<>();
        List<String> timesList = new ArrayList<>();


        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        // clear DB
        userCursor = db.query("MEAL_SETTINGS", null, "DAY = ?", new String[]{String.valueOf(dayId)}, null, null, null);

        if (userCursor.moveToFirst()) {
            do {
                try {
                    db.delete("MEAL_SETTINGS", "DAY = ?", new String[] { String.valueOf(dayId) });
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            } while (userCursor.moveToNext());
        }


        //insert into db

        shemasList.add(breakfestShema.getSelectedItemPosition());
        shemasList.add(secondBreakfestShema.getSelectedItemPosition());
        shemasList.add(lunchShema.getSelectedItemPosition());
        shemasList.add(secondLunchShema.getSelectedItemPosition());
        shemasList.add(dinnerShema.getSelectedItemPosition());

        timesList.add(breakfestTime.getText().toString());
        timesList.add(secondBreakfestTime.getText().toString());
        timesList.add(lunchTime.getText().toString());
        timesList.add(secondLunchTime.getText().toString());
        timesList.add(dinnerTime.getText().toString());


        for(int i = 0; i < shemasList.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put("DAY", dayId);
            cv.put("MEAL", i);
            cv.put("SHEMA", shemasList.get(i));
            cv.put("TIME", timesList.get(i));

            db.insert("MEAL_SETTINGS", null, cv);
        }


        shemasList.clear();
        timesList.clear();

        userCursor.close();
        db.close();

        Toast.makeText(getContext(), "Настройки для этого дня сохранены", Toast.LENGTH_SHORT).show();

    }

    private void loadSettings() {

        Integer dayId = getArguments().getInt(ARGUMENT_PAGE_NUMBER);;

        List<Integer> shemasList = new ArrayList<>();
        List<String> timesList = new ArrayList<>();

        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        // clear DB
        userCursor = db.query("MEAL_SETTINGS", null, "DAY = ?", new String[]{String.valueOf(dayId)}, null, null, null);

        Integer a = userCursor.getCount();

        if (userCursor.getCount() == 0)
        {
            return;
        }

        if (userCursor.moveToFirst()) {
            do {
                shemasList.add(userCursor.getInt(2));
                timesList.add(userCursor.getString(4));
            } while (userCursor.moveToNext());
        }

        breakfestShema.setSelection(shemasList.get(0));
        secondBreakfestShema.setSelection(shemasList.get(1));
        lunchShema.setSelection(shemasList.get(2));
        secondLunchShema.setSelection(shemasList.get(3));
        dinnerShema.setSelection(shemasList.get(4));

        breakfestTime.setText(timesList.get(0).toString());
        secondBreakfestTime.setText(timesList.get(1).toString());
        lunchTime.setText(timesList.get(2).toString());
        secondLunchTime.setText(timesList.get(3).toString());
        dinnerTime.setText(timesList.get(4).toString());

        shemasList.clear();
        timesList.clear();

        userCursor.close();
        db.close();

    }

}


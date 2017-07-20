package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
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
                //code for saving preferences
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


    private void setTimeValues(){ //crash

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
                incrementedHour = Utils.twoHoursIncDec(hour, 2, true);
                secondBreakfestTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 4, true);
                lunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 6, true);
                secondLunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 8, true);
                dinnerTime.setText(incrementedHour + ":" + m);
                break;
            case 2:
                decrementedHour = Utils.twoHoursIncDec(hour, 2, false);
                breakfestTime.setText(decrementedHour + ":" + m);
                secondBreakfestTime.setText(h + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 2, true);
                lunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 4, true);
                secondLunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 6, true);
                dinnerTime.setText(incrementedHour + ":" + m);
                break;
            case 3:
                lunchTime.setText(h + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 2, true);
                secondLunchTime.setText(incrementedHour + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 4, true);
                dinnerTime.setText(incrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 2, false);
                secondBreakfestTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 4, false);
                breakfestTime.setText(decrementedHour + ":" + m);

                break;
            case 4:
                secondLunchTime.setText(h + ":" + m);
                incrementedHour = Utils.twoHoursIncDec(hour, 2, true);
                dinnerTime.setText(incrementedHour + ":" + m);

                decrementedHour = Utils.twoHoursIncDec(hour, 2, false);
                lunchTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 4, false);
                secondBreakfestTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 6, false);
                breakfestTime.setText(decrementedHour + ":" + m);

                break;
            case 5:
                dinnerTime.setText(h + ":" + m);

                decrementedHour = Utils.twoHoursIncDec(hour, 2, false);
                secondLunchTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 4, false);
                lunchTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 6, false);
                secondBreakfestTime.setText(decrementedHour + ":" + m);
                decrementedHour = Utils.twoHoursIncDec(hour, 8, false);
                breakfestTime.setText(decrementedHour + ":" + m);

                break;
        }

    }

    }


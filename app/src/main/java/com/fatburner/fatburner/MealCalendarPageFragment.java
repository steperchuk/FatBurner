package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.app.TimePickerDialog;
import android.content.Intent;
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

import static com.fatburner.fatburner.GlobalVariables.dietListViewMode;


public class MealCalendarPageFragment extends Fragment implements View.OnClickListener {

    Intent intent;

    int hour;
    int min;
    int timerIdForApply;

    String legend = " П - мясо/рыба/яйца \n К - крупы \n М - молочные \n Ф - фрукты/ягоды \n О - орехи \n Ж - масла/жиры";

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


    private void setMealShemas(){
        String breakfestItems[] = {"1М + 1К", "1П + 1К","1П + 1Ф", "1M + 1O"};
        String secondBreakFestItems[] = {"1M", "1Ф + 0.5О", "1К"};
        String lunchItems[] = {"3К + 1П", "2К + 1П + 1Ф + 1Ж", "2К + 1М + 1П", "2К + 1П + 1О", "2К + 1П + 2Ж", "1К + 2Ф + 1Ж + 1П", "1К + 1Ф + 1М + 1П", "1К + 1Ф + 1П + 1О"};
        String secondLunchItems[] = {"1M", "1Ф + 0.5О", "1К"};
        String dinnerItems[] = lunchItems;

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, breakfestItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        breakfestShema.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, secondBreakFestItems);
        secondBreakfestShema.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lunchItems);
        lunchShema.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, secondLunchItems);
        secondLunchShema.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dinnerItems);
        dinnerShema.setAdapter(adapter);

    }



    public void onClick(View v) {

        Calendar calendar = Calendar.getInstance();
        dietListViewMode = false;
        switch (v.getId()){
            case R.id.breakfestTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false).show();
                timerIdForApply = 1;
                break;
            case R.id.secondBreakfestTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false).show();
                timerIdForApply = 2;
                break;
            case R.id.lunchTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false).show();
                timerIdForApply = 3;
                break;
            case R.id.secondLunchTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false).show();
                timerIdForApply = 4;
                break;
            case R.id.dinnerTime:
                new TimePickerDialog(getActivity(), onStartTimeListener, calendar
                        .get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false).show();
                timerIdForApply = 5;
                break;
            case R.id.breakfestBtn:
                //code for defining parameters of view

                intent = new Intent(getActivity(), Products.class);
                startActivity(intent);
                break;
            case R.id.secondBreakfestBtn:
                //code for defining parameters of view

                intent = new Intent(getActivity(), Products.class);
                startActivity(intent);
                break;
            case R.id.lunchBtn:
                //code for defining parameters of view

                intent = new Intent(getActivity(), Products.class);
                startActivity(intent);
                break;
            case R.id.secondLunchBtn:
                //code for defining parameters of view

                intent = new Intent(getActivity(), Products.class);
                startActivity(intent);
                break;
            case R.id.dinnerBtn:
                //code for defining parameters of view

                intent = new Intent(getActivity(), Products.class);
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


    private void setTimeValues(){
        String AM_PM = "";
        String h = String.valueOf(hour);
        String m = String.valueOf(min);
        if(hour > 11)
        {AM_PM = "PM";}
        if(hour < 10)
        {h = "0" + h;}
        if(min < 10)
        {m = "0" + m;}
        switch (timerIdForApply){
            case 1:
                breakfestTime.setText(h + ":" + m + " " + AM_PM);
                break;
            case 2:
                secondBreakfestTime.setText(h + ":" + m + " " + AM_PM);
                break;
            case 3:
                lunchTime.setText(h + ":" + m + " " + AM_PM);
                break;
            case 4:
                secondLunchTime.setText(h + ":" + m + " " + AM_PM);
                break;
            case 5:
                dinnerTime.setText(h + ":" + m + " " + AM_PM);
                break;
        }

    }

    }


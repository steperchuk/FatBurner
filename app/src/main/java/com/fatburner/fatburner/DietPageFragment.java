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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import static android.content.Context.MODE_PRIVATE;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;
import static com.fatburner.fatburner.GlobalVariables.dietListViewMode;
import static com.fatburner.fatburner.GlobalVariables.selectedDiet;
import static com.fatburner.fatburner.GlobalVariables.selectedPhase;
import static com.fatburner.fatburner.R.id.textView;
import static com.fatburner.fatburner.R.id.view;


public class DietPageFragment extends Fragment {


    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";

    TextView meal1;
    TextView meal2;
    TextView meal3;
    TextView meal4;
    TextView meal5;

    int pageNumber;

    static DietPageFragment newInstance(int page) {
        DietPageFragment pageFragment = new DietPageFragment();
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
        View view = inflater.inflate(R.layout.diet_fragment, null);

        meal1 = (TextView) view.findViewById(R.id.meal1);
        meal2 = (TextView) view.findViewById(R.id.meal2);
        meal3 = (TextView) view.findViewById(R.id.meal3);
        meal4 = (TextView) view.findViewById(R.id.meal4);
        meal5 = (TextView) view.findViewById(R.id.meal5);

        String productsList = "Product 1 \nProduct2 \nProduct3  \nProduct4  \nProduct5";
        meal1.setText(productsList);
        meal2.setText(productsList);
        meal3.setText(productsList);
        meal4.setText(productsList);
        meal5.setText(productsList);


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



}


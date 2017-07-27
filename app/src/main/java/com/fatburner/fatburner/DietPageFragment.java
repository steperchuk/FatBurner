package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;
import static com.fatburner.fatburner.GlobalVariables.dietListViewMode;
import static com.fatburner.fatburner.GlobalVariables.globalProductsMap;
import static com.fatburner.fatburner.GlobalVariables.selectedDiet;
import static com.fatburner.fatburner.GlobalVariables.selectedPhase;
import static com.fatburner.fatburner.R.id.textView;



public class DietPageFragment extends Fragment {


    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    ListView breakfestList;
    ListView secondBreakfestList;
    ListView lunchList;
    ListView secondLunchList;
    ListView dinnerList;

    final String ATTRIBUTE_NAME = "name";
    final String ATTRIBUTE_NAME_INFO = "info";


    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";


    String mealList[] = {"Завтрак","Перекус","Обед","Перекус","Ужин"};

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


        breakfestList = (ListView) view.findViewById(R.id.breakfestList);
        secondBreakfestList = (ListView) view.findViewById(R.id.secondBreakfestList);
        lunchList = (ListView) view.findViewById(R.id.lunchList);
        secondLunchList = (ListView) view.findViewById(R.id.secondLunchList);
        dinnerList = (ListView) view.findViewById(R.id.dinnerList);

        fillList("0", breakfestList);
        fillList("1", secondBreakfestList);
        fillList("2", lunchList);
        fillList("3", secondLunchList);
        fillList("4", dinnerList);

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


    private String getProductsList(String mealIdentifier){

        String products = "";

        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();
        userCursor = db.query("DIET", null, "DAY = ? AND MEAL = ?", new String[]{String.valueOf(pageNumber), mealIdentifier}, null, null, null);

        if (userCursor.moveToFirst()) {
            do {
                products = products + userCursor.getString(userCursor.getColumnIndex("PRODUCT")) + "   - " +
                                      userCursor.getString(userCursor.getColumnIndex("WEIGHT")) + "\n";
            } while (userCursor.moveToNext());
        }

        return products;
    }

    private void fillList(String mealIdentifier, ListView list){
        String productsList = getProductsList(mealIdentifier);
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(1);
        Map<String, Object> m;
        for (int i = 0; i < 1; i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME, mealList[Integer.parseInt(mealIdentifier)]);
            m.put(ATTRIBUTE_NAME_INFO, productsList);
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME,ATTRIBUTE_NAME_INFO};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.dayTitle, R.id.products };

        // создаем адаптер
        SimpleAdapter sAdapter = new SimpleAdapter(getContext(), data, R.layout.list_row_products_diet, from, to);
        list.setAdapter(sAdapter);

    }

}


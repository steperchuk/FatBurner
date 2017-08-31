package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

    CheckBox checkBoxMealStatus;

    final String ATTRIBUTE_NAME = "name";
    final String ATTRIBUTE_NAME_INFO = "info";
    final String ATTRIBUTE_STATUS = "status";


    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";


    String mealList[] = {"Завтрак", "Перекус", "Обед", "Перекус", "Ужин"};

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

        ListView.OnItemClickListener mOnListClick = new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if(pageNumber != Utils.getCurrentDayID()-1){

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder
                            .setMessage("Статус может быть сохранен только для текущего дня.")
                            .setCancelable(false)
                            .setIcon(R.drawable.ic_information)
                            .setNegativeButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    return;
                }


                if (view != null) {
                    checkBoxMealStatus = (CheckBox) view.findViewById(R.id.checkBoxMealStatus);
                    checkBoxMealStatus.setChecked(!checkBoxMealStatus.isChecked());
                }

                String mealID = null;
                int listID = parent.getId();
                switch (listID){
                    case 2131689699:
                        mealID = "0";
                        break;
                    case 2131689700:
                        mealID = "1";
                        break;
                    case 2131689701:
                        mealID = "2";
                        break;
                    case 2131689702:
                        mealID = "3";
                        break;
                    case 2131689703:
                        mealID = "4";
                        break;
                }
                saveMealStatus(mealID);


                Integer progress = Integer.valueOf(loadProgressForCurretDay());
                if(checkBoxMealStatus.isChecked())
                {
                    progress = progress + 20;
                }
                else
                {
                    progress = progress - 20;
                }

                setProgressForCurrentDay(progress);
            }
        };


        fillList("0", breakfestList);
        fillList("1", secondBreakfestList);
        fillList("2", lunchList);
        fillList("3", secondLunchList);
        fillList("4", dinnerList);

        breakfestList.setOnItemClickListener(mOnListClick);
        secondBreakfestList.setOnItemClickListener(mOnListClick);
        lunchList.setOnItemClickListener(mOnListClick);
        secondLunchList.setOnItemClickListener(mOnListClick);
        dinnerList.setOnItemClickListener(mOnListClick);

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


    private String getProductsList(String mealIdentifier) {

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

    private Boolean getMealStatus(String mealIdentifier) {

        Boolean status = false;
        String statusInDB = "false";
        String date = "";

        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();
        userCursor = db.query("DIET", null, "DAY = ? AND MEAL = ?", new String[]{String.valueOf(pageNumber), mealIdentifier}, null, null, null);

        if (userCursor.moveToFirst()) {
            do {
                statusInDB = userCursor.getString(userCursor.getColumnIndex("MEAL_STATUS"));
                date = userCursor.getString(userCursor.getColumnIndex("DATE"));
            } while (userCursor.moveToNext());
        }

        if(!date.equals(Utils.getCurrentDate()))
        {
            return false;
        }

        if(statusInDB.equals("true"))
        {
            status = true;
        }
        return status;
    }

    private void fillList(String mealIdentifier, ListView list) {


        String productsList = getProductsList(mealIdentifier);
        Boolean mealStatus = getMealStatus(mealIdentifier);


        if (productsList.isEmpty() || productsList.equals("   - "+"\n")) {
            productsList = "Продукты не выбраны";
        }
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(1);
        Map<String, Object> m;
        for (int i = 0; i < 1; i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME, mealList[Integer.parseInt(mealIdentifier)]);
            m.put(ATTRIBUTE_NAME_INFO, productsList);
            m.put(ATTRIBUTE_STATUS, mealStatus);
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = {ATTRIBUTE_NAME, ATTRIBUTE_NAME_INFO, ATTRIBUTE_STATUS};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = {R.id.dayTitle, R.id.products, R.id.checkBoxMealStatus};

        // создаем адаптер
        SimpleAdapter sAdapter = new SimpleAdapter(getContext(), data, R.layout.list_row_products_diet, from, to);
        list.setAdapter(sAdapter);

    }

    private String loadProgressForCurretDay() {

        String dietStatus = "0";
        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        userCursor = db.query("CALENDAR", null, "DATE = ?", new String[]{Utils.getCurrentDate()}, null, null, null);

        if (userCursor.getCount() != 0) {

            userCursor.moveToFirst();
            dietStatus = userCursor.getString(5);
        }
        userCursor.close();
        db.close();

        return dietStatus;
    }

    private void setProgressForCurrentDay(Integer progress){
        db = databaseHelper.open();
        ContentValues cv = new ContentValues();
        cv.put("FOOD_STATUS", progress);
        db.update("CALENDAR", cv, "DATE = ?" , new String[]{Utils.getCurrentDate()});

        databaseHelper.close();
        db.close();
    }

    private void saveMealStatus(String mealIdentifier){
        boolean status = checkBoxMealStatus.isChecked();
        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();
        ContentValues cv = new ContentValues();
        cv.put("MEAL_STATUS", String.valueOf(status));
        cv.put("DAY", String.valueOf(pageNumber));
        cv.put("MEAL", mealIdentifier);
        cv.put("DATE", Utils.getCurrentDate());

        userCursor = db.query("DIET", null, "DAY = ? AND MEAL = ?", new String[]{String.valueOf(pageNumber), mealIdentifier}, null, null, null);

        if (userCursor.getCount() !=0) {
            db.update("DIET", cv, "DAY = ? AND MEAL = ?", new String[]{String.valueOf(pageNumber), mealIdentifier});
        }
        else {
            cv.put("PRODUCT", "");
            cv.put("WEIGHT", "");
            cv.put("DATE", Utils.getCurrentDate());
            db.insert("DIET", null, cv);
        }

        databaseHelper.close();
        db.close();

    }

}


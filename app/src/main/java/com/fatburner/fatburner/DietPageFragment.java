package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;
import static com.fatburner.fatburner.GlobalVariables.dietListViewMode;
import static com.fatburner.fatburner.GlobalVariables.globalProductsMap;
import static com.fatburner.fatburner.GlobalVariables.selectedDiet;
import static com.fatburner.fatburner.GlobalVariables.selectedPhase;
import static com.fatburner.fatburner.R.id.textView;
import static com.fatburner.fatburner.R.id.view;


public class DietPageFragment extends Fragment {

    String days[] = { "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье" };
    String daysSelected = "";
    final int DIALOG_DAYS = 3;
    boolean chkd[] = { false, false, false, false, false, false, false };

    ListView dietList;
    ImageButton getTotalItemsList;

    final String ATTRIBUTE_NAME = "name";
    final String ATTRIBUTE_NAME_INFO = "info";


    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";


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


        String mealList[] = {"Завтрак","Перекус","Обед","Перекус","Ужин"};
        String productsList[] = {"Product 1\nProduct 2\nProduct 2\nProduct 2\nProduct 2\nProduct 2\nProduct 2"};


        dietList = (ListView) view.findViewById(R.id.dietList);
        getTotalItemsList = (ImageButton) view.findViewById(R.id.getTotalItemsList);

        getTotalItemsList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                showDialog(DIALOG_DAYS);
            }
        });




        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(mealList.length);
        Map<String, Object> m;
        for (int i = 0; i < mealList.length; i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME, mealList[i]);
            m.put(ATTRIBUTE_NAME_INFO, productsList[0]);
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTRIBUTE_NAME,ATTRIBUTE_NAME_INFO};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.dayTitle, R.id.products };

        // создаем адаптер
        SimpleAdapter sAdapter = new SimpleAdapter(getContext(), data, R.layout.list_row_products_diet, from, to);
        dietList.setAdapter(sAdapter);

        return view;
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);


        if (id == DIALOG_DAYS)
        {
            adb.setTitle("Дни недели");
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
            int atLeastOneChecked = 0;
            SparseBooleanArray sbArray = ((AlertDialog)dialog).getListView().getCheckedItemPositions();
            for (int i = 0; i < sbArray.size(); i++) {
                int key = sbArray.keyAt(i);
                if (sbArray.get(key))
                {
                    daysSelected = daysSelected + days[key] + ", ";
                    atLeastOneChecked = 1;
                }
            }
            if(atLeastOneChecked == 0){
                dialog.cancel();
                return;
            }

                // perform products calculation according to selected days
                // perform PRODUCTS_ORDER table in DB filling. Need separate method.

            Intent intent = new Intent(getActivity(), ProductsOrder.class);
            startActivity(intent);

        }
    };


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


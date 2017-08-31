package com.fatburner.fatburner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.fatburner.fatburner.Diet.PAGE_COUNT;

public class Diet extends FragmentActivity {

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    List<String> products = new ArrayList<>();
    List<String> weights = new ArrayList<>();

    ImageButton getTotalItemsList;

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 7;
    String days[] = { "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье" };
    String daysSelected = "";
    final int DIALOG_DAYS = 3;
    boolean chkd[] = { false, false, false, false, false, false, false };

    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);


        getTotalItemsList = (ImageButton) findViewById(R.id.getTotalItemsList);

        getTotalItemsList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                showDialog(DIALOG_DAYS);
            }
        });




        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new DietPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(Utils.getCurrentDayID()-1);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Diet.this, TrainingsCalendar.class);
        startActivity(intent);
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
            daysSelected = "";
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

            calculateProducts(daysSelected);

            Intent intent = new Intent(Diet.this, ProductsOrder.class);
            startActivity(intent);

        }
    };


    void calculateProducts(String daysSelected){
        products.clear();
        weights.clear();

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        if(daysSelected.contains("Пон")){
            userCursor = db.query("DIET", null, "DAY = ?", new String[]{"0"}, null, null, null);
            getProducts(userCursor);}
        if(daysSelected.contains("Вто")){
            userCursor = db.query("DIET", null, "DAY = ?", new String[]{"1"}, null, null, null);
            getProducts(userCursor);}
        if(daysSelected.contains("Сре")){
            userCursor = db.query("DIET", null, "DAY = ?", new String[]{"2"}, null, null, null);
            getProducts(userCursor);}
        if(daysSelected.contains("Чет")){
            userCursor = db.query("DIET", null, "DAY = ?", new String[]{"3"}, null, null, null);
            getProducts(userCursor);}
        if(daysSelected.contains("Пят")){
            userCursor = db.query("DIET", null, "DAY = ?", new String[]{"4"}, null, null, null);
            getProducts(userCursor);}
        if(daysSelected.contains("Суб")){
            userCursor = db.query("DIET", null, "DAY = ?", new String[]{"5"}, null, null, null);
            getProducts(userCursor);}
        if(daysSelected.contains("Вос")){
            userCursor = db.query("DIET", null, "DAY = ?", new String[]{"6"}, null, null, null);
            getProducts(userCursor);}

        Set set = new HashSet(products);
        List<String> productsList = new ArrayList<String>(set);
        List<String> weightsList = performWeightCalulation(products);


        userCursor =  db.rawQuery("select * from PRODUCTS_ORDER", null);

        if (userCursor.moveToFirst()) {
            do {
                try {
                    db.delete("PRODUCTS_ORDER", null, null);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            } while (userCursor.moveToNext());
        }


        for(int i = 0; i < productsList.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put("IS_ORDERED", "0");
            cv.put("PRODUCT", productsList.get(i));
            cv.put("WEIGHT", weightsList.get(i));

            db.insert("PRODUCTS_ORDER", null, cv);
        }

        userCursor.close();
        db.close();


    }

    void getProducts(Cursor userCursor){
        String product = "";
        if (userCursor.moveToFirst()) {
            do {
                product = userCursor.getString(userCursor.getColumnIndex("PRODUCT"));
                if(product.isEmpty()){continue;}
                else {
                    products.add(product);
                    weights.add(userCursor.getString(userCursor.getColumnIndex("WEIGHT")).replace("гр", ""));
                }
            } while (userCursor.moveToNext());
        }

    }

    private List<String> performWeightCalulation(List<String> list) {

        Set set = new HashSet(list);
        List<String> uniqueList = new ArrayList<String>(set);
        List<String> uniqueWeights = new ArrayList<>();



        for(int j = 0; j < uniqueList.size(); j++){
            int increment = 0;
            int weightValue = 0;
            String weightInDb = "";
            for(int i = 0; i < list.size(); i++){
                if(uniqueList.get(j).equals(list.get(i))) {increment++;}
                weightInDb = weights.get(products.indexOf(uniqueList.get(j))).trim();
                if(weightInDb.isEmpty())
                {continue;}
                else {weightValue = Integer.parseInt(weightInDb) * increment;}
            }
            uniqueWeights.add(String.valueOf(weightValue));
        }
        return uniqueWeights;
    }

}

class DietPagerAdapter extends FragmentPagerAdapter {

    public DietPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String day = "";
        switch (position){
            case 0:
                day = "Понедельник";
                break;
            case 1:
                day = "Вторник";
                break;
            case 2:
                day = "Среда";
                break;
            case 3:
                day = "Четверг";
                break;
            case 4:
                day = "Пятница";
                break;
            case 5:
                day = "Суббота";
                break;
            case 6:
                day = "Воскресенье";
                break;
        }
        return day;
    }

    @Override
    public Fragment getItem(int position) {
        return DietPageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}

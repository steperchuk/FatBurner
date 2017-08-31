package com.fatburner.fatburner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS;
import static com.fatburner.fatburner.GlobalVariables.globalProductsMap;

public class Products extends FragmentActivity {

    static final String TAG = "myLogs";
    //static final int PAGES_COUNT = 5;

    ViewPager pager;
    PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ProductsListPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean show = sp.getBoolean("showProductsAdvice", true);
        if(show) {
            ShowInfoDialog();
        }

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

    private void ShowInfoDialog(){
        final Dialog dialog = new Dialog(Products.this);
        dialog.setContentView(R.layout.modal_advice);
        dialog.setCancelable(true);

        CheckBox checkBoxSave = (CheckBox) dialog.findViewById(R.id.checkBoxSave);
        checkBoxSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    sp.putBoolean("showProductsAdvice", false);
                    sp.commit();
                }
            }
        });


        TextView exerciseLabel = (TextView) dialog.findViewById(R.id.label);
        exerciseLabel.setText("1. Вам необходимо выбрать продукты из каждой категории.\n\n" +
                              "2. Необходимый список продуктов отображен исходя из выбраной вами формулы.\n\n" +
                              "3. Для выбора доступно количество продуктов исходя из выбраной формулы.\n\n" +
                              "4. При превышении лимита количеста продуктов первый выбраный продукт будет заменен последним.\n");

        //set up button
        ImageButton button = (ImageButton) dialog.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //alertDialogBuilder.setTitle("Выйти из приложения?");
        alertDialogBuilder
                .setMessage("Вы уверены что сохранили список продуктов?")
                .setCancelable(false)
                .setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(Products.this, MealCalendar.class);
                                startActivity(intent);
                            }
                        })

                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

class ProductsListPagerAdapter extends FragmentPagerAdapter {

    //private static final int PAGES_COUNT = 6;

    public ProductsListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String product = "";
        switch (position){
            case 0:
                product = "<- ->"; //Протеин
                break;
            case 1:
                product = "<- ->"; //Каши/Крупы
                break;
            case 2:
                product = "<- ->"; //Молочные
                break;
            case 3:
                product = "<- ->"; //Фрукты/Овощи
                break;
            case 4:
                product = "<- ->"; //Орехи
                break;
            case 5:
                product = "<- ->"; //Жиры
                break;
        }
        return product;
    }

    @Override
    public Fragment getItem(int position) {
        return ProductsPageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PRODUCTS_PAGES_COUNT;
    }

}



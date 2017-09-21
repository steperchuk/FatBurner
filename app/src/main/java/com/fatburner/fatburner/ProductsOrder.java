package com.fatburner.fatburner;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.appodeal.ads.Appodeal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sete on 7/21/2017.
 */


public class ProductsOrder extends Menu {

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    int listLayout = android.R.layout.simple_list_item_multiple_choice;

    public DrawerLayout mDrawerLayout;


    ListView productsOrderList;
    Switch switchHideOrdered;

    List<String> productsList = new ArrayList<String>();
    boolean hideOrdered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_order);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_products_order, null, false);
        //mDrawerLayout.addView(contentView, 0);

        Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        //Appodeal.disableNetwork(this, "cheetah");

        productsOrderList = (ListView) findViewById(R.id.orders_list);
        switchHideOrdered = (Switch) findViewById(R.id.hideOrdred);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        hideOrdered = sp.getBoolean("hideOrdered", false);
        switchHideOrdered.setChecked(hideOrdered);

        productsOrderList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if(loadList() == 0){
            showDialog();
        }

        switchHideOrdered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hideOrdered = isChecked;

                SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                sp.putBoolean("hideOrdered", hideOrdered);
                sp.commit();

                loadList();
            }
        });


        productsOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                databaseHelper = new DatabaseHelper(getApplicationContext());
                databaseHelper.getWritableDatabase();
                db = databaseHelper.open();

                ContentValues cv = new ContentValues();

                if(productsOrderList.isItemChecked(position)) { cv.put("IS_ORDERED", 1);}
                else { cv.put("IS_ORDERED", 0);}

                db.update("PRODUCTS_ORDER", cv, "PRODUCT" + " = ?" , new String[]{getCurrentProductsList().get(position).toString()});

                loadList();

                db.close();
                databaseHelper.close();
            }
        });

    }

    private void showDialog(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            //alertDialogBuilder.setTitle("Выйти из приложения?");
            alertDialogBuilder
                    .setMessage("Продукты не выбраны в настройках диеты. Перейти к настройкам?")
                    .setCancelable(false)
                    .setPositiveButton("Да",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(ProductsOrder.this, MealCalendar.class);
                                    startActivity(intent);
                                }
                            })

                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent = new Intent(ProductsOrder.this, Diet.class);
                            startActivity(intent);
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
    }


    private Integer loadList(){

        productsList.clear();
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();


        if(hideOrdered)
        {
            userCursor = db.query("PRODUCTS_ORDER", null, "IS_ORDERED != ?", new String[] {String.valueOf(1)}, null, null, null);
        }
        else
        {
            userCursor =  db.rawQuery("select * from PRODUCTS_ORDER", null);
        }

        List<String> products = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        List<Integer> orderedList = new ArrayList<>();
        if (userCursor.moveToFirst()) {
            do {
                orderedList.add(userCursor.getInt(0));
                products.add(userCursor.getString(1) );
                weights.add(userCursor.getInt(2));
            } while (userCursor.moveToNext());
        }


        for (int i = 0; i < products.size(); i++){
            productsList.add(products.get(i) + "  -  " + weights.get(i) + " гр");
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                listLayout, productsList);

        // присваиваем адаптер списку
        productsOrderList.setAdapter(adapter);

        for (int i = 0; i < orderedList.size(); i++){
            if(orderedList.get(i) == 0)
            {
                productsOrderList.setItemChecked(i, false);
            }
            else{
                productsOrderList.setItemChecked(i, true);
            }
        }

        userCursor.close();
        db.close();

        return productsList.size();
    }

    private List<String> getCurrentProductsList(){

        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();

        if(hideOrdered)
        {
            userCursor = db.query("PRODUCTS_ORDER", null, "IS_ORDERED != ?", new String[] {String.valueOf(1)}, null, null, null);
        }
        else
        {
            userCursor =  db.rawQuery("select * from PRODUCTS_ORDER", null);
        }

        List<String> products = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            do {
                products.add(userCursor.getString(1) );
            } while (userCursor.moveToNext());
        }

        userCursor.close();
        db.close();

        return products;
    }
}
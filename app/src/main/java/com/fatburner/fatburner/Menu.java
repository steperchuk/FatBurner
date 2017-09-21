package com.fatburner.fatburner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import static com.fatburner.fatburner.GlobalVariables.TRAINING;
import static com.fatburner.fatburner.GlobalVariables.dietListViewMode;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;

public class Menu extends AppCompatActivity {

    public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;
    private NavigationView mNavigationView;

    String currentProgram = "";
    String currentTraining = "";
    Integer currentDiet = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        getMenuSettings();

        mDrawerLayout.setDrawerListener(mToogle);
        mToogle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupDrawerContent(mNavigationView);


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem item){
            Intent intent;
        getMenuSettings();
            String products[] = {"П", "К", "М", "Ф", "О", "Ж"};
            switch (item.getItemId()) {
                case R.id.nav_training:
                    intent = new Intent(Menu.this, TrainingsList.class);
                    intent.putExtra("openedFromMenu", true);
                    if(currentProgram != "") {
                    startActivity(intent);
                    }
                    else{
                        ModalDialogProgramNotSelected dialog = new ModalDialogProgramNotSelected();
                        dialog.show(getSupportFragmentManager(), "custom");
                        return;
                    }
                    break;
                case R.id.nav_challange:
                    intent = new Intent(Menu.this, SelectedTraining.class);
                    intent.putExtra("openedFromMenu", true);
                    if(currentTraining != "") {
                    startActivity(intent);
                    }
                    else{
                        ModalDialogNotSelected dialog = new ModalDialogNotSelected();
                        dialog.show(getSupportFragmentManager(), "custom");
                        return;
                    }
                    break;
                case R.id.nav_programs:
                    intent = new Intent(Menu.this, ProgramsList.class);
                    startActivity(intent);
                    break;
                case R.id.nav_trainings_calendar:
                    intent = new Intent(Menu.this, TrainingsCalendar.class);
                    startActivity(intent);
                    break;
                case R.id.nav_water:
                    intent = new Intent(Menu.this, Water.class);
                    startActivity(intent);
                    break;
                case R.id.nav_diet:
                    intent = new Intent(Menu.this, Diet.class);
                    if(currentDiet != 0) {
                        startActivity(intent);
                    }
                    else{
                        ModalDialogNotSelectedDiet dialog = new ModalDialogNotSelectedDiet();
                        dialog.show(getSupportFragmentManager(), "custom");
                        return;
                    }
                    break;
                case R.id.nav_meal_calendar:
                    dietListViewMode = false;
                    intent = new Intent(Menu.this, MealCalendar.class);
                    if(currentDiet != 0) {
                        startActivity(intent);
                    }
                    else{
                        ModalDialogNotSelectedDiet dialog = new ModalDialogNotSelectedDiet();
                        dialog.show(getSupportFragmentManager(), "custom");
                        return;
                    }
                    break;

                /*
                case R.id.products_list:
                    dietListViewMode = true;
                    PRODUCTS_PAGES_COUNT = 6;
                    PRODUCTS = products;
                    intent = new Intent(Menu.this, Products.class);
                    startActivity(intent);
                    break;
                */

                case R.id.nav_settings:
                    intent = new Intent(Menu.this, Settings.class);
                    startActivity(intent);
                    break;
            }
        }


       private void getMenuSettings(){
           DatabaseHelper databaseHelper;
           SQLiteDatabase db;
           Cursor userCursor;

           databaseHelper = new DatabaseHelper(getApplicationContext());
           databaseHelper.create_db();

           databaseHelper = new DatabaseHelper(this);
           databaseHelper.getWritableDatabase();
           db = databaseHelper.open();

           userCursor =  db.rawQuery("select * from PROGRAMMS where IS_CURRENT = ?", new String[]{String.valueOf(1)});
           int a = userCursor.getCount();
           if(userCursor.getCount() != 0)
           {
               userCursor.moveToFirst();
               currentProgram = userCursor.getString(0);
           }

           userCursor =  db.rawQuery("select PROGRAMM_NAME from TRAININGS where IS_CURRENT = 1", null);
           if(userCursor.getCount() != 0)
           {
               userCursor.moveToFirst();
               currentTraining = userCursor.getString(0);
           }

           userCursor =  db.rawQuery("select * from APP_SETTINGS", null);
           if(userCursor.getCount() != 0)
           {
               userCursor.moveToFirst();
               currentDiet = userCursor.getInt(6);
           }

           userCursor.close();
           db.close();

        }


    }



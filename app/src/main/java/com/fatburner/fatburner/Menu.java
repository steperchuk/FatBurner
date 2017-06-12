package com.fatburner.fatburner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class Menu extends AppCompatActivity {

    public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;
    private NavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToogle);
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
            switch (item.getItemId()) {
                case R.id.nav_achivements:
                    intent = new Intent(Menu.this, MyAchivement.class);
                    startActivity(intent);
                    break;
                case R.id.nav_challange:
                    intent = new Intent(Menu.this, TrainingsList.class);
                    startActivity(intent);
                    break;
                case R.id.nav_calendar:
                    intent = new Intent(Menu.this, MealCalendar.class);
                    startActivity(intent);
                    break;
                case R.id.nav_diet:
                    intent = new Intent(Menu.this, Diet.class);
                    startActivity(intent);
                    break;
                case R.id.nav_programs:
                    intent = new Intent(Menu.this, MyPrograms.class);
                    startActivity(intent);
                    break;
                case R.id.nav_settings:
                    intent = new Intent(Menu.this, Settings.class);
                    startActivity(intent);
                    break;
            }
        }

    }



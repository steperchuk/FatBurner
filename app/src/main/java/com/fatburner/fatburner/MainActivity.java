package com.fatburner.fatburner;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToogle.onOptionsItemSelected(item)){
            return true;
        }

        Intent intent;
        switch(item.getItemId()) {
            case R.id.nav_achivements:
                intent = new Intent(MainActivity.this, MyAchivement.class);
                startActivity(intent);
                break;
            case R.id.nav_chanlange:
                intent = new Intent(MainActivity.this, CurrentChalange.class);
                startActivity(intent);
                break;
            case R.id.nav_calendar:
                intent = new Intent(MainActivity.this, MealCalendar.class);
                startActivity(intent);
                break;
            case R.id.nav_diet:
                intent = new Intent(MainActivity.this, Diet.class);
                startActivity(intent);
                break;
            case R.id.nav_programs:
                intent = new Intent(MainActivity.this, CurrentChalange.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }


}


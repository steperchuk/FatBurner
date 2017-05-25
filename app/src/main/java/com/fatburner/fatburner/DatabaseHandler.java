package com.fatburner.fatburner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sergeyteperchuk on 5/25/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "settingsManager";

    // Table name
    private static final String TABLE_SETTINGS = "settings";

    // Settings Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AGE = "age";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_DAY_START = "dayStart";
    private static final String KEY_TRAINING_START = "trainingStart";
    private static final String KEY_TRAINING_DAYS = "trainingDays";
    private static final String KEY_WATER_NOTIFICATION = "waterNotification";
    private static final String KEY_FOOD_NOTIFICATION = "foodNotification";
    private static final String KEY_SLEEP_NOTIFICATION = "sleepNotification";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables

    public void onCreate(SQLiteDatabase db) {
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GENDER + " TEXT,"
                + KEY_AGE + " TEXT" + KEY_WEIGHT + " TEXT" + KEY_DAY_START + " TEXT" + KEY_TRAINING_START + " TEXT"
                + KEY_TRAINING_DAYS + " TEXT" + KEY_WATER_NOTIFICATION + " TEXT" + KEY_FOOD_NOTIFICATION + " TEXT"
                + KEY_SLEEP_NOTIFICATION + " TEXT" + ")";
        db.execSQL(CREATE_SETTINGS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);

        // Create tables again
        onCreate(db);
    }

    public void addSettings(SettingsProvider settings){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, 0);
        values.put(KEY_GENDER, settings.getGender());
        values.put(KEY_AGE, settings.getAge());
        values.put(KEY_WEIGHT, settings.getWeight());
        values.put(KEY_DAY_START, settings.getDayStartTime());
        values.put(KEY_TRAINING_START, settings.getTrainingStartTime());
        values.put(KEY_TRAINING_DAYS, settings.getTrainingDays());
        values.put(KEY_WATER_NOTIFICATION, settings.getShowWaterNotification());
        values.put(KEY_FOOD_NOTIFICATION, settings.getShowFoodNotification());
        values.put(KEY_SLEEP_NOTIFICATION, settings.getShowSleepNotification());

        // Inserting Row
        db.insert(TABLE_SETTINGS, null, values);
        db.close(); // Closing database connection

    }


    public SettingsProvider getSettings() {
        SettingsProvider settingsList = new SettingsProvider();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                settingsList.setGender(Boolean.parseBoolean(cursor.getString(1)));
                settingsList.setAge(cursor.getString(2));
                settingsList.setWeight(cursor.getString(3));
                settingsList.setDayStartTime(cursor.getString(4));
                settingsList.setTrainingStartTime(cursor.getString(5));
                settingsList.setTrainingDays(cursor.getString(6));
                settingsList.setShowWaterNotification(Boolean.parseBoolean(cursor.getString(7)));
                settingsList.setShowFoodNotification(Boolean.parseBoolean(cursor.getString(8)));
                settingsList.setShowSleepNotification(Boolean.parseBoolean(cursor.getString(9)));
            } while (cursor.moveToNext());
        }

        // return settings object
        return settingsList;
    }


    public int updateSettings(SettingsProvider settings) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GENDER, settings.getGender());
        values.put(KEY_AGE, settings.getAge());
        values.put(KEY_WEIGHT, settings.getWeight());
        values.put(KEY_DAY_START, settings.getDayStartTime());
        values.put(KEY_TRAINING_START, settings.getTrainingStartTime());
        values.put(KEY_TRAINING_DAYS, settings.getTrainingDays());
        values.put(KEY_WATER_NOTIFICATION, settings.getShowWaterNotification());
        values.put(KEY_FOOD_NOTIFICATION, settings.getShowFoodNotification());
        values.put(KEY_SLEEP_NOTIFICATION, settings.getShowSleepNotification());

        // updating row
        return db.update(TABLE_SETTINGS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(0) });
    }


}

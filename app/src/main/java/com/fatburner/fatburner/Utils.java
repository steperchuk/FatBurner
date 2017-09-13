package com.fatburner.fatburner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_COUNT;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS;

/**
 * Created by sergeyteperchuk on 6/20/17.
 */

public class Utils {


    public static void applyShemaProductsList(String Shema){
        List<String> lettersArray = new ArrayList<String>();
        Map<String,Integer> productsCount = new ArrayMap<>();
        for(int i = 0; i < Shema.length(); i++){
            if(Character.isLetter(Shema.charAt(i)))
            {
                String l = "" + Shema.charAt(i);
                if(!l.equals("+"))
                {

                    int index = 0 + Character.getNumericValue(Shema.charAt(i-1));
                    lettersArray.add(l);
                    productsCount.put(l,index);
                }
            }
        }
        PRODUCTS_PAGES_COUNT = lettersArray.size();
        String products[] =  new String[lettersArray.size()];;
        products = lettersArray.toArray(products);
        PRODUCTS = products;
        PRODUCTS_COUNT = productsCount;
    }


    //Пшеничная каша      - 60гр  - 81Ккал

    public static ArrayList<ArrayList<String>> normalizeProductsList(String[] array)
    {
        ArrayList<String> productNames = new ArrayList<>();
        ArrayList<String> productWeights = new ArrayList<>();
        ArrayList<String> productCaloricitys = new ArrayList<>();

        ArrayList<ArrayList<String>> parsedproductsList = new ArrayList<ArrayList<String>>();

        for (int i=0; i< array.length; i++) {
            String arrayItem = array[i];

            arrayItem = arrayItem.trim();
            String productName = arrayItem.substring(0, arrayItem.indexOf("-"));
            arrayItem = arrayItem.replace(productName + "-", "");
            String productWeight = arrayItem.substring(0, arrayItem.indexOf("-"));
            arrayItem = arrayItem.replace(productName + "-", "");
            String productCaloricity = arrayItem.substring(0, arrayItem.indexOf("-"));

            productNames.add(productName);
            productWeights.add(productWeight);
            productCaloricitys.add(productCaloricity);
        }

        parsedproductsList.add(productNames);
        parsedproductsList.add(productWeights);
        parsedproductsList.add(productCaloricitys);

        return parsedproductsList;
    }

    public static String[] normalizeProductsList(Map<Integer, List<String>> listOfProducts){

        ArrayList<List<String>> list = new ArrayList<List<String>>();
        List<String> normalizedList = new ArrayList<String>();
        List<String> l = new ArrayList<String>();

        for(int i = 0; i< listOfProducts.size(); i++){
            list.add(listOfProducts.get(i));
        }

        for (int i = 0; i < list.size(); i++){
            l = list.get(i);

            for (String item : l) {
                if(!item.equals(" ")){
                    normalizedList.add(item);
                }
            }
        }

       return  normalizedList.toArray(new String[0]);

    }


    public static boolean[] getCheckedDays(String daysValue){
        boolean chkd[] = { false, false, false, false, false, false, false };
        if(daysValue.contains("Пон")){chkd[0] = true;};
        if(daysValue.contains("Вто")){chkd[1] = true;};
        if(daysValue.contains("Сре")){chkd[2] = true;};
        if(daysValue.contains("Чет")){chkd[3] = true;};
        if(daysValue.contains("Пят")){chkd[4] = true;};
        if(daysValue.contains("Суб")){chkd[5] = true;};
        if(daysValue.contains("Вос")){chkd[6] = true;};
        return chkd;
    }


    public static String twoHoursIncDec(Integer h, Integer increment, boolean forward ){
        String result = "";
        Integer hour = 0;
        if(forward){
            hour = h + increment;
        }
        else{
            hour = h - increment;
        }
        if(hour > 23){hour = (h + increment) - 24;}
        if(hour < 0){hour = 24 + hour;}
        if(hour < 10) {result = "0" + hour.toString();}
        else {result = hour.toString();}

        return result;
    }


    public static String parseGoalValue(int value){
        String goal ="";
        switch (value){
            case 0:
                goal = "Увеличение силы";
                break;
            case 1:
                goal = "Поддержание формы";
                break;
            case 2:
                goal = "Подготовительная";
                break;
            case 3:
                goal = "Набор Массы";
                break;
            case 4:
                goal = "Жиросжигание";
                break;
        }
        return goal;
    }

    public static String parseDifficultyValue(int value){
        String difficulty ="";
        switch (value){
            case 0:
                difficulty = "Новичок";
                break;
            case 1:
                difficulty = "Любитель";
                break;
            case 2:
                difficulty = "Профессионал";
                break;
        }
        return difficulty;
    }

    public static String getCurrentDate(){
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        String date = year + "-" + month + "-" + day;

        return date;
    }

    public static Integer getParsedDate(String parameter){
        Integer value = null;
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        switch (parameter){
            case "day":
                value = c.get(Calendar.DAY_OF_MONTH);
                break;
            case "month":
                value = c.get(Calendar.MONTH);
                break;
            case "year":
                value = c.get(Calendar.YEAR);
                break;
        }
        return value;
    }

    public static int getCurrentDayID() {

        int result = 1;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                result = 7;
                break;
            case Calendar.MONDAY:
                result = 1;
                break;
            case Calendar.TUESDAY:
                result = 2;
                break;
            case Calendar.WEDNESDAY:
                result = 3;
                break;
            case Calendar.THURSDAY:
                result = 4;
                break;
            case Calendar.FRIDAY:
                result = 5;
                break;
            case Calendar.SATURDAY:
                result = 6;
                break;
        }
        return result;
    }

    public static String getSpecifiedDayName(String date){

        List<Integer> dateForIncrement = normalizeDateForColoring(date);
        date = dateForIncrement.get(0) + "-" + (dateForIncrement.get(1)+1) + "-" + dateForIncrement.get(2);

        String result = null;

        Date formatedDate = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            formatedDate = date_format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

         Calendar calendar = new GregorianCalendar();
         calendar.setTime(formatedDate);

        int day  = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                result = "Пон";
                break;
            case Calendar.TUESDAY:
                result = "Вто";
                break;
            case Calendar.WEDNESDAY:
                result = "Сре";
                break;
            case Calendar.THURSDAY:
                result = "Чет";
                break;
            case Calendar.FRIDAY:
                result = "Пят";
                break;
            case Calendar.SATURDAY:
                result = "Суб";
                break;
            case Calendar.SUNDAY:
                result = "Вос";
                break;
        }

        return result;
    }

    public static List<Integer> normalizeDateForColoring(String date){
        List<Integer> normalizedDate = new ArrayList<>();

        String year = date.substring(0, date.indexOf("-"));
        date = date.replace(year + "-","");
        String month = date.substring(0, date.indexOf("-"));
        String day = date = date.replace(month + "-","");

        normalizedDate.add(Integer.valueOf(year));
        normalizedDate.add(Integer.valueOf(month));
        normalizedDate.add(Integer.valueOf(day));

        return normalizedDate;
    }

    public static Calendar getNotificationTime(Context context, Date date){
        //Don't know if it helps
        ////// - getting time
        DatabaseHelper databaseHelper;
        SQLiteDatabase db;
        Cursor userCursor;

        databaseHelper = new DatabaseHelper(context);
        databaseHelper.getWritableDatabase();
        db = databaseHelper.open();
        int dayId = Utils.getCurrentDayID()-1;

        userCursor = db.query("MEAL_SETTINGS", null, "DAY = ?", new String[]{String.valueOf(dayId)}, null, null, null);

        List<String> timesList = new ArrayList<>();
        List<Integer> days = new ArrayList<>();


        Integer a = userCursor.getCount();

        if (userCursor.getCount() == 0)
        {
            return null;
        }

        if (userCursor.moveToFirst()) {
            do {
                timesList.add(userCursor.getString(4));
                days.add(userCursor.getInt(0));
            } while (userCursor.moveToNext());
        }

        userCursor.close();
        db.close();


        Date currentTime = date;
        Integer currentHour = currentTime.getHours();
        Integer currentMinute = currentTime.getMinutes();
        Integer currentDay = currentTime.getDate();

        Integer hour = 0;
        Integer minute = 0;

        Date dateToShowNotification = currentTime;
        Calendar cal = Calendar.getInstance();



        for(int i = 0; i<timesList.size(); i++){

            String time = timesList.get(i);

            hour  = Integer.valueOf(time.substring(0, time.indexOf(":")));
            minute = Integer.valueOf(time.substring(time.indexOf(":")+1, time.length()));

            if (currentHour <= hour)
            {
                if(currentMinute <= minute) {
                    dateToShowNotification.setDate(currentDay);
                    dateToShowNotification.setHours(hour);
                    dateToShowNotification.setMinutes(minute);
                    dateToShowNotification.setSeconds(0);
                    cal.setTime(dateToShowNotification);
                    break;
                }
            }
        }

        Integer lastHour  = Integer.valueOf(timesList.get(4).substring(0, timesList.get(4).indexOf(":")));
        Integer firstHour  = Integer.valueOf(timesList.get(0).substring(0, timesList.get(0).indexOf(":")));
        Integer firstMinute = Integer.valueOf(timesList.get(0).substring(timesList.get(0).indexOf(":")+1, timesList.get(0).length()));

        if(currentHour == lastHour){
            if(currentMinute >= minute) {
                dateToShowNotification.setDate(currentDay);
                dateToShowNotification.setHours(firstHour);
                dateToShowNotification.setMinutes(firstMinute);
                dateToShowNotification.setSeconds(0);
                cal.setTime(dateToShowNotification);
                cal.add(Calendar.DATE, 1);
            }
        }
        if(currentHour > lastHour){
            dateToShowNotification.setDate(currentDay);
            dateToShowNotification.setHours(firstHour);
            dateToShowNotification.setMinutes(firstMinute);
            dateToShowNotification.setSeconds(0);
            cal.setTime(dateToShowNotification);
            cal.add(Calendar.DATE, 1);
        }

        return cal;
    }

    public static Date getCurrentTime(){
        return Calendar.getInstance(TimeZone.getDefault()).getTime();
    }

    public static Date incrementTimeOnOneMin(){
        Date date = Calendar.getInstance(TimeZone.getDefault()).getTime();
        date.setMinutes(date.getMinutes()+1);
        return date;
    }

}

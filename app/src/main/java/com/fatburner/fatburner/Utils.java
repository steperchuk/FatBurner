package com.fatburner.fatburner;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        String date = year + "-" + month + "-" + day;

        return date;
    }

    public static String getParsedDate(String parameter){
        Integer value = null;
        Calendar c = Calendar.getInstance();
        switch (parameter){
            case "day":
                value = c.get(Calendar.DATE);
                break;
            case "month":
                value = c.get(Calendar.MONTH);
                break;
            case "year":
                value = c.get(Calendar.YEAR);
                break;
        }
        return String.valueOf(value);
    }

    public static int getCurrentDayID() {

        int result = 1;
        Calendar calendar = Calendar.getInstance();
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
}

package com.fatburner.fatburner;

import java.util.ArrayList;
import java.util.List;

import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS;

/**
 * Created by sergeyteperchuk on 6/20/17.
 */

public class Utils {

    //static List<String> numsArray;
    //static List<String> lettersArray;
    //static Map<String,String> shema;

    /*
    public static Map<String,String> normalizeSelectedShema(String Shema){
        PRODUCTS = null;
        char[] charArray = Shema.toCharArray();
        String d = "";
        String l = "";
        for(int i = 0; i < charArray.length; i++){
            if(Character.isDigit(Shema.charAt(i)))
            {
                d = "" + Shema.charAt(i);
                numsArray.add(d);
            }
            if(Character.isLetter(Shema.charAt(i)))
            {
                l = "" + Shema.charAt(i);
                if(!l.equals("+"))
                {lettersArray.add(l);l = "";}
            }
        }


        for (int i = 0; i < numsArray.size()-1; i++){
            shema.put(lettersArray.get(i), numsArray.get(i));
            PRODUCTS[i] = lettersArray.get(i);
        }

        PRODUCTS_PAGES_COUNT = lettersArray.size();

        return shema;
    }
    */

    public static void applyShemaProductsList(String Shema){
        List<String> lettersArray = new ArrayList<String>();

        for(int i = 0; i < Shema.length(); i++){
            if(Character.isLetter(Shema.charAt(i)))
            {
                String l = "" + Shema.charAt(i);
                if(!l.equals("+"))
                {

                    lettersArray.add(l);
                    //l = "";
                }
            }
        }
        PRODUCTS_PAGES_COUNT = lettersArray.size();
        String products[] =  new String[lettersArray.size()];;
        products = lettersArray.toArray(products);
        PRODUCTS = products;
    }

}

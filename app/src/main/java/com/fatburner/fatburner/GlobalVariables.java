package com.fatburner.fatburner;

import android.app.Application;
import android.util.ArrayMap;

import java.util.Map;


public class GlobalVariables extends Application{
    public static String selectedProgram;
    public static int selectedPhase = 1;
    public static boolean selectedDiet = true;
    public static boolean dietListViewMode = true;
    public static int PRODUCTS_PAGES_COUNT = 6;
    public static String TRAINING[] = null;
    public static int TRAINING_ID = 0;
    public static int LOAD_ARRAY[];
    public static String PRODUCTS[] = {"П", "К", "М", "Ф", "О", "Ж"};
    public static Map<String,Integer> PRODUCTS_COUNT = new ArrayMap<>();
    }

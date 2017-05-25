package com.fatburner.fatburner;

/**
 * Created by sergeyteperchuk on 5/25/17.
 */

public class SettingsProvider {

    public boolean _gender;
    public String _age;
    public String _weight;
    public String _dayStartTime;
    public String _trainingStartTime;
    public String _trainingDays;
    public boolean _showWaterNotification;
    public boolean _showFoodNotification;
    public boolean _showSleepNotification;

    public SettingsProvider(){

    }


    public boolean getGender(){
        return this._gender;
    }

    public void setGender(boolean gender){
         this._gender = gender;
    }

    public String getAge(){
        return this._age;
    };

    public void setAge(String age){
        this._age = age;
    }

    public String getWeight(){
        return this._weight;
    }

    public void setWeight(String weight){
        this._weight = weight;
    }

    public String getDayStartTime(){
        return this._dayStartTime;
    }

    public void setDayStartTime(String dayStartTime){
        this._dayStartTime = dayStartTime;
    }

    public String getTrainingStartTime(){
        return this._trainingStartTime;
    }

    public void setTrainingStartTime(String trainingStartTime){
        this._trainingStartTime = trainingStartTime;
    }

    public String getTrainingDays(){
        return this._trainingDays;
    }

    public void setTrainingDays(String trainingDays){
        this._trainingDays = trainingDays;
    }

    public boolean getShowWaterNotification(){
        return  this._showWaterNotification;
    }

    public void setShowWaterNotification(boolean showWaterNotification){
        this._showWaterNotification = showWaterNotification;
    }

    public boolean getShowFoodNotification(){
        return this._showFoodNotification;
    }

    public void setShowFoodNotification(boolean showFoodNotification){
        this._showFoodNotification = showFoodNotification;
    }

    public boolean getShowSleepNotification(){
        return this._showSleepNotification;
    }

    public void setShowSleepNotification(boolean showSleepNotification){
        this._showSleepNotification = showSleepNotification;
    }





}

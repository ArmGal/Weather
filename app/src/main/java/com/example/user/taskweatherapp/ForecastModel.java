package com.example.user.taskweatherapp;

public class ForecastModel {
    private String dayName;
    private int temp;
    private String conditionIconUrl;

    public ForecastModel(String dayName, int temp, String conditionIconUrl) {
        this.dayName = dayName;
        this.temp = temp;
        this.conditionIconUrl = conditionIconUrl;
    }

    public String getDayName() {
        return dayName;
    }

    public int getTemp() {
        return temp;
    }

    public String getConditionIconUrl() {
        return conditionIconUrl;
    }
}

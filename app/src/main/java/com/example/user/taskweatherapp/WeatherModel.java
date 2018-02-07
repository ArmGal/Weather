package com.example.user.taskweatherapp;

import android.util.SparseArray;

public class WeatherModel {

    private String cityName;
    private String country;
    private int temp;
    private String conditionIconUrl;
    private String conditionText;
    private int humidity;
    private double windKph;
    private String windDirection;
    private double pressureMb;
    private double visibilityKm;
    private SparseArray<ForecastModel> forecastModelArray;

    public WeatherModel(String cityName, String country, int temp, String conditionIconUrl, String conditionText, int humidity, double windKph, String windDirection, double pressureMb, double visibilityKm, SparseArray<ForecastModel> forecastModelArray) {
        this.cityName = cityName;
        this.country = country;
        this.temp = temp;
        this.conditionIconUrl = conditionIconUrl;
        this.conditionText = conditionText;
        this.humidity = humidity;
        this.windKph = windKph;
        this.windDirection = windDirection;
        this.pressureMb = pressureMb;
        this.visibilityKm = visibilityKm;
        this.forecastModelArray = forecastModelArray;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public int getTemp() {
        return temp;
    }

    public String getConditionIconUrl() {
        return conditionIconUrl;
    }

    public String getConditionText() {
        return conditionText;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getWindKph() {
        return windKph;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public double getPressureMb() {
        return pressureMb;
    }

    public double getVisibilityKm() {
        return visibilityKm;
    }

    public SparseArray<ForecastModel> getForecastModelArray() {
        return forecastModelArray;
    }
}
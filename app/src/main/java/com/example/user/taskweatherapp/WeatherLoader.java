package com.example.user.taskweatherapp;

import android.content.AsyncTaskLoader;
import android.content.Context;


public class WeatherLoader extends AsyncTaskLoader<WeatherModel> {

    private String mUrl;

    public WeatherLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public WeatherModel loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        return HttpHandler.fetchData(mUrl);
    }
}
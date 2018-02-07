package com.example.user.taskweatherapp;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

    private HttpHandler() {
    }

    public static WeatherModel fetchData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the HTTP request.", e);
        }

        return extractFeatureFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            } else if (urlConnection.getResponseCode() == 400) {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the weather JSON results.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static WeatherModel extractFeatureFromJson(String weatherJSON) {
        if (TextUtils.isEmpty(weatherJSON)) {
            return null;
        }

        try {
            JSONObject locationJsonResponse = new JSONObject(weatherJSON);

            JSONObject location = locationJsonResponse.getJSONObject("location");
            String cityName = location.getString("name");
            String country = location.getString("country");

            JSONObject currentJsonResponse = new JSONObject(weatherJSON);
            JSONObject current = currentJsonResponse.getJSONObject("current");
            int tempC = (int) Math.round(current.getDouble("temp_c"));
            int humidity = current.getInt("humidity");
            double windKph = current.getDouble("wind_kph");
            String windDirection = current.getString("wind_dir");
            double pressureMb = current.getDouble("pressure_mb");
            double visibilityKm = current.getDouble("vis_km");

            JSONObject condition = current.getJSONObject("condition");
            String conditionIcon = condition.getString("icon");
            String conditionText = condition.getString("text");

            JSONObject forecastJsonResponse = new JSONObject(weatherJSON);
            JSONObject forecast = forecastJsonResponse.getJSONObject("forecast");
            JSONArray forecastDay = forecast.getJSONArray("forecastday");

            SparseArray<ForecastModel> forecastModelArray = new SparseArray<>();

            try {
                for (int i = 1; i < forecastDay.length(); i++) {
                    JSONObject index = forecastDay.getJSONObject(i);

                    long timeInMilliseconds = index.getLong("date_epoch");
                    String forecastDayName = formatDateTime(timeInMilliseconds);

                    JSONObject day = index.getJSONObject("day");
                    int forecastTemp = (int) Math.round(day.getDouble("avgtemp_c"));

                    JSONObject forecastCondition = day.getJSONObject("condition");
                    String forecastConditionIcon = forecastCondition.getString("icon");

                    ForecastModel forecastModel = new ForecastModel(forecastDayName, forecastTemp, forecastConditionIcon);

                    forecastModelArray.put(i - 1, forecastModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new WeatherModel(cityName, country, tempC, conditionIcon, conditionText, humidity, windKph, windDirection, pressureMb, visibilityKm, forecastModelArray);

        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the weather JSON results", e);
        }
        return null;
    }

    private static String formatDateTime(long timeInMilliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(("E"), Locale.ENGLISH);
        return formatter.format(new Date(timeInMilliseconds * 1000));
    }
}


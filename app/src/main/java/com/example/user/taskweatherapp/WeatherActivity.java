package com.example.user.taskweatherapp;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class WeatherActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<WeatherModel> {

    private static final String API_KEY = "68f617bffd9041fb9be211528182601";

    private ProgressBar mLoadingSpinner;
    private EditText mSearchInput;
    private TextView mEmptyStateTextView;
    private LoaderManager mLoaderManager;
    private Group mWeatherDetailsGroup;
    private Group mErrorGroup;
    private Group mInputGroup;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        findViews();

        mInputGroup.setVisibility(View.GONE);
        mWeatherDetailsGroup.setVisibility(View.GONE);
        mLoadingSpinner.setVisibility(View.GONE);

        if (API_KEY.isEmpty()) {
            mEmptyStateTextView.setText(R.string.no_api_key);
        } else if (!NetworkCheck.isOnline(this)) {
            mEmptyStateTextView.setText(R.string.no_internet);
        } else {
            mErrorGroup.setVisibility(View.GONE);
            mInputGroup.setVisibility(View.VISIBLE);
            Button mSearchButton = findViewById(R.id.search_button);
            mSearchInput = findViewById(R.id.search_input);
            mLoaderManager = getLoaderManager();

            mSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mErrorGroup.setVisibility(View.GONE);
                    mLoadingSpinner.setVisibility(View.VISIBLE);
                    cityName = mSearchInput.getText().toString();

                    if (TextUtils.isEmpty(cityName)) {
                        Toast.makeText(WeatherActivity.this, R.string.empty_edit_text_message, Toast.LENGTH_SHORT).show();
                    } else {
                        mLoaderManager.restartLoader(1, null, WeatherActivity.this);
                        mLoaderManager.initLoader(1, null, WeatherActivity.this);
                    }

                    dismissKeyboard(WeatherActivity.this);
                }
            });

            if (mLoaderManager.getLoader(1) != null) {
                mLoaderManager.initLoader(1, null, this);
            }
        }
    }

    private void findViews() {
        mInputGroup = findViewById(R.id.input_group);
        mErrorGroup = findViewById(R.id.error_group);
        mWeatherDetailsGroup = findViewById(R.id.weather_details_group);
        mLoadingSpinner = findViewById(R.id.loading_indicator);
        mEmptyStateTextView = findViewById(R.id.empty_view);
    }

    private void updateUi(WeatherModel weather) {
        TextView city = findViewById(R.id.city_name);
        city.setText(weather.getCityName());

        TextView country = findViewById(R.id.country);
        country.setText(weather.getCountry());

        TextView tempCelsius = findViewById(R.id.temp);
        GradientDrawable tempCircle = (GradientDrawable) tempCelsius.getBackground();
        int tempColor = getTemperatureColor(weather.getTemp());
        tempCircle.setColor(tempColor);
        tempCelsius.setText(getString(R.string.temp_degree, Integer.toString(weather.getTemp())));

        ImageView conditionIcon = findViewById(R.id.condition_icon);
        Picasso.with(WeatherActivity.this)
                .load("http:" + weather.getConditionIconUrl())
                .into(conditionIcon);

        TextView conditionText = findViewById(R.id.condition_text);
        conditionText.setText(weather.getConditionText());

        TextView humidity = findViewById(R.id.humidity);
        humidity.setText(getString(R.string.humidity, Integer.toString(weather.getHumidity())));

        TextView windKph = findViewById(R.id.wind);
        windKph.setText(getString(R.string.wind, weather.getWindDirection(), Double.toString(weather.getWindKph())));

        TextView pressureMb = findViewById(R.id.pressure);
        pressureMb.setText(getString(R.string.pressure, Double.toString(weather.getPressureMb())));

        TextView visibilityKm = findViewById(R.id.visibility);
        visibilityKm.setText(getString(R.string.visibility, Double.toString(weather.getVisibilityKm())));

        Resources r = getResources();
        String name = getPackageName();

        int[] resId = new int[4];

        for (int i = 0; i < 4; i++) {
            resId[i] = r.getIdentifier("forecast_day" + (i + 1) + "_name", "id", name);
            TextView forecastDayName = findViewById(resId[i]);

            forecastDayName.setText(weather.getForecastModelArray().get(i).getDayName());

            resId[i] = r.getIdentifier("forecast_day" + (i + 1) + "_icon", "id", name);

            ImageView forecastConditionIcon = findViewById(resId[i]);
            Picasso.with(WeatherActivity.this)
                    .load("http:" + weather.getForecastModelArray().get(i).getConditionIconUrl())
                    .into(forecastConditionIcon);

            resId[i] = r.getIdentifier("forecast_day" + (i + 1) + "_temp", "id", name);
            TextView tempForecast = findViewById(resId[i]);
            GradientDrawable tempCircleForecast = (GradientDrawable) tempForecast.getBackground();
            int tempColorForecast = getTemperatureColor(weather.getForecastModelArray().get(i).getTemp());
            tempCircleForecast.setColor(tempColorForecast);
            tempForecast.setText(getString(R.string.temp_degree, Integer.toString(weather.getForecastModelArray().get(i).getTemp())));

            mLoadingSpinner.setVisibility(View.GONE);
            mWeatherDetailsGroup.setVisibility(View.VISIBLE);
        }
    }

    private int getTemperatureColor(int temp) {
        int tempColorResourceId;

        if (temp <= -21)
            tempColorResourceId = R.color.tempM21;
        else if (-20 <= temp && temp <= -16)
            tempColorResourceId = R.color.tempM20_M16;
        else if (-15 <= temp && temp <= -11)
            tempColorResourceId = R.color.tempM15_M11;
        else if (-10 <= temp && temp <= -6)
            tempColorResourceId = R.color.tempM10_M6;
        else if (-5 <= temp && temp <= -1)
            tempColorResourceId = R.color.tempM5_M1;
        else if (0 <= temp && temp <= 4)
            tempColorResourceId = R.color.temp0_P4;
        else if (5 <= temp && temp <= 9)
            tempColorResourceId = R.color.tempP5_P9;
        else if (10 <= temp && temp <= 14)
            tempColorResourceId = R.color.tempP10_P14;
        else if (15 <= temp && temp <= 19)
            tempColorResourceId = R.color.tempP15_P19;
        else if (20 <= temp && temp <= 24)
            tempColorResourceId = R.color.tempP20_P24;
        else if (25 <= temp && temp <= 29)
            tempColorResourceId = R.color.tempP25_P29;
        else if (30 <= temp && temp <= 34)
            tempColorResourceId = R.color.tempP30_P34;
        else if (temp >= 35)
            tempColorResourceId = R.color.tempP35;
        else
            tempColorResourceId = R.color.colorAccent;

        return ContextCompat.getColor(WeatherActivity.this, tempColorResourceId);
    }

    @Override
    public Loader<WeatherModel> onCreateLoader(int i, Bundle bundle) {
        return new WeatherLoader(this, String.format("http://api.apixu.com/v1/forecast.json?key=%s&days=5&q=%s", API_KEY, cityName));
    }

    @Override
    public void onLoadFinished(Loader<WeatherModel> loader, WeatherModel model) {
        if (model == null) {
            mLoadingSpinner.setVisibility(View.GONE);
            mWeatherDetailsGroup.setVisibility(View.GONE);
            mErrorGroup.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.location_no_found);
        } else {
            updateUi(model);
        }
    }

    private void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getApplicationWindowToken(), 0);
            }
    }

    @Override
    public void onLoaderReset(Loader<WeatherModel> loader) {
    }
}
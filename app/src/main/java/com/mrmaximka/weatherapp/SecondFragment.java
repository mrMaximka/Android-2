package com.mrmaximka.weatherapp;

import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class SecondFragment extends Fragment {

    private final Handler handler = new Handler();
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private TextView tvCityName;        // Название города
    private TextView tvTemperature;     // Температура
    private TextView tvPrecipitatoin;   // Осадки
    private TextView tvWind;        // Ветер
    private TextView tvWet;         // Влажность
    private TextView tvPressure;    // Давление

    private boolean isCbWind;       // Включен ли чекбокс "Ветер" на FirstFragment
    private boolean isCbWet;        // Влажность
    private boolean isCbPressure;   // И давление
    private static String temperatureText;  // Температура с датчика
//    private static String wetText;      // Влажность с датчика

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);        // Инициализируем вьюхт
        setViewSettings();      // Задаем им настройкт
    }

    private void initViews(View view) {
        tvCityName = view.findViewById(R.id.second_fragment_tv_town_name);
        tvTemperature = view.findViewById(R.id.second_fragment_tv_temperature);
        tvPrecipitatoin = view.findViewById(R.id.second_fragment_tv_precipitation);
        tvWind = view.findViewById(R.id.second_fragment_tv_wind);
        tvWet = view.findViewById(R.id.second_fragment_tv_wet);
        tvPressure = view.findViewById(R.id.second_fragment_tv_pressure);

        updateWeatherData("Moscow");        // Отправляем запрос на город Москва
    }

    private void setViewSettings() {
        /*String[] precipitation = getResources().getStringArray(R.array.sky);    // Берем массив с осадками
        int n = 5;      // Зашлушка на погодные данные
*/
        /*tvCityName.setText(SecondActivity.getCityName());   // Ставим название города
        tvPrecipitatoin.setText(precipitation[0]);      // Осадки
        tvWind.setText(String.format(getString(R.string.second_fragment_tv_wind), n));  // Ветер
        tvPressure.setText(String.format(getString(R.string.second_fragment_tv_pressure), n));  // И давление

        tvTemperature.setText(temperatureText);    // Температуру берем из датчика
        tvWet.setText(wetText);     // Влажность тоже*/
        checkWeatherSettings();     // Проверяем какие настройки отобраджения были включены
        setVisibleSettings();       // Делаем видимыми необходимые объекты
    }

    private void checkWeatherSettings() {
        boolean[] weatherSettings = FirstFragment.getWeatherSettings(); // Берем массив

        isCbWind = weatherSettings[0];  // Смотрим, включены ли настройки ветра
        isCbWet = weatherSettings[1];   // Владности
        isCbPressure = weatherSettings[2];  // и давления
    }

    private void setVisibleSettings() { // Делаем видимыми необходимые части
        if (isCbWind) tvWind.setVisibility(View.VISIBLE);
        if (isCbWet) tvWet.setVisibility(View.VISIBLE);
        if (isCbPressure) tvPressure.setVisibility(View.VISIBLE);
    }

    // Вывод датчика температуры
    public static void showTemperatureSensors(SensorEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.values[0]);
        temperatureText = String.valueOf(stringBuilder);    // Сюда засунули температуру с датчика
    }

    /*public static void showWetSensors(SensorEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Влажность ").append(event.values[0]).append("%");
        wetText = String.valueOf(stringBuilder);    // Сюда влажность
    } */                                  // PS Да, хардкод исправлю, не успел (

    public static double getTemperatureText() { // Геттер для сервиса
        if (temperatureText == null){
            return 0;
        }
        else {
            return Double.parseDouble(temperatureText);
        }
    }

    private void updateWeatherData(final String city) {
        new Thread() {
            @Override
            public void run() {
                final JSONObject jsonObject = WeatherDataLoader.getJSONData(Objects.requireNonNull(getActivity()).getApplicationContext(), city);   // Отправляем запрос
                if(jsonObject == null) {
                    handler.post(new Runnable() {   // Если ошибка
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.json_city_not_found), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(jsonObject);      // Обрабатываем полученные данные
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject jsonObject) {
        Log.d(LOG_TAG, "json: " + jsonObject.toString());
        try {
            JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonObject.getJSONObject("main");
            JSONObject wind = jsonObject.getJSONObject("wind");

            setTownName();      // Пишем название города
            setDetails(details, main);  // Тут вытащим давление, влажность, осадки
            setCurrentTemp(main);       // Вытащим температуру
            setWind(wind);      // Вытащим ветер

        } catch (Exception exc) {
            exc.printStackTrace();
            Log.e(LOG_TAG, "One or more fields not found in the JSON data");
        }
    }

    private void setWind(JSONObject wind) throws JSONException {
        String speed = wind.getString("speed");
        tvWind.setText(String.format(getString(R.string.second_fragment_tv_wind), speed));
    }

    private void setCurrentTemp(JSONObject main) throws JSONException {
        tvTemperature.setText(main.getString("temp") + "\u2103");
    }

    private void setDetails(JSONObject details, JSONObject main) throws JSONException {
        String pressure = main.getString("pressure");
        String wet = main.getString("humidity");
        tvPrecipitatoin.setText(details.getString("description").toUpperCase());
        tvPressure.setText(String.format(getString(R.string.second_fragment_tv_pressure), pressure));
        tvWet.setText(String.format("Влажность %s ", wet));
    }

    private void setTownName() {
        tvCityName.setText(SecondActivity.getCityName());
    }
}

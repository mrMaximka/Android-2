package com.mrmaximka.lesson7;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class SecondFragment extends Fragment {

    public static final String PARCEL = "parcel";

    Parcel parcel;

    private boolean swWind;
    private boolean swWet;
    private boolean swPressure;
    private TextView precipitation;
    private static TextView wet;


    private static TextView temperature;
    int n = 5;


    public static SecondFragment init(Parcel parcel) {
        SecondFragment f = new SecondFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARCEL, parcel);
        f.setArguments(args);
        return f;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] precipitations = getResources().getStringArray(R.array.sky);

        View layout = inflater.inflate(R.layout.fragment_second, container, false);

        TextView cityNameView = layout.findViewById(R.id.textView);
        temperature = layout.findViewById(R.id.temperature);
        precipitation = layout.findViewById(R.id.precipitation);
        TextView wind = layout.findViewById(R.id.secondWind);
        wet = layout.findViewById(R.id.secondWet);
        TextView pressure = layout.findViewById(R.id.secondPressure);

        Button inBrowser = layout.findViewById(R.id.inBrowser);
        ImageButton share = layout.findViewById(R.id.share);

        inBrowser.setOnClickListener(onClickListener);
        share.setOnClickListener(onClickListenerShare);

        swWind = FirstFragment.getSwWind();
        swWet = FirstFragment.getSwWet();
        swPressure = FirstFragment.getSwPressure();

        parcel = getParcel();



        cityNameView.setText(String.format(getString(R.string.second_title),parcel.getCityName()));

//        temperature.setText(String.format(getString(R.string.second_temperature), n ));
        precipitation.setText(precipitations[0]);

        wind.setText(String.format(getString(R.string.wind_txt), n));
//        wet.setText(String.format(getString(R.string.wet_txt), n));
        pressure.setText(String.format(getString(R.string.pressure_txt), n));

        if (swWind) wind.setVisibility(View.VISIBLE);
        if (swWet) wet.setVisibility(View.VISIBLE);
        if (swPressure) pressure.setVisibility(View.VISIBLE);
        return layout;
    }


    // Вывод датчика температуры
    public static void showTemperatureSensors(SensorEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.values[0]);
        temperature.setText(stringBuilder);
    }

    public static void showWetSensors(SensorEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Влажность ").append(event.values[0]).append("%");
        wet.setText(stringBuilder);
    }



    public Parcel getParcel() {
        assert getArguments() != null;
        return (Parcel) getArguments().getSerializable(PARCEL);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String[] eng_towns = getResources().getStringArray(R.array.eng_towns);
            Uri uri = Uri.parse(getString(R.string.uri_txt) + eng_towns[parcel.getImageIndex()]); // Ссылка на погоду выбранного города
            intent.setData(uri);
            if (intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager())!= null){
                startActivity(intent);
            }
            else {
                Toast.makeText(getActivity(), getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener onClickListenerShare = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String message = String.format(getString(R.string.message), parcel.getCityName(), n, precipitation.getText());
            if (swWind) message += String.format(getString(R.string.wind_txt), n);
            if (swWet) message += String.format(getString(R.string.wet_txt), n);
            if (swPressure) message += String.format(getString(R.string.pressure_txt), n);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("mailto:");
            intent.setData(uri);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            if (intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager())!= null){
                startActivity(intent);
            }
            else {
                Toast.makeText(getActivity(), getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    };
}

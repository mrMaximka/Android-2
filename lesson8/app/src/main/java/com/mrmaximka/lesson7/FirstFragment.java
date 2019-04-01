package com.mrmaximka.lesson7;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    public static final String CURRENT_CITY = "CurrentCity";
    public static final String WIND_KEY = "WindKey";
    public static final String WET_KEY = "WetKey";
    public static final String PRESSURE_KEY = "PressureKey";
    public static final String ARRAY_TOWNS = "ArrayTowns";
    private boolean isExistCoatofarms;  // Возможность поместить 2 фрагмента
    private Parcel currentParcel;
    static CheckBox swWind;      // Включен ли checkBox с ветром
    static CheckBox swWet;       // Влажностью
    static CheckBox swPressure;  // И давлением
    static CityAdapter adapter;

    static ArrayList<String> townList = new ArrayList<>();

    public static void addTown(Editable text) {     // Добавление города
        townList.add(String.valueOf(text));
        adapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    private OnClick onClickListener = new OnClick() {
        @Override
        public void onListItemClick(int position, String city) {
            currentParcel = new Parcel(position, city);
            showCoatOfArms(currentParcel);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] cities = getActivity().getResources().getStringArray(R.array.Cities);
        if (townList.isEmpty()) for (int i = 0; i < cities.length; i++) townList.add(cities[i]);    // Закидывает города из ресурсов, только при первом создании

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CityAdapter(townList, onClickListener);
        TextView settingsTextView = view.findViewById(R.id.settings_text_view);
        LinearLayout settingsLayout = view.findViewById(R.id.settings_layout);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(settingsLayout);
        int peekHeight = (int) settingsTextView.getTextSize();
        bottomSheetBehavior.setPeekHeight(peekHeight + settingsTextView.getPaddingBottom() + settingsTextView.getPaddingTop());
        bottomSheetBehavior.setHideable(false);
        recyclerView.setAdapter(adapter);
        swWind = view.findViewById(R.id.swWind);
        swWet = view.findViewById(R.id.swWet);
        swPressure = view.findViewById(R.id.swPressure);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isExistCoatofarms = getActivity().findViewById(R.id.coat_of_arms) != null;

        if (savedInstanceState != null) {   // Создавалась ли уже активити
            currentParcel = (Parcel) savedInstanceState.getSerializable(CURRENT_CITY);
            swWind.setChecked(savedInstanceState.getBoolean(WIND_KEY));
            swWet.setChecked(savedInstanceState.getBoolean(WET_KEY));
            swPressure.setChecked(savedInstanceState.getBoolean(PRESSURE_KEY));
            townList = savedInstanceState.getStringArrayList(ARRAY_TOWNS);
        } else {
            currentParcel = new Parcel(0, getResources().getTextArray(R.array.Cities)[0].toString());
        }

        // Если есть место по 2 фрагмента, то рисуем
        if (isExistCoatofarms) {
            showCoatOfArms(currentParcel);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CURRENT_CITY, currentParcel);     // Сохраним позицию
        outState.putSerializable(WIND_KEY, swWind.isChecked());        // И чекбоксы
        outState.putSerializable(WET_KEY, swWet.isChecked());
        outState.putSerializable(PRESSURE_KEY, swPressure.isChecked());
        outState.putSerializable(ARRAY_TOWNS, townList);
    }

    // Отрисовка второго фрагмента
    private void showCoatOfArms(Parcel parcel) {
        if (isExistCoatofarms) {        // Если можно отобразить 2 фрагмента

            SecondFragment secondFragment = (SecondFragment)
                    getFragmentManager().findFragmentById(R.id.coat_of_arms);

            if (secondFragment == null || secondFragment.getParcel().getImageIndex() != parcel.getImageIndex()) {


                secondFragment = SecondFragment.init(parcel);


                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.coat_of_arms, secondFragment);
                ft.commit();
            }
        } else {    // Иначе вторая активити
            Intent intent = new Intent();
            intent.setClass(getActivity(), SecondActivity.class);
            intent.putExtra(SecondFragment.PARCEL, parcel);
            startActivity(intent);
        }
    }


    public static boolean getSwWind(){
        return swWind.isChecked();
    }

    public static boolean getSwWet(){
        return swWet.isChecked();
    }

    public static boolean getSwPressure(){
        return swPressure.isChecked();
    }


    interface OnClick {

        void onListItemClick(int position, String city);
    }
}

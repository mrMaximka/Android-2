package com.mrmaximka.lesson7;

import java.io.Serializable;

final class Parcel implements Serializable {

    private final int imageIndex;
    private final String cityName;


    Parcel(int imageIndex, String cityName) {
        this.imageIndex = imageIndex;
        this.cityName = cityName;
    }

    int getImageIndex() {
        return imageIndex;
    }

    String getCityName() {
        return cityName;
    }
}

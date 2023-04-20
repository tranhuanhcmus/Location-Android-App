package com.example.googlemaps.InfoPlace;

import android.view.View;

import com.example.googlemaps.Direction.Distance;
import com.example.googlemaps.Direction.Duration;
import com.google.android.gms.maps.model.LatLng;

public class infoPlace {

    public String name;
    public String address;
    public String rating;

    public LatLng position;

    public Distance distance;

    public Duration duration;

    public infoPlace(String name, String address, String rating, LatLng position){
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.position = position;
    }

}

package com.example.googlemaps.Direction;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {

    public Distance distance;
    public Duration duration;
    public LatLng end_location;
    public String end_address;
    public LatLng start_location;
    public String start_address;
    public String travel_mode;
    public List<LatLng> polyline;

    public Route(){

    }

    public Route(Distance distance, Duration duration, LatLng end_location, String end_address, LatLng start_location,
                 String start_address, String travel_mode, List<LatLng> polyline){
        this.distance = distance;
        this.duration = duration;
        this.end_location = end_location;
        this.start_location = start_location;
        this.travel_mode = travel_mode;
        this.polyline = polyline;
        this.end_address = end_address;
        this.start_address = start_address;

    }

}

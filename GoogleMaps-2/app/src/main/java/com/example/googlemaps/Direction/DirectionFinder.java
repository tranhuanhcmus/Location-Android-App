package com.example.googlemaps.Direction;

import android.content.Context;

import com.example.googlemaps.R;
import com.google.android.gms.maps.model.LatLng;

public class DirectionFinder {

    final private String urlDirectionAPI = "https://maps.googleapis.com/maps/api/directions/json?";

    Context context;
    DIrectionListener listener;
    LatLng origin;
    LatLng destination;
    boolean alternatives;

    public DirectionFinder(Context context,DIrectionListener listener, LatLng origin, LatLng destination,boolean alternatives){
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
        this.context = context;
        this.alternatives = alternatives;
    }

    public String createUrlDirection(String mode){

        String urlOrigin = String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude);
        String urlDestination = String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude);

        String totalUrl = urlDirectionAPI + "origin="+urlOrigin + "&destination=" + urlDestination +"&language=vi" +"&mode="+ mode;
        if(alternatives){
            totalUrl += "&alternatives=true";
        }
        if(mode == "moto"){
            return totalUrl + "&vehicleType=2" + "&key=" + context.getString(R.string.API_KEY);
        }

        return totalUrl + "&key=" + context.getString(R.string.API_KEY);
    }

    public void execute(String trafficMode){
        listener.onStartFindDirection();
        DownloadJsonDirection downloadJsonDirection = new DownloadJsonDirection(listener, trafficMode);
        downloadJsonDirection.execute(createUrlDirection(trafficMode));
    }

}

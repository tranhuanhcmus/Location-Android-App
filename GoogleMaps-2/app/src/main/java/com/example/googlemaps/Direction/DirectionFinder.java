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

    public DirectionFinder(Context context,DIrectionListener listener, LatLng origin, LatLng destination){
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
        this.context = context;
    }

    public String createUrlDirection(String mode){

        String urlOrigin = String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude);
        String urlDestination = String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude);

        if(mode == "moto"){
            return urlDirectionAPI + "origin="+urlOrigin + "&destination=" + urlDestination +"&mode="+ mode + "&vehicleType=2" + "&key=" + context.getString(R.string.API_KEY);
        }

        return urlDirectionAPI + "origin="+urlOrigin + "&destination=" + urlDestination +"&mode="+ mode + "&key=" + context.getString(R.string.API_KEY);
    }

    public void execute(String trafficMode){
        listener.onStartFindDirection();
        DownloadJsonDirection downloadJsonDirection = new DownloadJsonDirection(listener);
        downloadJsonDirection.execute(createUrlDirection(trafficMode));
    }

}

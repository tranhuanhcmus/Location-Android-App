package com.example.googlemaps.InfoPlace;

import android.view.View;

import com.example.googlemaps.Direction.Distance;
import com.example.googlemaps.Direction.Duration;

public interface FragmentCallbacks {

    public void onMsgFromMainToFrag(infoPlace infoPlace );

    public void onMsgFromMainToFrag(String info );

    public void onMsgFromMainToFrag(Distance distance, Duration duration);

    public void onMsgFromMainToFrag(View view);
}

package com.example.googlemaps.Direction;

import java.util.List;

public interface DIrectionListener {

    public void onStartFindDirection();
    public void onSuccessFindDirection(List<Route> routeList);

}

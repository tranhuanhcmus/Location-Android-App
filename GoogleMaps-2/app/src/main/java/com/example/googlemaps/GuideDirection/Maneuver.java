package com.example.googlemaps.GuideDirection;

import java.util.HashMap;
import java.util.Map;

public class Maneuver {

    private Map<String,String> maneuver = new HashMap<String, String>();;

    public Maneuver(){

        maneuver.put("turn-slight-right","Rẽ nhẹ phải");
        maneuver.put("turn-right","Rẽ phải");
        maneuver.put("turn-sharp-right","Rẽ dốc phải");
        maneuver.put("uturn-right","Quay đầu phải");
        maneuver.put("turn-slight-left","Rẽ nhẹ trái");
        maneuver.put("turn-left","Rẽ trái");
        maneuver.put("turn-sharp-left","Rẽ dốc trái");
        maneuver.put("uturn-left","Quay đầu trái");
        maneuver.put("straight","Đi thẳng");
        maneuver.put("ramp-right","Nhảy lên đường cao tốc rẽ phải");
        maneuver.put("ramp-left","Nhảy lên đường cao tốc rẽ trái");
        maneuver.put("merge","Hợp nhất vào đường cao tốc");
        maneuver.put("fork-right","Rẽ phải tại giao lộ");
        maneuver.put("fork-left","Rẽ trái tại giao lộ");
        maneuver.put("ferry","Đi đường phà");
        maneuver.put("ferry-train","Đi đường phà tàu");
        maneuver.put("roundabout-right","Rẽ phải tại vòng xuyến");
        maneuver.put("roundabout-left","Rẽ trái tại vòng xuyến");
        maneuver.put("rotary","Đi vòng xoay");
        maneuver.put("depart","Xuất phát");
        maneuver.put("arrive","Đến nơi");

    }

    public String convertToVietnamese(String maneuver){
        return this.maneuver.get(maneuver);
    }

}

package com.example.googlemaps.GuideDirection;

import com.example.googlemaps.R;

import java.util.HashMap;
import java.util.Map;

public class Maneuver {

    private Map<String,String> maneuver = new HashMap<String, String>();;

    public Maneuver(){

        maneuver.put("turn-slight-right","Rẽ nhẹ phải");//
        maneuver.put("turn-right","Rẽ phải");//
        maneuver.put("turn-sharp-right","Rẽ dốc phải");//
        maneuver.put("u-turn-right","Quay đầu phải");//
        maneuver.put("turn-slight-left","Rẽ nhẹ trái");//
        maneuver.put("turn-left","Rẽ trái");//
        maneuver.put("turn-sharp-left","Rẽ dốc trái");//
        maneuver.put("u-turn-left","Quay đầu trái");//
        maneuver.put("straight","Đi thẳng");//
        maneuver.put("ramp-right","Nhảy lên đường cao tốc rẽ phải");//
        maneuver.put("ramp-left","Nhảy lên đường cao tốc rẽ trái");//
        maneuver.put("merge","Hợp nhất vào đường cao tốc");//
        maneuver.put("fork-right","Rẽ phải tại giao lộ");//
        maneuver.put("fork-left","Rẽ trái tại giao lộ");//
        maneuver.put("ferry","Đi đường phà");//
        maneuver.put("ferry-train","Đi đường phà tàu");//
        maneuver.put("roundabout-right","Rẽ phải tại vòng xuyến");//
        maneuver.put("roundabout-left","Rẽ trái tại vòng xuyến");//
        maneuver.put("rotary","Đi vòng xoay");//
        maneuver.put("depart","Xuất phát");
        maneuver.put("arrive","Đến nơi");


    }


    public String convertToVietnamese(String maneuver){
        return this.maneuver.get(maneuver);
    }

    public int findImageManeuver(String maneuver){

        if(maneuver.equals("turn-slight-right")){
            return R.drawable.ic_turn_slight_left;
        }else if(maneuver.equals("turn-right")){
            return R.drawable.ic_turn_right;
        }else if(maneuver.equals("turn-sharp-right")){
            return R.drawable.ic_turn_shape_right;
        }else if(maneuver.equals("u-turn-right")){
            return R.drawable.ic_u_turn_right;
        }else if(maneuver.equals("turn-slight-left")){
            return R.drawable.ic_turn_slight_left;
        }else if(maneuver.equals("turn-left")){
            return R.drawable.ic_turn_left;
        }else if(maneuver.equals("turn-sharp-left")){
            return R.drawable.ic_turn_shape_left;
        }else if(maneuver.equals("u-turn-left")){
            return R.drawable.ic_u_turn_left;
        }else if(maneuver.equals("straight")){
            return R.drawable.ic_straight;
        }else if(maneuver.equals("ramp-right")){
            return R.drawable.ic_ramp_right;
        }else if(maneuver.equals("ramp-left")){
            return R.drawable.ic_ramp_left;
        }else if(maneuver.equals("merge")){
            return R.drawable.ic_merge;
        }else if(maneuver.equals("fork-right")){
            return R.drawable.ic_fork_right;
        }else if(maneuver.equals("fork-left")){
            return R.drawable.ic_fork_left;
        }else if(maneuver.equals("ferry")){
            return R.drawable.ic_ferry;
        }else if(maneuver.equals("ferry-train")){
            return R.drawable.ic_ferry;
        }else if(maneuver.equals("roundabout-right")){
            return R.drawable.ic_roundabout_right;
        }else if(maneuver.equals("roundabout-left")){
            return R.drawable.ic_roundabout_left;
        }else if(maneuver.equals("rotary")){
            return R.drawable.ic_rotary;
        }else if(maneuver.equals("depart")){
            return R.drawable.ic_depart;
        }else if(maneuver.equals("arrive")){
            return R.drawable.ic_arrive;
        }else{
            return R.drawable.ic_straight;
        }
    }

}

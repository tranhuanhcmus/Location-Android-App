package com.example.googlemaps.GuideDirection;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class EstimateTime
{
    private int curHour;
    private int curMinute;

    public EstimateTime(){
        Date currentDate = Calendar.getInstance().getTime();
        curHour = currentDate.getHours();
        curMinute = currentDate.getMinutes();
    }

    public String estimate(int second){

        Log.e("EstimateTime", "estimate: "+String.valueOf(second) );
        int minute = parseToMinute(second);

        while (curMinute + minute >= 60){
            curHour += 1;
            minute =  minute - 60;
        }

        curMinute = curMinute + minute;

        while (curHour >= 24){
            curHour = curHour - 24;
        }

        String h = "";
        String m = "";

        if(curHour < 10){
            h = "0" + String.valueOf(curHour);
        }else{
            h = String.valueOf(curHour);
        }
        if(curMinute < 10){
            m = "0" + String.valueOf(curMinute);
        }else{
            m = String.valueOf(curMinute);
        }

        return h + ":" + m;

    }

    public int parseToMinute(int second){
        return (second/60) + 1;
    }

}

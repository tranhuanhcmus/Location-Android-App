package com.example.googlemaps;

public class InfoSearching {

    private String name;
    private String address;
    private int distance;

    InfoSearching(String name, String address, int distance){
        this.distance = distance;
        this.address = address;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getAddress(){
        return this.address;
    }

    public int getDistance(){
        return distance;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setDistance(int distance){
        this.distance = distance;
    }

}

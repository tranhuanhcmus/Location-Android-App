package com.example.googlemaps.BookMark;

import android.database.sqlite.SQLiteDatabase;

import com.example.googlemaps.MapsActivity;

public class WorkWithSQLiteBookMark {

    SQLiteDatabase db;
    String tblName;
    MapsActivity map;

    public WorkWithSQLiteBookMark(SQLiteDatabase db,String tblName){
        this.db = db;
        this.tblName = tblName;
    }

    public void createTable(){
        if(!map.checkIfTableExists(db,tblName)){
            db.execSQL("create table "+tblName +"("
                            +"name text,"
                            +"address text primary key);");
        }
    }

    public void deleteRow(String name,String address){
        db.delete(tblName,"name=? And address=?",new String[]{name,address});
    }

    public void addRow(String name, String address){
        String query = "insert into Luu(name, address) values (?,?)";
        db.execSQL(query,new Object[]{name, address});
    }
}

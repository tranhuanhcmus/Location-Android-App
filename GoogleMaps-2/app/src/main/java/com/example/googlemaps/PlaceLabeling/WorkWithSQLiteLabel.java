package com.example.googlemaps.PlaceLabeling;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.googlemaps.MapsActivity;


public class WorkWithSQLiteLabel {

    MapsActivity map;
    SQLiteDatabase db;
    String tblName;

    public WorkWithSQLiteLabel(SQLiteDatabase db, String table){
        this.db = db;
        this.tblName = table;
    }

    public void createTblLabel(){
        if(!map.checkIfTableExists(db, "Nhan")){
            db.execSQL("create table Nhan ( "
                            + "name text,"
                            + "address text,"
                            + "primary key (name, address));"
                            );

            addRow("Nhà riêng", "");
            addRow("Nơi làm việc", "");
        }
    }

    public void deleteRow(String name,String address){
        db.delete(tblName,"name=? And address=?",new String[]{name,address});
    }

    public void addRow(String name, String address){
        String query = "insert into Nhan(name, address) values (?,?)";
        db.execSQL(query,new Object[]{name, address});
    }



}

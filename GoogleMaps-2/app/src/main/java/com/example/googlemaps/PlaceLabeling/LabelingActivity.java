package com.example.googlemaps.PlaceLabeling;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.googlemaps.InfoSearching;
import com.example.googlemaps.MapsActivity;
import com.example.googlemaps.R;

import java.util.ArrayList;
import java.util.List;

public class LabelingActivity extends AppCompatActivity implements ReloadActivity  {

    SQLiteDatabase db;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_labeling);

//        setTitle("Nhãn đã lưu");
        getSupportActionBar().hide();

        db = this.openOrCreateDatabase("myDB",MODE_PRIVATE,null);

        listView = findViewById(R.id.listViewLabel);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InfoSearching is = (InfoSearching) adapterView.getItemAtPosition(i);

                // nếu như nhãn chưa có địa chỉ, khi click vào thì sẽ thêm địa chỉ cho nó
                if(is.getAddress().equals("")){
                    Intent intent = new Intent(getApplicationContext(), addLabel.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Name",is.getName());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        showListLabel();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void backActivity(View view){
        onBackPressed();
    }

    public void showListLabel(){

        List<InfoSearching> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from Nhan",null);

        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                InfoSearching is = new InfoSearching(name,address,0);
                list.add(is);


            }while (cursor.moveToNext());
        }

        ListLabelAdapter lla = new ListLabelAdapter(this,list, this::reloadActivity);
        listView.setAdapter(lla);

    }

    public void addLabel(View view){

        Intent intent = new Intent(this, addLabel.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        showListLabel();
    }

    @Override
    public void reloadActivity() {
        recreate();
    }
}
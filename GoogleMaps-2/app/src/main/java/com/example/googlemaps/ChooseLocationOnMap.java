package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.example.googlemaps.PlaceLabeling.setNameLabel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class ChooseLocationOnMap extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    MapsActivity mapsActivity;

    private LatLng myLocation = null;

    private String address = "";

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location_on_map);

//        setTitle("Chọn địa diểm");
        getSupportActionBar().hide();

        db = this.openOrCreateDatabase("myDB",MODE_PRIVATE,null);

        initMap();

        // lấy myLocation từ các activity trước
//        Bundle bundle = getIntent().getExtras();
//        double latitude = bundle.getDouble("Latitude");
//        double longitude = bundle.getDouble("Longitude");
//
//        LatLng location = new LatLng(latitude, longitude);
//        myLocation = location;


    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // hiển thị vị trí hiện tại
        map.setMyLocationEnabled(true);

        // tắt nút quay về vị trí hiện tại
        map.getUiSettings().setMyLocationButtonEnabled(false);

        getDeviceLocation();

    }

    public void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public synchronized void getDeviceLocation() {
        // để lấy vị trí hiện tại
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Log.e("getDeviceLocation", "getMyLocation");

        try {


            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            do {

                Task getLocation = fusedLocationProviderClient.getLastLocation();
                getLocation.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            boolean checkFirstView = false ;
                            if(myLocation == null){
                                checkFirstView = true;
                            }
                            Location location = (Location) getLocation.getResult();
                            if (location == null) {
                                myLocation = null;
                                Log.e("loop", "getDeviceLocation: null");
                                return;

                            }

                            // Add a marker in myLocation and move the camera
                            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            float tilt = 35; // độ nghiêng mới là 45 độ
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(myLocation)
                                    .zoom(17f)
                                    .tilt(tilt)
                                    .build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            if(checkFirstView == true){
                                map.animateCamera(cameraUpdate, 10, null);
                            }else{
                                map.animateCamera(cameraUpdate, 2000, null);
                            }

                            Log.e("loop", "getDeviceLocation: not null " + String.valueOf(myLocation.latitude));

                        }
                    }
                });

            } while (myLocation.equals(null));


        } catch (Exception e) {
            Log.e("GetDeviceLocation", e.getMessage());
        }

    }

    public void handleGps(View view){
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // Hiển thị thông báo yêu cầu bật vị trí
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vị trí của bạn chưa được bật");
            builder.setMessage("Để tiếp tục, vui lòng bật vị trí để có thể sử dụng chức năng định vị vị trí này của Google Maps");
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Mở cài đặt để người dùng bật vị trí
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Không, cảm ơn", null);
            builder.show();
        }
        else{
            getDeviceLocation();
        }


    }

    public void backActivity(View view){
        onBackPressed();
    }

    public void btnOk(View view) throws IOException {
        LatLng center = map.getCameraPosition().target;
        double lat = center.latitude;
        double lng = center.longitude;

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = geocoder.getFromLocation(lat,lng,1);

        if(list.size() > 0){
            address = list.get(0).getAddressLine(0);

            Log.e("Lấy latlng center", String.valueOf(lat) +" " +String.valueOf(lng) + " address : " + address );

            // kiểm tra xem đây là set địa chỉ cho nhãn đã có tên hay không
            // nếu đúng thì lưu vào db luôn, không thì thực hiện thêm bước đặt tên
            Bundle prevBundle = getIntent().getExtras();
            if(prevBundle != null && prevBundle.containsKey("Name")){
                ContentValues values = new ContentValues();
                values.put("address",  address);

                String whereClause = "name=?";
                String[] whereArgs = new String[] { prevBundle.getString("Name") };

                db.update("Nhan", values, whereClause, whereArgs);

                Intent resultIntent = new Intent();
                setResult(RESULT_OK,resultIntent);
                finish();
            }else{
                Intent intent = new Intent(this, setNameLabel.class);
                // gửi dữ liệu address qua cho activity đặt tên
                Bundle bundle = new Bundle();
                bundle.putString("Address", address);
                intent.putExtras(bundle);
                startActivityForResult(intent, 3);
            }



        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 3){
            if(resultCode == RESULT_OK){
                // Quay về activity trước
                Intent result = new Intent();
                setResult(RESULT_OK,result);
                onBackPressed();
            }
        }

    }


}
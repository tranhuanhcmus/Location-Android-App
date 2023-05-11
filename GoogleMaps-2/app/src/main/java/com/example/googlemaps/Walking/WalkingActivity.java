package com.example.googlemaps.Walking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.googlemaps.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class WalkingActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    private LatLng myLocation = null;
    private List<LatLng> listPointWalked = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private PolylineOptions polylineOptions = new PolylineOptions();
    private Polyline polyline;
    private float totalDistance = (float)0;
    private float averageStep = (float) 0.75;
    private float averageCaloPerStep = (float)0.05;
    private TextView totalDistanceTextView;
    private TextView totalStepTextView;
    private TextView caloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);

        initMap();

        getDeviceLocation();

        getSupportActionBar().hide();

        totalDistanceTextView = findViewById(R.id.totalDistance);
        totalStepTextView = findViewById(R.id.totalStep);
        caloTextView = findViewById(R.id.calo);

        // chỉnh kiểu hiển thị đường đi cho polyline
        PatternItem DOT = new Dash(20);
        PatternItem GAP = new Gap(10);
        List<PatternItem> PATTERN = new ArrayList<>();
        PATTERN.add(DOT);
        PATTERN.add(GAP);

        polylineOptions = new PolylineOptions()
                .width(15)
                .color(Color.rgb(165, 178, 176))
                ;
    }

    public void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

       polyline =  map.addPolyline(polylineOptions);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // hiển thị vị trí hiện tại
        map.setMyLocationEnabled(true);

        // tắt nút quay về vị trí hiện tại
        map.getUiSettings().setMyLocationButtonEnabled(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(30000)
                .setFastestInterval(20000)
                .setSmallestDisplacement(0)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                // thêm vị trí vào mảng danh sách các điểm đã đi qua
                LatLng newPoints = new LatLng(locationResult.getLastLocation().getLatitude()
                        ,locationResult.getLastLocation().getLongitude());
                addAndDrawPolyline(newPoints);

                LatLng lastedLocationWalked;

                if(listPointWalked.size() >= 2){
                    lastedLocationWalked = listPointWalked.get(listPointWalked.size() - 2);
                }else{
                    lastedLocationWalked = listPointWalked.get(listPointWalked.size() - 1);
                }
                calDistanceOfTwoLocation(lastedLocationWalked,newPoints);


                int totalStepCurrent = (int) (totalDistance/averageStep);
                float totalCaloCurrent = (float)(totalStepCurrent*averageCaloPerStep);

                totalDistanceTextView.setText("Khảng cách đã đi: "+String.valueOf(totalDistance) + " m");
                totalStepTextView.setText("Số bước chân: "+String.valueOf(totalStepCurrent));
                caloTextView.setText("Calo tiêu thụ: "+String.valueOf(totalCaloCurrent));

            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());

    }

    public void addAndDrawPolyline(LatLng newLatlng){

        polyline.getPoints().add(newLatlng);
        listPointWalked.add(newLatlng);
        polyline.setPoints(listPointWalked);

    }

    public void calDistanceOfTwoLocation(LatLng latLng1, LatLng latLng2){

        Location location1 = new Location("");
        location1.setLatitude(latLng1.latitude);
        location1.setLongitude(latLng2.longitude);

        Location location2 = new Location("");
        location2.setLatitude(latLng2.latitude);
        location2.setLongitude(latLng2.longitude);

        int distance = (int) location1.distanceTo(location2);
        totalDistance += distance;

        Log.e("WalkingActivity", "calDistanceOfTwoLocation: "+String.valueOf(distance) );

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        finish();
    }

    public void close(View view){
        onBackPressed();
    }

    public void handleGps(View view){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
        } else {
            getDeviceLocation();
        }
    }


}
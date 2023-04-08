package com.example.googlemaps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.googlemaps.Adapter.AutoCompleteAdapter;
import com.example.googlemaps.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_CODE_LOCATION = 1234;

    private FusedLocationProviderClient fusedLocationProviderClient;

    //var
    private boolean markerSearchCheck = false;
    private Marker markerSearch;


    private static boolean checkPermissionLocation = false;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private LatLng myLocation = null;

    //widgets
    private static AutoCompleteTextView textSearch;
    private static ImageView imgGps;
    private ListView myListView;


    //places
    PlacesClient placesClient;


    //token


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),getString(R.string.API_KEY));
        }


        placesClient = Places.createClient(this);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // fragment
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
//
//        autocompleteFragment.setCountries("VN");
//
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("fragment", "Place: " + place.getName() + ", " + place.getId());
//            }
//
//
//            @Override
//            public void onError(@NonNull Status status) {
//                // TODO: Handle the error.
//                Log.i("fragment", "An error occurred: " + status);
//            }
//        });


        // autoComplete code
        textSearch = findViewById(R.id.textSearch);

        // thiết lập adapter

        // check khi ô search thay đổi text
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence c, int i, int i1, int i2) {
                // khi người dùng nhập hoặc thay đổi từ khóa, autoComplete sẽ hiện ra
                if(c.length() > 0){
                    autoCompleteSearch(c.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        textSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InfoSearching infoSearching = (InfoSearching)adapterView.getItemAtPosition(i);
                textSearch.setText(infoSearching.getAddress());

                //tìm kiếm dựa trên thông tin của ô search
                geoLocate();
            }
        });
        // Thiết lập
        imgGps = findViewById(R.id.gps);

        getPermissionFromUser();


    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                UserListFragment fragment =
                        (UserListFragment) getSupportFragmentManager().findFragmentByTag("User List");
                if(fragment != null){
                    if(fragment.isVisible()){
                        getSupportFragmentManager().popBackStack();
                        return true;
                    }
                }
                finish();
                return true;
            }
            case R.id.action_sign_out:{
                signOut();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng HoChiMinhLocation = new LatLng(10.823099, 106.629662);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(HoChiMinhLocation,15f),2000,null);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                String name = marker.getTitle();

                LatLng position = marker.getPosition();
                double latitude = position.latitude;
                double longitude = position.longitude;

                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1 );
                    Address address = addresses.get(0);
                    String marketAddress = address.getAddressLine(0);




                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return true;
            }
        });

//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(@NonNull LatLng latLng) {
//                double latitude = latLng.latitude;
//                double longitude = latLng.longitude;
//
//                Geocoder geocoder = new Geocoder(getApplicationContext());
//                try {
//                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1 );
//
//                    if(addresses!= null && addresses.size() > 0){
//                        Address address = addresses.get(0);
//                        Toast.makeText(getApplicationContext(),address.toString(),Toast.LENGTH_SHORT).show();
//                    }
//
//
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });

        if (checkPermissionLocation) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // hiển thị vị trí hiện tại
            mMap.setMyLocationEnabled(true);

            // tắt nút quay về vị trí hiện tại
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            Search();
        }



    }

    public void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void getDeviceLocation() {
        // để lấy vị trí hiện tại
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Log.e("getDeviceLocation","getMyLocation");

        try {
            if (checkPermissionLocation) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Task getLocation = fusedLocationProviderClient.getLastLocation();
                getLocation.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location location = (Location)getLocation.getResult();
                            if(location == null){
                                myLocation = null;
                                return;
                            }
                            // Add a marker in myLocation and move the camera
                            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15f),2000,null);

                        }
                    }
                });
            }else{
                getPermissionFromUser();
            }

        }catch (Exception e){
            Log.e("GetDeviceLocation",e.getMessage());
        }

    }

    public void Search(){
        textSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // kiểm tra nếu người dùng bấm nút search ở bàn phím
                // Hoặc bấm nút done
                // Hoặc bấm nút enter
                // Hoặc chạm
                if(i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //gọi hàm geoLocate để lấy giá trị address sau khi search
                    geoLocate();

                    // sau khi bấm vào tim kiếm thì sẽ ẩn bàn phím
                    invisibleKeyBoard(textSearch);

                    return true;
                }
                return false;
            }
        });
    }

    public void autoCompleteSearch(String query){
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        String TAG = "AutoComplete";
        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
//                .setLocationBias(bounds)
//                //.setLocationRestriction(bounds)
                .setOrigin(myLocation)
                .setCountries("VN")
//                .setTypesFilter(Arrays.asList(TypeFilter.ADDRESS.toString()))
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {

            List<InfoSearching> infoSearchings = new ArrayList<>();
            List<AutocompletePrediction> autocompletePredictions = response.getAutocompletePredictions();

            // nếu có vị trí hiện tại thì trả về khoảng cách từ địa điểm đến vị trí hiện tại
            if(myLocation != null){
                for (int i = 0; i < autocompletePredictions.size(); i++) {
                    if(autocompletePredictions.get(i).getDistanceMeters() != null){
                        InfoSearching infoSearching = new InfoSearching(autocompletePredictions.get(i).getPrimaryText(null).toString(),
                                autocompletePredictions.get(i).getFullText(null).toString(),autocompletePredictions.get(i).getDistanceMeters());
                        infoSearchings.add(infoSearching);
                    }else{
                        InfoSearching infoSearching = new InfoSearching(autocompletePredictions.get(i).getPrimaryText(null).toString(),
                                autocompletePredictions.get(i).getFullText(null).toString(),-1);
                        infoSearchings.add(infoSearching);
                    }


                }
                AutoCompleteAdapter aa = new AutoCompleteAdapter(MapsActivity.this,infoSearchings);
                textSearch.setAdapter(aa);
                aa.notifyDataSetChanged();

                //aa.addAllItems(infoSearchings);

                //Toast.makeText(this, infoSearchings.get(0).getAddress(), Toast.LENGTH_SHORT).show();
            }else{
                for (int i = 0; i < autocompletePredictions.size(); i++) {
                    InfoSearching infoSearching = new InfoSearching(autocompletePredictions.get(i).getPrimaryText(null).toString(),
                            autocompletePredictions.get(i).getFullText(null).toString(),-1);
                    infoSearchings.add(infoSearching);

                }
                AutoCompleteAdapter aa = new AutoCompleteAdapter(MapsActivity.this,infoSearchings);
                textSearch.setAdapter(aa);
                aa.notifyDataSetChanged();

                //aa.addAllItems(infoSearchings);

            }




        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    public void callGeoLocateForOnclick(View view){
        invisibleKeyBoard(view);
        geoLocate();
    }

    private void geoLocate(){


        String searchString = textSearch.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (Exception e){
            Log.e("geoLocate", "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d("geoLocate", "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();


            LatLng myLocation = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
            // kiểu tra xem markersearch tồn tại hay chưa
            // nếu chưa tổn tại marker thì thêm marker mới nó vào
            if(!markerSearchCheck){
                markerSearch = mMap.addMarker(new MarkerOptions().position(myLocation).title("Marker in search location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15f),2000,null);
                markerSearchCheck = true;

            }else{
                markerSearch.setPosition(myLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15f),2000,null);
            }

        }
    }


    public void getPermissionFromUser(){
        String permission[] = {
                FINE_LOCATION,
                COARSE_LOCATION
        };

        // check xem người dùng đã cấp phép vị trí GPS chưa( vị trí chính xác )
        if(ContextCompat.checkSelfPermission(this,FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            // check xem người dùng đã cấp phép vị trí Wifi, mạng... chưa( vị trí xấp xỉ )
            if(ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                checkPermissionLocation = true;
                initMap();
            }else{
                // nếu chưa cấp phép 1 trong 2, sẽ yêu cầu người dùng cấp phép, hàm requestPermissions sẽ gọi hàm
                // onRequestPermissionResult thực thi
                ActivityCompat.requestPermissions(this,permission,REQUEST_CODE_LOCATION);
            }

        }
        else{
            ActivityCompat.requestPermissions(this,permission,REQUEST_CODE_LOCATION);
        }
    }

    @Override
    // hàm xử lý các cấp phép của người dùng
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        checkPermissionLocation = false;
        switch (requestCode){
            case REQUEST_CODE_LOCATION:{

                // kiểu tra xem grantResult có trả về gì không
                if(grantResults.length > 0){
                    // nếu có thì check từng phần tử trả về, có một quyền chưa được cấp phép thì return false

                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            checkPermissionLocation = false;
                            return;
                        }
                    }

                    checkPermissionLocation = true;
                    initMap();
                }
            }
        }
    }

    // Hàm để xét sự kiện gps, khi nhấn vào nút gps, cam sẽ trở về vị trí người dùng
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


    public void invisibleKeyBoard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



}
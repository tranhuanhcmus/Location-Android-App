package com.example.googlemaps.PlaceLabeling;

import static com.example.googlemaps.MapsActivity.db;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.example.googlemaps.Adapter.AutoCompleteAdapter;
import com.example.googlemaps.ChooseLocationOnMap;
import com.example.googlemaps.InfoSearching;
import com.example.googlemaps.MapsActivity;
import com.example.googlemaps.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class addLabel extends AppCompatActivity {

    AutoCompleteTextView textSearch;

    PlacesClient placesClient;

    private final int Request_Code_ChooseLocationOnMap = 2;

    private final int Request_Code_SetNameLabel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_label);

//        setTitle("Thêm nhãn");
        getSupportActionBar().hide();

        db = this.openOrCreateDatabase("myDB",MODE_PRIVATE,null);

        textSearch = findViewById(R.id.textSearch);

        // khi người dùng thay đổi ô nhập địa chỉ, gợi ý khác sẽ xuất hiện thông qua PlaceAutoComplete
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0){
                    autoCompleteSearch(textSearch,charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        textSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // lấy thông tin địa chỉ đã nhận từ autoComplete
                InfoSearching is = (InfoSearching)adapterView.getItemAtPosition(i);
                String address = is.getAddress();

                // kiểm tra xem activity trước có gửi tên địa điểm không, nếu có thì ko cần đặt tên nữa
                // lưu thẳng vào db luôn
                Bundle prevBundle = getIntent().getExtras();
                if(prevBundle != null && prevBundle.containsKey("Name")){
                    //Thêm nhãn vào sqlite
                    ContentValues values = new ContentValues();
                    values.put("address",  address);

                    String whereClause = "name=?";
                    String[] whereArgs = new String[] { prevBundle.getString("Name") };

                    db.update("Nhan", values, whereClause, whereArgs);
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK,resultIntent);
                    finish();
                }else{
                    // gửi địa chỉ này qua cho activity đặt tên nhãn
                    Intent intent = new Intent(getApplicationContext(), setNameLabel.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Address",address);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,Request_Code_SetNameLabel);
                }


            }
        });

        placesClient = Places.createClient(this);
    }

    public void chooseOnMap(View view){

        Intent intent = new Intent(this, ChooseLocationOnMap.class);
        Bundle prevBundle = getIntent().getExtras();
        if(prevBundle != null && prevBundle.containsKey("Name")){
            intent.putExtras(prevBundle);
        }
        startActivityForResult(intent,Request_Code_ChooseLocationOnMap);



    }

    public void autoCompleteSearch(AutoCompleteTextView textView,String query){
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

                .setCountries("VN")
//                .setTypesFilter(Arrays.asList(TypeFilter.ADDRESS.toString()))
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {

            List<InfoSearching> infoSearchings = new ArrayList<>();
            List<AutocompletePrediction> autocompletePredictions = response.getAutocompletePredictions();

            // nếu có vị trí hiện tại thì trả về khoảng cách từ địa điểm đến vị trí hiện tại

            for (int i = 0; i < autocompletePredictions.size(); i++) {
                InfoSearching infoSearching = new InfoSearching(autocompletePredictions.get(i).getPrimaryText(null).toString(),
                        autocompletePredictions.get(i).getFullText(null).toString(),-1);
                infoSearchings.add(infoSearching);

            }
            AutoCompleteAdapter aa = new AutoCompleteAdapter(this,infoSearchings);
            textView.setAdapter(aa);
            aa.notifyDataSetChanged();

            //aa.addAllItems(infoSearchings);




        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Request_Code_ChooseLocationOnMap){
            if(resultCode == RESULT_OK){
                // Quay về activity trước
                Intent intentResult = new Intent();
                setResult(RESULT_OK,intentResult);
                Log.e("addLabel", "onActivityResultChooseLocationOnMap: finish" );
                finish();
            }
        }

        if(requestCode == Request_Code_SetNameLabel){
            if(resultCode == RESULT_OK){
                // Quay về activity trước
                Intent intentResult = new Intent();
                setResult(RESULT_OK,intentResult);
                Log.e("addLabel", "onActivityResultSetNameLabel: finish" );
                finish();
            }
        }
    }

    public void backActivity(View view){
        onBackPressed();
    }
}
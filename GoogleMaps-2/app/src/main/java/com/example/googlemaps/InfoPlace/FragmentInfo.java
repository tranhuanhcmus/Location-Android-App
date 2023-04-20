package com.example.googlemaps.InfoPlace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.googlemaps.Direction.Distance;
import com.example.googlemaps.Direction.Duration;
import com.example.googlemaps.InfoPlace.FragmentCallbacks;
import com.example.googlemaps.InfoPlace.MainCallbacks;
import com.example.googlemaps.InfoPlace.infoPlace;
import com.example.googlemaps.MapsActivity;
import com.example.googlemaps.R;

public class FragmentInfo extends Fragment implements FragmentCallbacks {

    MapsActivity map;

    TextView name;
    TextView address;
    TextView rating;
    AppCompatButton btnDirection;
    AppCompatButton btnStart;
    AppCompatButton btnBookmark;
    AppCompatButton btnShare;

    TextView time;

    TextView distance;

    View view;

    public final String drivingMode = "driving";
    public final String motoMode = "moto";
    public final String transitMode = "transit";
    public final String walkMode = "walking";

    LinearLayout linearLayoutFrag1;
    LinearLayout linearLayoutFrag2;

    public static com.example.googlemaps.InfoPlace.infoPlace infoPlace;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof MainCallbacks)) {
            throw new IllegalStateException("Activity must implement MainCallbacks");
        }
        map = (MapsActivity) getActivity(); // use this reference to invoke main callbacks


    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view_layout_detailsInfo = inflater.inflate(R.layout.detail_place,null);

        name = view_layout_detailsInfo.findViewById(R.id.name);
        address = view_layout_detailsInfo.findViewById(R.id.address);
        rating = view_layout_detailsInfo.findViewById(R.id.rating);
        btnDirection = view_layout_detailsInfo.findViewById(R.id.direction);
        btnBookmark = view_layout_detailsInfo.findViewById(R.id.save);
        btnStart = view_layout_detailsInfo.findViewById(R.id.start);
        btnShare = view_layout_detailsInfo.findViewById(R.id.share);

        time = view_layout_detailsInfo.findViewById(R.id.time);
        distance = view_layout_detailsInfo.findViewById(R.id.distance);

        linearLayoutFrag1 = view_layout_detailsInfo.findViewById(R.id.linearInfo);
        linearLayoutFrag2 = view_layout_detailsInfo.findViewById(R.id.linearDirection);

        linearLayoutFrag2.setVisibility(View.GONE);

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.originLocation = map.myLocation;
                map.destLocation = infoPlace.position;
                map.sendRequestFindPath(map.myLocation, infoPlace.position, motoMode);
                map.linearLayout1.setVisibility(View.GONE);
                map.linearLayout2.setVisibility(View.VISIBLE);

                linearLayoutFrag1.setVisibility(View.GONE);
                linearLayoutFrag2.setVisibility(View.VISIBLE);

                //map.textSearchOrigin.setText("Vị trí của bạn");
                map.textSearchDest.setText(infoPlace.name);
                map.textSearchOrigin.setText("");

            }
        });

        return view_layout_detailsInfo;

    }

    @Override
    public void onMsgFromMainToFrag(infoPlace infoPlace) {

        this.infoPlace = infoPlace;
        name.setText(infoPlace.name);
        address.setText(infoPlace.address);
        rating.setText("Rating: " + infoPlace.rating);


    }

    @Override
    public void onMsgFromMainToFrag(String info) {
        if(info == "backDirection"){
            map.linearLayout2.setVisibility(View.GONE);
            map.linearLayout1.setVisibility(View.VISIBLE);

            linearLayoutFrag2.setVisibility(View.GONE);
            linearLayoutFrag1.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMsgFromMainToFrag(Distance distance, Duration duration) {
        infoPlace.distance = distance;
        infoPlace.duration = duration;

        time.setText(infoPlace.duration.text);
        this.distance.setText(" ( " + infoPlace.distance.text + " ) ");

        // mặc định mới vào tìm địa chỉ thì traffic mode là xe gắn máy
        if(view == null){
            map.timeMoto.setText(infoPlace.duration.text);
            map.findViewById(R.id.moto).setBackgroundColor(Color.parseColor("#66FFFF"));
        }

        // set text trên icon traffic mode
        if(view != null){
            if(this.view.getId() == map.findViewById(R.id.car).getId()){
                map.timeCar.setText(infoPlace.duration.text);
            }

            if(this.view.getId() == map.findViewById(R.id.moto).getId()){
                map.timeMoto.setText(infoPlace.duration.text);
            }

            if(this.view.getId() == map.findViewById(R.id.bus).getId()){
                map.timeBus.setText(infoPlace.duration.text);
            }

            if(this.view.getId() == map.findViewById(R.id.walk).getId()){
                map.timeWalk.setText(infoPlace.duration.text);
            }
        }


    }

    @Override
    public void onMsgFromMainToFrag(View view) {
        this.view = view;
    }
}

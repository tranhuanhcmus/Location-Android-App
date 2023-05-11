package com.example.googlemaps.InfoPlace;

import static android.content.Context.PRINT_SERVICE;
import static com.example.googlemaps.MapsActivity.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.googlemaps.Direction.Distance;
import com.example.googlemaps.Direction.Duration;
import com.example.googlemaps.GuideDirection.GuideByText;
import com.example.googlemaps.InfoPlace.FragmentCallbacks;
import com.example.googlemaps.InfoPlace.MainCallbacks;
import com.example.googlemaps.InfoPlace.infoPlace;
import com.example.googlemaps.MapsActivity;
import com.example.googlemaps.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FragmentInfo extends Fragment implements FragmentCallbacks {

    MapsActivity map;

    TextView name;
    TextView address;
    TextView rating;
    AppCompatButton btnDirection;
    AppCompatButton btnStart;
    AppCompatButton btnBookmark;
    AppCompatButton btnShare;
    AppCompatButton btnStart2;
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
        btnStart2 = view_layout_detailsInfo.findViewById(R.id.start2);
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
                map.sendRequestFindPath(map.originLocation, infoPlace.position, motoMode);
                map.trafficMode = motoMode;
                map.linearLayout1.setVisibility(View.GONE);
                map.linearLayout2.setVisibility(View.VISIBLE);

                linearLayoutFrag1.setVisibility(View.GONE);
                linearLayoutFrag2.setVisibility(View.VISIBLE);

                //map.textSearchOrigin.setText("Vị trí của bạn");
                map.textSearchDest.setText(infoPlace.name);
                map.textSearchOrigin.setText("");
                Log.e("BtnDirection", "onClick" );

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    shareLocation(getContext(), infoPlace.address);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = getContext().openOrCreateDatabase("myDB",Context.MODE_PRIVATE,null);
                // nếu button bookmark đang ở trạng thái chưa lưu, click vào button lưu, nó sẽ được lưu vào bookmark
                if(!checkIfBookMarkExist()){

                    String query = "insert into Luu(name,address) values(?,?)";
                    db.execSQL(query,new Object[]{infoPlace.name, infoPlace.address});
                    btnBookmark.setText("Hủy lưu");

                    // set lại ảnh cho bookmark
                    Drawable drawableBookmark = getResources().getDrawable(R.drawable.ic_bookmark_remove);
                    btnBookmark.setCompoundDrawablesWithIntrinsicBounds(drawableBookmark,null,null,null);

                    Toast.makeText(map, "Đã lưu địa điểm vào danh mục đã lưu", Toast.LENGTH_SHORT).show();
                }else{
                    String query = "delete from table Luu where address=?";
                    db.delete("Luu","address=?",new String[]{infoPlace.address});
                    btnBookmark.setText("Yêu thích");

                    // set lại ảnh cho bookmark
                    Drawable drawableBookmark = getResources().getDrawable(R.drawable.ic_bookmark_add);
                    btnBookmark.setCompoundDrawablesWithIntrinsicBounds(drawableBookmark,null,null,null);
                }

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GuideByText.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitudeOrigin",map.myLocation.latitude);
                bundle.putDouble("longitudeOrigin",map.myLocation.longitude);
                bundle.putDouble("latitudeDest",infoPlace.position.latitude);
                bundle.putDouble("longitudeDest",infoPlace.position.longitude);
                bundle.putString("trafficMode",map.trafficMode);
                bundle.putString("address", infoPlace.address);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        btnStart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GuideByText.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitudeOrigin",map.myLocation.latitude);
                bundle.putDouble("longitudeOrigin",map.myLocation.longitude);
                bundle.putDouble("latitudeDest",infoPlace.position.latitude);
                bundle.putDouble("longitudeDest",infoPlace.position.longitude);
                bundle.putString("trafficMode",map.trafficMode);
                bundle.putString("address", infoPlace.address);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        return view_layout_detailsInfo;

    }

    public static void shareLocation(Context context, String address) throws UnsupportedEncodingException {
        // Tạo Uri cho ứng dụng của bạn
        String encode = URLEncoder.encode(address,"UTF-8");
        String link = "http://www.my_google_maps.com/location?address=" + encode;
        Uri uri = Uri.parse(link);

        // Tạo intent chia sẻ thông tin vị trí
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ vị trí");
        shareIntent.putExtra(Intent.EXTRA_TEXT, address + "\n" + uri);


        // Khởi tạo ShareSheet
        Intent chooserIntent = Intent.createChooser(shareIntent, "Chia sẻ vị trí");

        // Start activity để chia sẻ thông tin vị trí
        context.startActivity(chooserIntent);
    }

    @Override
    public void onMsgFromMainToFrag(infoPlace infoPlace) {

        this.infoPlace = infoPlace;
        name.setText(infoPlace.name);
        address.setText(infoPlace.address);
        rating.setText("Rating: " + infoPlace.rating);



        if(checkIfBookMarkExist()){
            btnBookmark.setText("Hủy lưu");
            Drawable drawableBookmark = getResources().getDrawable(R.drawable.ic_bookmark_add);
            btnBookmark.setCompoundDrawablesWithIntrinsicBounds(drawableBookmark,null,null,null);
        }else{
            Drawable drawableBookmark = getResources().getDrawable(R.drawable.ic_bookmark_remove);
            btnBookmark.setCompoundDrawablesWithIntrinsicBounds(drawableBookmark,null,null,null);
        }

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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onMsgFromMainToFrag(Distance distance, Duration duration) {
        infoPlace.distance = distance;
        infoPlace.duration = duration;

        time.setText(infoPlace.duration.text);
        this.distance.setText(" ( " + infoPlace.distance.text + " ) ");

        // mặc định mới vào tìm địa chỉ thì traffic mode là xe gắn máy
        if(view == null){
            map.trafficMode = motoMode;
            map.timeMoto.setText(infoPlace.duration.text);
            map.findViewById(R.id.moto).setBackgroundResource(R.drawable.custom_traffic_mode);
            //map.findViewById(R.id.imageViewMoto).setBackgroundColor(R.color.blue1);
        }

        // set text trên icon traffic mode
        if(view != null){
            if(this.view.getId() == map.findViewById(R.id.car).getId()){
                map.trafficMode = drivingMode;
                map.timeCar.setText(infoPlace.duration.text);
            }

            if(this.view.getId() == map.findViewById(R.id.moto).getId()){
                map.trafficMode = motoMode;
                map.timeMoto.setText(infoPlace.duration.text);
            }

            if(this.view.getId() == map.findViewById(R.id.bus).getId()){
                map.trafficMode = transitMode;
                map.timeBus.setText(infoPlace.duration.text);
            }

            if(this.view.getId() == map.findViewById(R.id.walk).getId()){
                map.trafficMode = walkMode;
                map.timeWalk.setText(infoPlace.duration.text);
            }
        }


    }

    @Override
    public void onMsgFromMainToFrag(View view) {
        this.view = view;
    }

    public boolean checkIfBookMarkExist(){
        SQLiteDatabase db = getContext().openOrCreateDatabase("myDB",Context.MODE_PRIVATE,null);
        String query = "select * from Luu where address=?";
        Cursor cursor = db.rawQuery(query,new String[]{infoPlace.address});
        if(cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }
}

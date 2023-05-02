package com.example.googlemaps.PlaceLabeling;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemaps.InfoSearching;
import com.example.googlemaps.MapsActivity;
import com.example.googlemaps.R;

import java.util.List;

public class ListLabelAdapterForMap extends RecyclerView.Adapter<ListLabelAdapterForMap.MyViewHolder> {
    private List<InfoSearching> mDataset;
    private ViewGroup parent;
    private int viewType;
    private Context context;

    public static View view;

    MapsActivity map ;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout layoutItem;
        private TextView name;
        private TextView address;
        private ImageView viewPlace;
        public MyViewHolder(View v) {
            super(v);
            view = v;
            name = v.findViewById(R.id.textTitlePlace);
            address = v.findViewById(R.id.textLocation);
            viewPlace = v.findViewById(R.id.viewPlace);
            layoutItem = v.findViewById(R.id.layout_item);
        }
    }

    @SuppressLint("RestrictedApi")
    public ListLabelAdapterForMap(Context context, List<InfoSearching> myDataset) {
        mDataset = myDataset;
        this.context = context;
        map = (MapsActivity) getActivity(context);

    }

    @Override
    public ListLabelAdapterForMap.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items_label_for_map, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        InfoSearching is = mDataset.get(position);
        holder.name.setText(mDataset.get(position).getName());
        holder.address.setText(mDataset.get(position).getAddress());

        if(mDataset.get(position).getName().equals("Nhà riêng")){
            holder.viewPlace.setImageResource(R.drawable.ic_home);

        }else if(mDataset.get(position).getName().equals("Nơi làm việc")){
            holder.viewPlace.setImageResource(R.drawable.ic_work_place);

        } else if (mDataset.get(position).getName().equals("Khác")) {
            holder.viewPlace.setImageResource(R.drawable.ic_more);
        } else{
            holder.viewPlace.setImageResource(R.drawable.ic_flag);

        }

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // nếu click vào nhãn mà nhãn đó đã có địa chỉ thì tìm vị trí nhãn đó
                if(!is.getAddress().equals("")){

                    Log.e("ListLabelAdapterForMap", "onClick: " + is.getAddress() );

                    // nếu click vào ô có tên là khác thì vào xem danh sách nhãn đã lưu
                    if(is.getName().equals("Khác")){
                        Intent intent = new Intent(context, LabelingActivity.class);
                        context.startActivity(intent);
                        return;
                    }

                    // nếu click vào các nhãn đã có địa chỉ thì sẽ hiện điểm đó trên bản đồ
                    if(map.textSearch.isFocused()){
                        map.textSearch.setText(is.getAddress());
                        Log.e("ListLabelAdapterForMap", "onClick: " + map.textSearch.getText());
                        map.geoLocate(map.textSearch, "dest");
                        map.invisibleKeyBoard(map.textSearch);
                    }else if(map.textSearchOrigin.isFocused()){
                        map.textSearchOrigin.setText(is.getAddress());
                        map.geoLocate(map.textSearchOrigin, "origin");
                        map.invisibleKeyBoard(map.textSearchOrigin);
                    }else{
                        map.textSearchDest.setText(is.getAddress());
                        map.geoLocate(map.textSearchDest, "dest");
                        map.invisibleKeyBoard(map.textSearchDest);
                    }

                }else{
                    Intent intent = new Intent(context, addLabel.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Name",is.getName());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    return;
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}










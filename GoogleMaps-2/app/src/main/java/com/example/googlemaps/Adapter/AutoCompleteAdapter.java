package com.example.googlemaps.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.googlemaps.InfoSearching;
import com.example.googlemaps.R;

import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<InfoSearching> {
    Context context;
    List<InfoSearching> listPrediction;

    public AutoCompleteAdapter(@NonNull Context context, @NonNull List<InfoSearching> objects) {
        super(context, 0, objects);
        this.context = context;
        this.listPrediction = objects;
    }

    @Override
    public int getCount() {
        if(listPrediction == null){
            return 0;
        }
        return listPrediction.size();
    }


    public void addAllItems(List<InfoSearching> list) {
        listPrediction.clear();
        if (!list.isEmpty()){
            listPrediction.addAll(list);
        }

        notifyDataSetChanged();
    }


    @Override
    public void clear() {
        listPrediction.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(this.context).inflate(R.layout.list_item_searching,parent,false);
        }

        InfoSearching is = listPrediction.get(position);

        // hiển thị tên địa điểm
        TextView name = convertView.findViewById(R.id.textTitlePlace);
        name.setText(is.getName());

        // hiển thị vị trí địa điểm
        TextView location = convertView.findViewById(R.id.textLocation);
        location.setText(is.getAddress());

//        lấy khoảng cách
        TextView distance = convertView.findViewById(R.id.distance);
        if(is.getDistance() != -1){
            distance.setText(String.valueOf((float) is.getDistance()/1000)+ "km");
        }

        return convertView;
    }
}

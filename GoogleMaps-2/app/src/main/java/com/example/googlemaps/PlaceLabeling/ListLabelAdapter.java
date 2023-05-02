package com.example.googlemaps.PlaceLabeling;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.googlemaps.InfoPlace.FragmentInfo;
import com.example.googlemaps.InfoSearching;
import com.example.googlemaps.R;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class ListLabelAdapter extends ArrayAdapter<InfoSearching> {
    Context context;
    List<InfoSearching> listPrediction;

    ReloadActivity reloadActivity;

    public ListLabelAdapter(@NonNull Context context, @NonNull List<InfoSearching> objects, ReloadActivity reloadActivity) {
        super(context, 0, objects);
        this.context = context;
        this.listPrediction = objects;
        this.reloadActivity = reloadActivity;
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
            convertView = LayoutInflater.from(this.context).inflate(R.layout.list_items_label,parent,false);
        }

        InfoSearching is = listPrediction.get(position);

        ImageView viewPlace = convertView.findViewById(R.id.viewPlace);
        if(is.getName().equals("Nhà riêng")){
            viewPlace.setImageResource(R.drawable.ic_home);

        }else if(is.getName().equals("Nơi làm việc")){
            viewPlace.setImageResource(R.drawable.ic_work_place);

        }else{
            viewPlace.setImageResource(R.drawable.ic_flag);

        }

        // hiển thị tên địa điểm
        TextView name = convertView.findViewById(R.id.textTitlePlace);
        name.setText(is.getName());

        // hiển thị vị trí địa điểm
        TextView location = convertView.findViewById(R.id.textLocation);
        location.setText(is.getAddress());

        ImageView moreVert = convertView.findViewById(R.id.moreVert);
        if(is.getAddress().equals("")){
            moreVert.setVisibility(View.GONE);
        }else{
            moreVert.setVisibility(View.VISIBLE);
        }


        moreVert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, view);
                // nếu như là nhãn nhà riêng hoặc cơ quan thì sử lý menu label special, các nhãn khác thì
                // xử lý theo menu label default
                if(is.getName().equals("Nhà riêng") || is.getName().equals("Nơi làm việc")){
                    popupMenu.inflate(R.menu.menu_label_special);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.item1:
                                    Log.e("Popup menu", "onMenuItemClick: 1" );
                                    // chia sẻ địa chỉ
                                    try {
                                        FragmentInfo.shareLocation(context, is.getAddress());
                                    } catch (UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return true;

                                case R.id.item2:
                                    Log.e("Popup menu", "onMenuItemClick: 2" );
                                    // Sap chép địa chỉ
                                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("label", is.getAddress());
                                    clipboard.setPrimaryClip(clip);

                                    Toast.makeText(context, "Đã copy địa chỉ vào bộ nhớ đệm", Toast.LENGTH_SHORT).show();
                                    return true;

                                case R.id.item3:
                                    Log.e("Popup menu", "onMenuItemClick: 3" );
                                    // chỉnh sửa địa chỉ
                                    // gọi qua activity add label để chọn địa chỉ
                                    Intent intent = new Intent(context, addLabel.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Name",is.getName());
                                    intent.putExtras(bundle);
                                    context.startActivity(intent);

                                    // load lại activity
                                    reloadActivity.reloadActivity();
                                    return true;

                                case R.id.item4:
                                    Log.e("Popup menu", "onMenuItemClick: 4" );
                                    // xóa địa chỉ
                                    SQLiteDatabase db = context.openOrCreateDatabase("myDB",Context.MODE_PRIVATE,null);
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("address","");
                                    db.update("Nhan",contentValues,"name=? And address=?",new String[]{is.getName(),is.getAddress()});

                                    // load lại activity
                                    reloadActivity.reloadActivity();
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                }else{
                    popupMenu.inflate(R.menu.menu_label_default);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.item1:
                                    Log.e("Popup menu", "onMenuItemClick: 1" );
                                    // chia sẻ địa chỉ
                                    try {
                                        FragmentInfo.shareLocation(context, is.getAddress());
                                    } catch (UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return true;

                                case R.id.item2:
                                    Log.e("Popup menu", "onMenuItemClick: 2" );
                                    // Sap chép địa chỉ
                                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("label", is.getAddress());
                                    clipboard.setPrimaryClip(clip);

                                    Toast.makeText(context, "Đã copy địa chỉ vào bộ nhớ đệm", Toast.LENGTH_SHORT).show();
                                    return true;

                                case R.id.item3:
                                    Log.e("Popup menu", "onMenuItemClick: 3" );
                                   // xóa nhãn
                                    SQLiteDatabase db = context.openOrCreateDatabase("myDB",Context.MODE_PRIVATE,null);
                                    db.delete("Nhan","name=? AND address=?",new String[]{is.getName(),is.getAddress()});

                                    //load lại activity
                                    reloadActivity.reloadActivity();

                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                }


                // show menu
                popupMenu.show();


            }
        });




        return convertView;
    }

}

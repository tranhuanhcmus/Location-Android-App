package com.example.googlemaps.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemaps.Fragments.UserListFragment;
import com.example.googlemaps.R;
import com.example.googlemaps.model.User;

import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {


    private ArrayList<User> mUsers ;
    private  UserListRecyclerClickListener mClickListener;

    public UserRecyclerAdapter(ArrayList<User> users,UserListRecyclerClickListener clickListener) {
        this.mUsers = users;
        this.mClickListener=clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view,mClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=mUsers.get(position);
        ((ViewHolder)holder).username.setText(user.getUsername());
        ((ViewHolder)holder).email.setText(user.getEmail());

         String phoneNumber = user.getTelephone();

        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext=v.getContext();
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    makePhoneCall(mContext, phoneNumber);
                } else {
                   Toast.makeText(mContext,"Call is not permit",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView username, email;
        ImageButton callBtn;
        UserListRecyclerClickListener mUserRecyclerClickListener;
        public ViewHolder(View itemView, UserListRecyclerClickListener clickListener) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            mUserRecyclerClickListener =clickListener;
            callBtn=itemView.findViewById(R.id.callBtn);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mUserRecyclerClickListener.onUserClick(getAdapterPosition());
        }
    }
    public interface UserListRecyclerClickListener{
        void onUserClick(int position);
    }


    private void makePhoneCall(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

}
















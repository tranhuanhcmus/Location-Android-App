package com.example.googlemaps.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemaps.R;
import com.example.googlemaps.model.User;

import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>{

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

        ((ViewHolder)holder).username.setText(mUsers.get(position).getUsername());
        ((ViewHolder)holder).email.setText(mUsers.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView username, email;
        UserListRecyclerClickListener mUserRecyclerClickListener;
        public ViewHolder(View itemView, UserListRecyclerClickListener clickListener) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            mUserRecyclerClickListener =clickListener;
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
}
















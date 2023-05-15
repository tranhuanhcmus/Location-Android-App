package com.example.googlemaps.Adapter;

import static android.content.ContentValues.TAG;

import static com.example.googlemaps.utils.ImageLoader.loadImageChatMessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemaps.Fragments.PhotoViewActivity;
import com.example.googlemaps.R;
import com.example.googlemaps.model.Message;
import com.example.googlemaps.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatMessageRecyclerAdapter extends RecyclerView.Adapter<ChatMessageRecyclerAdapter.ViewHolder>{

    private ArrayList<Message> mMessages = new ArrayList<>();
    private ArrayList<User> mUsers = new ArrayList<>();
    private Context mContext;
    private static final int MSG_TYPE_LEFT_RECEIVED = 0;
    private static final int MSG_TYPE_RIGHT_RECEIVED = 1;

    public ChatMessageRecyclerAdapter(ArrayList<Message> messages,
                                      ArrayList<User> users,
                                      Context context) {
        this.mMessages = messages;
        this.mUsers = users;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_layout, parent, false);
            final ViewHolder holder = new ViewHolder(view);

            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_layout_right, parent, false);
            final ViewHolder holder = new ViewHolder(view);

            return holder;
        }

    }
    public String timeStampConversionToTime(Date date) {


        @SuppressLint("SimpleDateFormat") SimpleDateFormat jdf = new SimpleDateFormat("hh:mm a");
        jdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        return jdf.format(date);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String time_msg_received = timeStampConversionToTime(mMessages.get(position).getTimestamp());

        if(FirebaseAuth.getInstance().getUid().equals(mMessages.get(position).getUser().getUser_id())){
            ((ViewHolder)holder).username.setTextColor(ContextCompat.getColor(mContext, R.color.green1));
        }
        else{
            ((ViewHolder)holder).username.setTextColor(ContextCompat.getColor(mContext, com.google.android.libraries.places.R.color.quantum_yellow));
        }
        Log.d(TAG,mMessages.get(position).getType()+"0k");
        if(mMessages.get(position).getType().equals("image")){
            holder.message.setVisibility(View.GONE);
            holder.ImageMsg.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).username.setText(mMessages.get(position).getUser().getUsername());
            loadImageChatMessage(((ViewHolder)holder).ImageMsg,mMessages.get(position).getMessage());

            ((ViewHolder)holder).tv_time.setText(time_msg_received);
            ((ViewHolder)holder).ImageMsg.setOnClickListener(view -> {
                Intent intent = new Intent(this.mContext, PhotoViewActivity.class);
                intent.putExtra("CHAT_IMAGE_URL", mMessages.get(position).getMessage());
                this.mContext.startActivity(intent);
            });
        }
        else {
            holder.ImageMsg.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).username.setText(mMessages.get(position).getUser().getUsername());
            ((ViewHolder) holder).message.setText(mMessages.get(position).getMessage());
            ((ViewHolder) holder).tv_time.setText(time_msg_received);
        }
    }



    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView message, username, tv_time;
        RoundedImageView ImageMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message_message);
            username = itemView.findViewById(R.id.chat_message_username);
            tv_time = itemView.findViewById(R.id.tv_chat_time_received);
            ImageMsg = itemView.findViewById(R.id.ImageMsg);
        }
    }
    public int getItemViewType(int position) {
        if (FirebaseAuth.getInstance().getUid().equals(mMessages.get(position).getUser().getUser_id())) {
            return MSG_TYPE_LEFT_RECEIVED;
        } else return MSG_TYPE_RIGHT_RECEIVED;
    }


}

















package com.indianapp.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.indianapp.chatapp.Activities.ChatActivity;
import com.indianapp.chatapp.Activities.ImageAcitvity;
import com.indianapp.chatapp.Models.UserModel;
import com.indianapp.chatapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveUserAdapter extends RecyclerView.Adapter<ActiveUserAdapter.ViewHolder> {
    private ArrayList<UserModel> activeUsersList;
    private Context ctx;

    private LayoutInflater inflater;

    private SharedPreferences preferences;


    public ActiveUserAdapter(Context ctx, ArrayList<UserModel> activeUsersList) {
        this.activeUsersList = activeUsersList;
        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ActiveUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_active_user_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveUserAdapter.ViewHolder holder, int position) {
        UserModel userModel = activeUsersList.get(position);
        holder.lastMessage.setText(userModel.getLastMessage());
        holder.username.setText(userModel.getUsername());
        if (userModel.getNumberOfUnreadMsg() > 0 && userModel.getNumberOfUnreadMsg() < 99) {
            holder.numOfUnread.setVisibility(View.VISIBLE);
            holder.circle.setVisibility(View.VISIBLE);
            holder.numOfUnread.setText(String.valueOf(userModel.getNumberOfUnreadMsg()));

        } else if (userModel.getNumberOfUnreadMsg() > 99) {
            holder.numOfUnread.setVisibility(View.VISIBLE);
            holder.circle.setVisibility(View.VISIBLE);
            holder.numOfUnread.setText("99+");
        } else {
            holder.circle.setVisibility(View.INVISIBLE);
            holder.numOfUnread.setVisibility(View.INVISIBLE);
        }
        Long timeStamp = Long.valueOf(userModel.getTime());
        Date date = new Date(timeStamp);
        SimpleDateFormat sf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        String timeS = sf.format(date);
        holder.time.setText(timeS);
        Glide.with(ctx)
                .load(userModel.getImageUrl())
                .error(R.drawable.blank_profile)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ImageAcitvity.class);
                intent.putExtra("username", userModel.getUsername());
                intent.putExtra("imageUrl", userModel.getImageUrl());
                ctx.startActivity(intent);
            }
        });
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ChatActivity.class);
                intent.putExtra("username", userModel.getUsername());
                intent.putExtra("email", userModel.getEmail());
                intent.putExtra("UId", userModel.getUId());
                intent.putExtra("imageUrl", userModel.getImageUrl());
                intent.putExtra("fcmToken", userModel.getFcmToken());
                preferences = ctx.getSharedPreferences("com.indianapp.chatapp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user", userModel.getUId());
                editor.commit();
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activeUsersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView lastMessage;
        TextView numOfUnread;
        TextView time;

        CircleImageView image;
        ConstraintLayout cl;
        ConstraintLayout circle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.timeStamp);
            numOfUnread = itemView.findViewById(R.id.numOfUnread);
            cl = itemView.findViewById(R.id.cl);
            username = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_mesg);
            image = itemView.findViewById(R.id.circleImageView);
            circle = itemView.findViewById(R.id.circle);
        }
    }
}
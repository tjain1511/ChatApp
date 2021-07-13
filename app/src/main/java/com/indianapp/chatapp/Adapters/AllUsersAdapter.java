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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.ViewHolder> {

    private ArrayList<UserModel> users;
    private Context ctx;

    private LayoutInflater inflater;

    private SharedPreferences preferences;

    public AllUsersAdapter(Context ctx, ArrayList<UserModel> users) {
        this.ctx = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.users = users;
    }

    @NonNull
    @Override
    public AllUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_user_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsersAdapter.ViewHolder holder, int position) {
        UserModel user = users.get(position);
        holder.username.setText(user.getUsername());
        Glide.with(ctx)
                .load(user.getImageUrl())
                .error(R.drawable.blank_profile)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ImageAcitvity.class);
                intent.putExtra("username", user.getUsername());
                intent.putExtra("imageUrl", user.getImageUrl());
                ctx.startActivity(intent);
            }
        });
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ChatActivity.class);
                intent.putExtra("UId", user.getUId());
                intent.putExtra("imageUrl", user.getImageUrl());
                intent.putExtra("username", user.getUsername());
                preferences = ctx.getSharedPreferences("com.indianapp.chatapp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user", user.getUId());
                editor.commit();
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        CircleImageView imageView;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name_all);
            imageView = itemView.findViewById(R.id.circleImageViewAll);
            constraintLayout = itemView.findViewById(R.id.cl_all);
        }
    }
}

package com.indianapp.chatapp.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.indianapp.chatapp.R;

public class ImageAcitvity extends AppCompatActivity {
    private ImageView img;
    private TextView textView;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_acitvity);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));
        db = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        img = findViewById(R.id.imageView5);
        textView = findViewById(R.id.username_img_act);
        textView.setText(getIntent().getExtras().getString("username"));
        String imageUrl = getIntent().getExtras().getString("imageUrl");
        Log.i("imageUrl", imageUrl);
        Glide.with(this)
                .load(imageUrl)
                .error(R.drawable.blank_profile)
                .into(img);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
        finish();
    }
    @Override
    protected void onPause() {
        db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("offline");
        Log.i("pass","paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("online");
        super.onResume();
    }
}
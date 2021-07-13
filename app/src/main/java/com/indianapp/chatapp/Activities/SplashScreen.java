package com.indianapp.chatapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        Intent intent = new Intent(this, com.indianapp.chatapp.Activities.MainActivity.class);
        startActivity(intent);
        finish();
    }
}

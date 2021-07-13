package com.indianapp.chatapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.indianapp.chatapp.R;

public class SignInActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;

    private FirebaseAuth mAuth;
    private FirebaseMessaging fcm;
    private FirebaseDatabase db;

    private String fcmToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        fcm = FirebaseMessaging.getInstance();
        fcm.getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        fcmToken = task.getResult();
                    }
                });

    }

    public void signIn(View view) {
        Toast.makeText(SignInActivity.this, "Logging In...", Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            db.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("fcmToken").setValue(fcmToken);
                            Intent intent = new Intent(SignInActivity.this, BottomActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        } else {
                            Toast.makeText(SignInActivity.this, "Some error occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
package com.indianapp.chatapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.indianapp.chatapp.R;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText prevPass;
    private EditText newPass;
    private EditText verifyPass;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        db = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        prevPass = findViewById(R.id.prevPass);
        newPass = findViewById(R.id.newPass);
        verifyPass = findViewById(R.id.verifyPass);


    }

    public void reset(View view) {
        if (newPass.getText().toString().equals(verifyPass.getText().toString())) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), prevPass.getText().toString());
            currentUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    currentUser.updatePassword(newPass.getText().toString());
                    Toast.makeText(ResetPasswordActivity.this, "Password reset Successfully", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("password", "password not same");
                }
            });
        } else {
            Log.i("password", "password not same");
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
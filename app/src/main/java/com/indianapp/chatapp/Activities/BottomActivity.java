package com.indianapp.chatapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.indianapp.chatapp.Fragments.ActiveUsersFrag;
import com.indianapp.chatapp.Fragments.DiscoverFrag;
import com.indianapp.chatapp.Fragments.ProfileFrag;
import com.indianapp.chatapp.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class BottomActivity extends AppCompatActivity {
    private static ChipNavigationBar chipNavigationBar;

    public static Fragment fragment = null;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference ref0;
    private DatabaseReference ref1;
    private ChildEventListener childEventListener0;
    private ChildEventListener childEventListener1;

    public static void showBadge(int n) {
        if (n <= 0) {
            chipNavigationBar.dismissBadge(R.id.chats);
        } else {
            chipNavigationBar.showBadge(R.id.chats, n);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);

        getWindow().setStatusBarColor(Color.parseColor("#5d137d"));
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("online");
        db.getReference().child("users").child(currentUser.getUid()).child("lastSeen").setValue(ServerValue.TIMESTAMP);

        childEventListener0 = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String receiverId = snapshot.getKey().toString();
                ref1 = db.getReference().child("chatRoom").child(receiverId + currentUser.getUid());
                childEventListener1 = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String messageStatus = snapshot.child("messageStatus").getValue().toString();
                        if (messageStatus.equals("sent")) {
                            db.getReference().child("chatRoom").child(receiverId + currentUser.getUid()).child(snapshot.getKey()).child("messageStatus")
                                    .setValue("delivered");
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                ref1.addChildEventListener(childEventListener1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref0 = db.getReference().child("users").child(currentUser.getUid()).child("activeUsers");
        ref0.addChildEventListener(childEventListener0);
        chipNavigationBar = findViewById(R.id.chipNavigation);
        chipNavigationBar.setItemSelected(R.id.chats, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, new ActiveUsersFrag()).commit();

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.chats:
                        fragment = new ActiveUsersFrag();
                        break;
                    case R.id.discover:
                        fragment = new DiscoverFrag();
                        break;
                    case R.id.profile:
                        fragment = new ProfileFrag();
                        break;
                }

                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragment).commit();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (ActiveUsersFrag.query != null)
            ActiveUsersFrag.query.removeEventListener(ActiveUsersFrag.childEventListener);
        if (ref1 != null)
            ref1.removeEventListener(childEventListener1);
        if (ref0 != null)
            ref0.removeEventListener(childEventListener0);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("offline");
        Log.i("onPause", "bott");
        super.onPause();
    }

    @Override
    protected void onResume() {
        db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("online");
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if (fragment == null) {
            db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("offline");
            finishAffinity();
        } else {
            db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("online");
            Intent intent = new Intent(getApplicationContext(), BottomActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }
}
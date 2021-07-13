package com.indianapp.chatapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.indianapp.chatapp.Adapters.AllUsersAdapter;
import com.indianapp.chatapp.Models.UserModel;
import com.indianapp.chatapp.R;

import java.util.ArrayList;
import java.util.HashSet;

public class DiscoverFrag extends Fragment {

    private ArrayList<UserModel> users = new ArrayList<>();
    private HashSet set = new HashSet();

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;

    private RecyclerView recyclerView;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_all_user, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        users.clear();
        set.clear();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();

        recyclerView = rootView.findViewById(R.id.rvAll);

        AllUsersAdapter adapter = new AllUsersAdapter(getActivity(), users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        db.getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!set.contains(snapshot.getKey())) {
                    if (!snapshot.child("UId").getValue().toString().equals(currentUser.getUid())) {
                        UserModel user = new UserModel(snapshot.child("UId").getValue().toString(),
                                snapshot.child("email").getValue().toString(),
                                snapshot.child("imageUrl").getValue().toString(),
                                snapshot.child("username").getValue().toString(), snapshot.child("fcmToken").getValue().toString());
                        users.add(user);
                        adapter.notifyDataSetChanged();
                        set.add(snapshot.getKey());
                    }
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
        });
    }
}

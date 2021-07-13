package com.indianapp.chatapp.Fragments;

import android.os.Bundle;
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
import com.indianapp.chatapp.Activities.BottomActivity;
import com.indianapp.chatapp.Adapters.ActiveUserAdapter;
import com.indianapp.chatapp.Models.UserModel;
import com.indianapp.chatapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ActiveUsersFrag extends Fragment {
    private ArrayList<UserModel> activeUsersList = new ArrayList<>();
    private HashSet set = new HashSet();

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private FirebaseUser currentUser;

    private int n = 0;

    private RecyclerView recyclerView;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_users, container, false);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        BottomActivity.fragment =null;
        activeUsersList.clear();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        recyclerView = rootView.findViewById(R.id.activeUserRecycler);
        ActiveUserAdapter adapter = new ActiveUserAdapter(getActivity(), activeUsersList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        db.getReference().child("users").child(currentUser.getUid()).child("activeUsers").orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Collections.reverse(activeUsersList);
                if (snapshot.child("username").getValue() != null) {
                    UserModel userModel = new UserModel(snapshot.getKey(),
                            snapshot.child("imageUrl").getValue().toString(),
                            snapshot.child("username").getValue().toString(),
                            snapshot.child("latestMessage").getValue().toString(),
                            Integer.parseInt(snapshot.child("numberOfUnreadMsg").getValue().toString()),
                            snapshot.child("timestamp").getValue().toString());
                    n += Integer.parseInt(snapshot.child("numberOfUnreadMsg").getValue().toString());
                    BottomActivity.showBadge(n);
                    activeUsersList.add(userModel);
                    set.add(snapshot.getKey());
                    Collections.reverse(activeUsersList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("username").getValue() != null) {
                    Collections.reverse(activeUsersList);
                    UserModel userModel = new UserModel(snapshot.getKey(),
                            snapshot.child("imageUrl").getValue().toString(),
                            snapshot.child("username").getValue().toString(),
                            snapshot.child("latestMessage").getValue().toString(),
                            Integer.parseInt(snapshot.child("numberOfUnreadMsg").getValue().toString()),
                            snapshot.child("timestamp").getValue().toString());

                    if (!set.contains(snapshot.getKey())) {
                        activeUsersList.add(userModel);
                        n += Integer.parseInt(snapshot.child("numberOfUnreadMsg").getValue().toString());
                        BottomActivity.showBadge(n);
                    } else {
                        for (int i = 0; i < activeUsersList.size(); i++) {
                            if (activeUsersList.get(i).getUId().equals(snapshot.getKey())) {
                                n -= activeUsersList.get(i).getNumberOfUnreadMsg();
                                n += Integer.parseInt(snapshot.child("numberOfUnreadMsg").getValue().toString());
                                BottomActivity.showBadge(n);
                                activeUsersList.remove(i);
                                break;
                            }
                        }
                        activeUsersList.add(userModel);
                    }

                    Collections.reverse(activeUsersList);
                    adapter.notifyDataSetChanged();

                }
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

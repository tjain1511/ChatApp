package com.indianapp.chatapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.indianapp.chatapp.Adapters.ChatAdapter;
import com.indianapp.chatapp.Interface.FcmInterface;
import com.indianapp.chatapp.Models.Messages;
import com.indianapp.chatapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private TextView statusText;
    private TextView username;
    private CircleImageView imageView;

    private EditText msg;
    private String receiverId;
    private String roomIdSender;
    private String roomIdReceiver;
    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private HashMap map = new HashMap();
    private ArrayList<Messages> messagesArrayList = new ArrayList<>();
    private HashMap<String, String> mapwa = new HashMap();
    private String receiverToken;
    private SharedPreferences preferences;
    private String receiverName;
    private String imageUrl;
    private RecyclerView recyclerView;
    private String receiverUrl;
    private String senderUrl;
    private Integer numberOfUnreadMsg;
    private String status;
    private String lastSeen;
    private long delay = 1000;
    private long last_text_edit = 0;
    private Handler handler = new Handler();
    private DatabaseReference reference;
    private DatabaseReference reference0;
    private Query query;
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;
    private ChildEventListener childEventListener1;


    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                db.getReference().child("users").child(currentUser.getUid()).child("activeUsers").child(receiverId).child("isTyping").setValue("no");
            }
        }
    };

    private void setStatus(String status, String typing) {
        if (status.equals("offline")) {
            statusText.setVisibility(View.GONE);
        } else if (status.equals("online")) {
            statusText.setVisibility(View.VISIBLE);
            if (typing.equals("yes")) {
                statusText.setText("typing...");
            } else if (typing.equals("no")) {
                statusText.setText("Online");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setStatusBarColor(Color.parseColor("#5d137d"));
        statusText = findViewById(R.id.status);
        username = findViewById(R.id.txt);
        imageView = findViewById(R.id.imgC);
        username.setText(getIntent().getExtras().getString("username"));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                intent.putExtra("imageUrl", getIntent().getExtras().getString("imageUrl"));
                intent.putExtra("username", getIntent().getExtras().getString("username"));
                startActivity(intent);
            }
        });
        Glide.with(this)
                .load(getIntent().getExtras().getString("imageUrl"))
                .error(R.drawable.blank_profile)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);

        msg = findViewById(R.id.msg);
        recyclerView = findViewById(R.id.rvChat);
        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        senderUrl = currentUser.getPhotoUrl().toString();
        receiverId = getIntent().getExtras().getString("UId");
        roomIdSender = currentUser.getUid() + receiverId;
        roomIdReceiver = receiverId + currentUser.getUid();
        receiverUrl = getIntent().getExtras().getString("imageUrl");
        ChatAdapter adapter = new ChatAdapter(this, messagesArrayList, senderUrl, receiverUrl);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        numberOfUnreadMsg = 0;
        preferences = this.getSharedPreferences("com.indianapp.chatapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", receiverId);
        editor.commit();
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                    db.getReference().child("users").child(currentUser.getUid()).child("activeUsers").child(receiverId).child("isTyping").setValue("yes");
                }
            }
        });
        reference0 = db.getReference().child("users").child(receiverId);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiverToken = snapshot.child("fcmToken").getValue().toString();
                receiverName = snapshot.child("username").getValue().toString();
                imageUrl = snapshot.child("imageUrl").getValue().toString();
                if (snapshot.child("activeUsers").child(currentUser.getUid()).child("numberOfUnreadMsg").getValue() != null)
                    numberOfUnreadMsg = Integer.valueOf(snapshot.child("activeUsers").child(currentUser.getUid()).child("numberOfUnreadMsg").getValue().toString());
                status = snapshot.child("status").getValue().toString();
                Object object = snapshot.child("activeUsers").child(currentUser.getUid()).child("isTyping").getValue();
                if (object != null) {
                    setStatus(status, snapshot.child("activeUsers").child(currentUser.getUid()).child("isTyping").getValue().toString());
                }
                lastSeen = snapshot.child("lastSeen").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        reference0.addValueEventListener(valueEventListener);
        query = db.getReference().child("chatRoom").child(roomIdSender).orderByChild("timeStamp");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages message = new Messages(snapshot.getKey(),
                        snapshot.child("message").getValue().toString(),
                        snapshot.child("receiverId").getValue().toString(),
                        snapshot.child("senderId").getValue().toString(),
                        snapshot.child("senderName").getValue().toString(),
                        snapshot.child("timeStamp").getValue().toString(),
                        snapshot.child("messageStatus").getValue().toString());
                if (preferences.getString("user", "null").equals(receiverId)) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("activeUsers").child(receiverId).child("numberOfUnreadMsg")
                            .setValue(0);
                }
                messagesArrayList.add(message);
                adapter.notifyItemChanged(messagesArrayList.size() - 1);
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < messagesArrayList.size(); i++) {
                    if (snapshot.getKey().equals(messagesArrayList.get(i).getMessageId())) {
                        Messages message = new Messages(snapshot.getKey(),
                                snapshot.child("message").getValue().toString(),
                                snapshot.child("receiverId").getValue().toString(),
                                snapshot.child("senderId").getValue().toString(),
                                snapshot.child("senderName").getValue().toString(),
                                snapshot.child("timeStamp").getValue().toString(),
                                snapshot.child("messageStatus").getValue().toString());
                        messagesArrayList.set(i, message);
                        adapter.notifyDataSetChanged();
                    }
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
        };
        query.addChildEventListener(childEventListener);

        reference = db.getReference().child("chatRoom").child(roomIdReceiver);
        childEventListener1 = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String messageStatus = snapshot.child("messageStatus").getValue().toString();
                if (preferences.getString("user", "null").equals(receiverId)) {
                    db.getReference().child("chatRoom").child(receiverId + currentUser.getUid()).child(snapshot.getKey()).child("messageStatus")
                            .setValue("read");
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
        reference.addChildEventListener(childEventListener1);
    }

    public void send(View view) {

        String uniqueMessageId = UUID.randomUUID().toString();
        String message = msg.getText().toString().trim();
        if (!message.isEmpty()) {
            map.put("message", message);
            map.put("senderId", currentUser.getUid());
            map.put("senderName", currentUser.getDisplayName());
            map.put("receiverId", receiverId);
            map.put("timeStamp", ServerValue.TIMESTAMP);
            if (status.equals("online")) {
                map.put("messageStatus", "delivered");
            } else {
                map.put("messageStatus", "sent");
            }
            HashMap<String, Object> ss = new HashMap();
            HashMap<String, Object> rs = new HashMap();
            ss.put("latestMessage", message);
            ss.put("timestamp", ServerValue.TIMESTAMP);
            ss.put("username", receiverName);
            ss.put("imageUrl", imageUrl);
            ss.put("numberOfUnreadMsg", 0);
            ss.put("isTyping", "no");
            rs.put("latestMessage", message);
            rs.put("timestamp", ServerValue.TIMESTAMP);
            rs.put("username", currentUser.getDisplayName());
            rs.put("imageUrl", senderUrl);
            rs.put("isTyping", "no");
            numberOfUnreadMsg = numberOfUnreadMsg + 1;
            rs.put("numberOfUnreadMsg", numberOfUnreadMsg);
            FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("activeUsers").child(receiverId).setValue(ss);
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverId).child("activeUsers").child(currentUser.getUid()).setValue(rs);
            db.getReference().child("chatRoom").child(roomIdReceiver).child(uniqueMessageId).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db.getReference().child("chatRoom").child(roomIdSender).child(uniqueMessageId).setValue(map);
                    Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://serverfcm-eyczj.run-ap-south1.goorm.io").addConverterFactory(GsonConverterFactory.create());
                    Retrofit retrofit = builder.build();
                    final FcmInterface client = retrofit.create(FcmInterface.class);
                    mapwa.put("title", "Message from " + currentUser.getDisplayName());
                    mapwa.put("body", String.valueOf(map.get("message")));
                    mapwa.put("fcmToken", receiverToken);
                    mapwa.put("senderID", currentUser.getUid());
                    mapwa.put("username", currentUser.getDisplayName());
                    mapwa.put("email", currentUser.getEmail());
                    mapwa.put("imageUrl", currentUser.getPhotoUrl().toString());
                    Call<ResponseBody> call = client.sendFcm(mapwa);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            });
            msg.setText("");
            msg.setHint("Type a Message");
        } else {
            Toast.makeText(this, "Message can't be empty", Toast.LENGTH_SHORT).show();
        }


    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        preferences = this.getSharedPreferences("com.indianapp.chatapp", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", "null");
        editor.commit();
        db.getReference().child("users").child(currentUser.getUid()).child("activeUsers").child(receiverId).child("isTyping").setValue("no");
        Intent intent = new Intent(this, BottomActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onDestroy() {
        reference.removeEventListener(childEventListener1);
        query.removeEventListener(childEventListener);
        reference0.removeEventListener(valueEventListener);
        db.getReference().child("users").child(currentUser.getUid()).child("activeUsers").child(receiverId).child("isTyping").setValue("no");
        preferences = this.getSharedPreferences("com.indianapp.chatapp", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", "null");
        editor.commit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        db.getReference().child("users").child(currentUser.getUid()).child("activeUsers").child(receiverId).child("isTyping").setValue("no");
        db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("offline");
        super.onPause();
    }

    @Override
    protected void onResume() {
        db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("online");
        super.onResume();
    }

}
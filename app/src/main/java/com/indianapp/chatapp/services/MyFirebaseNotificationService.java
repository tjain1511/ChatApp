package com.indianapp.chatapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.indianapp.chatapp.Activities.ChatActivity;
import com.indianapp.chatapp.R;

public class MyFirebaseNotificationService extends FirebaseMessagingService {
    private SharedPreferences preferences;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public boolean handleIntentOnMainThread(@NonNull Intent intent) {
        return super.handleIntentOnMainThread(intent);


    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        preferences = this.getSharedPreferences("com.indianapp.chatapp", Context.MODE_PRIVATE);
        if (!preferences.getString("user", "null").equals(remoteMessage.getData().get("senderId").toString())) {
            addNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),
                    remoteMessage.getData().get("username").toString(),
                    remoteMessage.getData().get("email").toString(),
                    remoteMessage.getData().get("UId").toString(),
                    remoteMessage.getData().get("imageUrl").toString(),
                    remoteMessage.getData().get("fcmToken").toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotification(String title, String body, String username, String email, String UId, String imageUrl, String fcmToken) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("UId", UId);
        intent.putExtra("imageUrl", imageUrl);
        intent.putExtra("fcmToken", fcmToken);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
            NotificationChannel channel = new NotificationChannel("channel1",
                    "hello",
                    NotificationManager.IMPORTANCE_HIGH);

            manager.createNotificationChannel(channel);
        }

        //Creating the notification object
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "channel1");
        //notification.setAutoCancel(true);
        notification.setContentTitle(title);
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setContentText(body).setAutoCancel(true).setContentIntent(pendingIntent);

        //make the notification manager to issue a notification on the notification's channel
        manager.notify(121, notification.build());
    }
}

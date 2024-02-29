package com.dam.lic;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dam.lic.ui.main.SharedPrefs;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "CANAL_NOTIF";
    SharedPrefs sharedPreferences;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        sharedPreferences=new SharedPrefs(this);
        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();



        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Channel for Notification",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.ic_baseline_email_24)
                        .setAutoCancel(true);

        if(sharedPreferences.getNotifications()==true) {
            NotificationManagerCompat.from(this).notify(1, notification.build());
        }
    }
}



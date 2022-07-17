package com.example.emptyapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class AudioService extends Service {

    public static final String CHANNEL_ID = "ExampleSerivceChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotiChannel();
        Intent notiIntent = new Intent(this, Nitter.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notiIntent, 0);

        String input = intent.getStringExtra("inputExtra");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Audio Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_globe)
                .build();
        startForeground(1, notification);

        return START_STICKY;
    }

    private void createNotiChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "ExampleSerivceChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
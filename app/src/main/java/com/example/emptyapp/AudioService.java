package com.example.emptyapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class AudioService extends Service {

    String CHANNEL_ID = "ExampleService";
    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Intent notiIntent = new Intent(this, Nitter.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notiIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ExampleService")
                .setContentText("Test")
                .setSmallIcon(R.drawable.ic_baseline_android_24)
                .setContentIntent(pendingIntent)
                .build();
        //startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
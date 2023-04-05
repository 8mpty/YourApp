package com.empty.yourapp.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.empty.yourapp.WebActivity;
import com.example.emptyapp.R;

public class AudioService extends Service {

    public static final String CHANNEL_ID = "ExampleSerivceChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotiChannel();
        String message = "TEST";
        Intent notiIntent = new Intent(this, WebActivity.class);
//        notiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notiIntent, PendingIntent.FLAG_MUTABLE  |
                        PendingIntent.FLAG_NO_CREATE);



        Intent testIntent = new Intent(this, AudioService.class);
        testIntent.putExtra("toastMessage", message);
        PendingIntent playIntent = null;

        String webname = intent.getStringExtra("inputWebName");
        String webLink = intent.getStringExtra("inputWebLink");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(webname)
                .setContentText(webLink)
                .setSmallIcon(R.drawable.icon_audio)
                .setContentIntent(pendingIntent)
                //.addAction(R.mipmap.ic_launcher, "Play", null)
                //.addAction(R.mipmap.ic_launcher, "Pause", null)
                .build();
        startForeground(1, notification);

        return START_NOT_STICKY;
    }


    private void createNotiChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "ExampleSerivceChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
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
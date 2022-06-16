package com.example.emptyapp.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.emptyapp.Nitter;
import com.example.emptyapp.R;

public class AudioService extends Service
{

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        createNotiChan();

        Intent intent1 = new Intent(this, Nitter.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1,0);

        Notification notification = new NotificationCompat.Builder(this,"Channel1")
                .setContentText("App Running")
                .setContentText("Foreground Service")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);
        return START_STICKY;
    }

    private void createNotiChan()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "Channel1","Foreground Serivce", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }
}

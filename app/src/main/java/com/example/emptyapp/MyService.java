package com.example.emptyapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyService extends Service {
    private MediaPlayer mediaPlayer;
    Uri uri = Uri.parse(Nitter.webView.getUrl());
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void onCreate(){
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.setLooping(false);
    }
     public void onStart(Intent intent, int startid){
         Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
         mediaPlayer.start();
     }
    public void onDestroy(){
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        mediaPlayer.stop();
    }
}

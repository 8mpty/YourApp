package com.example.emptyapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MyService extends Service {
    private MediaPlayer mediaPlayer;
    Uri uri;
    String url;
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void onCreate(){
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
        mediaPlayer = new MediaPlayer();

        url = WebActivity.webView.getUrl();

        uri = Uri.parse(url);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
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

package com.empty.yourapp;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.emptyapp.R;

public class NewWebView extends WebView {
    SharedPreferences sp;
    Context context;

    private static final String PREF_SERVICE = "pref_Service";
    public static String SHARED_PREF_STR = "shared_pref_str";

    public NewWebView(@NonNull Context context) {
        super(context);

//        sp = getContext().getSharedPreferences(SHARED_PREF_STR, Context.MODE_PRIVATE);

        this.context = context;
    }

    public NewWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NewWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        sp = getContext().getSharedPreferences(SHARED_PREF_STR, Context.MODE_PRIVATE);
        if (sp.getBoolean(PREF_SERVICE, false)) {
            NotificationManager mNotifyMgr = (NotificationManager) this.context.getSystemService(NOTIFICATION_SERVICE);
            if (visibility == View.GONE) {

                Intent intentP = new Intent(getContext(), WebActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this.context, 0, intentP, PendingIntent.FLAG_NO_CREATE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String name = "Audio background";
                    String description = "Play audio on background -> click to open";
                    int importance = NotificationManager.IMPORTANCE_LOW; //Important for heads-up notification
                    NotificationChannel channel = new NotificationChannel("2", name, importance);
                    channel.setDescription(description);
                    channel.setShowBadge(true);
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    NotificationManager notificationManager = this.context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, "2")
                        .setSmallIcon(R.drawable.icon_audio)
                        .setAutoCancel(true)
                        .setContentTitle(this.getTitle())
//                        .setContentText(this.context.getString(R.string.setting_title_audioBackground))
                        .setContentIntent(pendingIntent); //Set the intent that will fire when the user taps the notification
                Notification buildNotification = mBuilder.build();
                mNotifyMgr.notify(2, buildNotification);
                Log.e("OUT", "ONE");
            }
            else {
                mNotifyMgr.cancel(2);
                Log.e("NOT", "TWO");
            }
            super.onWindowVisibilityChanged(View.VISIBLE);
        }
        else if(!sp.getBoolean(PREF_SERVICE, false)){
            Log.e("NOT", "ONE");
        }
        else {
            super.onWindowVisibilityChanged(visibility);
            Log.e("NOT", "ALL");
        }
//        super.onWindowVisibilityChanged(visibility);
    }
}

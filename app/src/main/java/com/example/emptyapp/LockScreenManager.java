package com.example.emptyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class LockScreenManager extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private static final String PREF_LOCKSW = "pref_LOCKSW";
    public static final String PREF_DEV = "pref_DEV";
    public static final String PREF_IPTOG = "pref_IpTog";

    public static final String PREF_DEF_URL_ACT = "pref_def_ACT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();


        // LOCKSCREEN SHOULD ALWAYS COMES FIRST IF ENABLED


        // If lockscreen is true,
        if(pref.getBoolean(PREF_LOCKSW, false)){
//            //Check if IP_TOG is true,
//            if(pref.getBoolean(PREF_DEV, false) &&
//                    pref.getBoolean(PREF_IPTOG, false)){
//                // Go to VerifyIP
//                startActivity(new Intent(this, VerifyIP.class));
//                Log.e("LCKMANAGER","THIS1");
//
//            }
//            else{
//                // Else just just to to LockScreen
//            }
            startActivity(new Intent(this, MainActivity.class));

        }
        // If lockscreen is false,
        else{
            if(pref.getBoolean(PREF_DEF_URL_ACT, false)){
                startActivity(new Intent(this, Nitter.class));
            }
            else{
                if(pref.getBoolean(PREF_DEV, false) &&
                        pref.getBoolean(PREF_IPTOG, false)){
                    startActivity(new Intent(this, VerifyIP.class));
                    Log.e("LCKMANAGER","THIS2");
                }
                else{
                    startActivity(new Intent(this, WebLinksActivity.class));
                    Log.e("WEBLINKS CALLED IN LCKMANAGER","");
                }
            }
        }

        finish();
    }
}
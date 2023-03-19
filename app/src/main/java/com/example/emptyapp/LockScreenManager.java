package com.example.emptyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class LockScreenManager extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private static final String PREF_LOCKSW = "pref_LOCKSW";
    public static final String PREF_DEF_URL_ACT = "pref_def_url_act";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();

        if(pref.getBoolean(PREF_LOCKSW, false)){
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            if(pref.getBoolean(PREF_DEF_URL_ACT, false)){
                startActivity(new Intent(this, Nitter.class));
            }
            else{
                startActivity(new Intent(this, WebLinksActivity.class));
            }
        }

        finish();
    }
}
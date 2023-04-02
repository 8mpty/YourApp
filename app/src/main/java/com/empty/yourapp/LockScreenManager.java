package com.empty.yourapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class LockScreenManager extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private static final String PREF_LOCKSW = "pref_LOCKSW";
    public static final String PREF_DEV = "pref_DEV";
    public static final String PREF_IPTOG = "pref_IpTog";
    public static final String PREF_VPN = "pref_VpnTog";
    public static final String PREF_DEF_URL_ACT = "pref_def_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();

        Checks();
    }

    private void Checks(){
        boolean lockBool = pref.getBoolean(PREF_LOCKSW, false);
        boolean actBool = pref.getBoolean(PREF_DEF_URL_ACT, false);
        boolean devBool = pref.getBoolean(PREF_DEV, false);
        boolean ipBool = pref.getBoolean(PREF_IPTOG, false);
        boolean vpnBool = pref.getBoolean(PREF_VPN, false);

        // LOCKSCREEN SHOULD ALWAYS COMES FIRST IF ENABLED
        if(lockBool) {
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            if(actBool) {
                if((devBool && ipBool) || (devBool && vpnBool)){
                    startActivity(new Intent(this, ConnectionCheck.class));}
                else {
                    startActivity(new Intent(this, WebActivity.class));}
            }
            else{
                if((devBool && ipBool) || (devBool && vpnBool)) {
                    startActivity(new Intent(this, ConnectionCheck.class));}
                else {
                    startActivity(new Intent(this, WebLinksActivity.class));}
            }
        }
        finish();
    }
}
package com.example.emptyapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class VerifyIP extends AppCompatActivity{

    private TextView iv;
    private Button btn;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static final String PREF_IPSAVE = "pref_Ipa";
    public static final String PREF_DEF_URL_ACT = "pref_def_ACT";

    private boolean boola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_ip);

        iv = findViewById(R.id.tv);
        btn = findViewById(R.id.exbtn);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String IP_ADDR = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();


        if (IP_ADDR.equals(pref.getString(PREF_IPSAVE,null))){
            if(pref.getBoolean(PREF_DEF_URL_ACT, false)){
                startActivity(new Intent(this, Nitter.class));
            }else {
                startActivity(new Intent(this, WebLinksActivity.class));
            }
        }
        else{
            Log.e("Not IP", "NOT THE IP");
            dia(IP_ADDR);
            iv.setText(IP_ADDR);
//            Log.e("IPSAVED is ", pref.getString(PREF_IPSAVE, null));
        }

        iv.setText(IP_ADDR);
//        Log.e("PREF_IPSAVE",pref.getString(PREF_IPSAVE,null));
    }

    private void dia(String ip){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("IP: " + ip)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        finishAndRemoveTask();
                        finish();
                        System.exit(0);
                    }
                });
        builder.create().show();
    }


    public void changeBool(View v){
        String boolval = String.valueOf(boola);
        boola = !boola;
        startActivity(new Intent(this, SettingsActivity.class));
        Log.e("BOOL",boolval);
    }
}
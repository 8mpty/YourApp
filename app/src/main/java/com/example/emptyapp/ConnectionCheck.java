package com.example.emptyapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import timber.log.Timber;

public class ConnectionCheck extends AppCompatActivity{

    private TextView iv;
    private Button btn;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static final String PREF_IPSAVE = "pref_Ipa";
    public static final String PREF_DEF_URL_ACT = "pref_def_ACT";
    public static final String PREF_VPN = "pref_VpnTog";
    public static final String PREF_IP = "pref_IpTog";

    private boolean boola;
    private boolean ipstate;
    private boolean vpnstate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_ip);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        iv = findViewById(R.id.tv);
        btn = findViewById(R.id.exbtn);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
        
        Check();
    }

    private void Check(){
        if(pref.getBoolean(PREF_IP, false)){
            IPCheck();
        }

        if(pref.getBoolean(PREF_VPN,false)){
            VPNCheck();
        }

        if(pref.getBoolean(PREF_IP, false) &&
                !pref.getBoolean(PREF_VPN,false)){
            if(ipstate){
                startActivity(new Intent(this, WebLinksActivity.class));
            }
        }
        else if(!pref.getBoolean(PREF_IP, false) &&
                pref.getBoolean(PREF_VPN,false)){
            if(vpnstate){
                startActivity(new Intent(this, WebLinksActivity.class));
            }
        }
        else if(pref.getBoolean(PREF_IP, false) &&
                pref.getBoolean(PREF_VPN,false)){
            if(ipstate && vpnstate){
                startActivity(new Intent(this, WebLinksActivity.class));
            }
        }
    }

    private void IPCheck(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String IP_ADDR = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        if (IP_ADDR.equals(pref.getString(PREF_IPSAVE,null))){
            if(pref.getBoolean(PREF_DEF_URL_ACT, false)){
                startActivity(new Intent(this, WebActivity.class));
            }else {
                ipstate = true;
            }
        }
        else{
            Timber.tag("IP").e("NOT THE IP");
            dia(IP_ADDR);
            ipstate = false;
        }
        iv.setText(IP_ADDR);
    }

    @SuppressLint("SetTextI18n")
    private void VPNCheck() {

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);

        if(vpnInUse){
            Timber.tag("VPN IS").e("USED");
            vpnstate = true;
        }
        else{
            Timber.tag("VPN IS").e("NOT USED");
            iv.setText("VPN IS NOT IN USE");
            vpnstate = false;
        }
    }

    private void dia(String ip){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("IP: " + ip)
                .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
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
        Timber.tag("BOOL").e(boolval);
    }
}
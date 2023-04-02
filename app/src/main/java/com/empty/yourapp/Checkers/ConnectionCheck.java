package com.empty.yourapp.Checkers;

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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.empty.yourapp.Settings.SettingsActivity;
import com.empty.yourapp.WebActivity;
import com.empty.yourapp.WebLinksActivity;
import com.example.emptyapp.BuildConfig;
import com.example.emptyapp.R;

import timber.log.Timber;

public class ConnectionCheck extends AppCompatActivity{

    private TextView iv;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static final String PREF_DEV = "pref_DEV";
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

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

        Check();
    }

    private void Check(){
        boolean ipBool = pref.getBoolean(PREF_IP, false);
        boolean vpnBool = pref.getBoolean(PREF_VPN, false);

        if(ipBool){
            IPCheck();
        }

        if(vpnBool){
            VPNCheck();
        }

        if(ipBool && !vpnBool){
            if(ipstate){
                startActivity(new Intent(this, WebLinksActivity.class));
            }
        }
        else if(!ipBool && vpnBool){
            if(vpnstate){
                startActivity(new Intent(this, WebLinksActivity.class));
            }
        }
        else if(ipBool && vpnBool){
            if(ipstate && vpnstate){
                startActivity(new Intent(this, WebLinksActivity.class));
            }
        }
    }

    private void IPCheck(){
        String ipsave = pref.getString(PREF_IPSAVE,null);
        boolean devBool = pref.getBoolean(PREF_DEV, false);
        boolean defAct = pref.getBoolean(PREF_DEF_URL_ACT, false);


        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String IP_ADDR = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        if (IP_ADDR.equals(ipsave)){
            if(devBool && defAct){
                startActivity(new Intent(this, WebActivity.class));
            }else {
                ipstate = true;
            }
        }
        else{
            Timber.tag("IP ").e("DOES NOT MATCH");
            Dialog(IP_ADDR);
            ipstate = false;
        }
        iv.setText(IP_ADDR);
    }


    @SuppressLint("SetTextI18n")
    private void VPNCheck() {
        boolean devBool = pref.getBoolean(PREF_DEV, false);
        boolean defAct = pref.getBoolean(PREF_DEF_URL_ACT, false);

        // https://stackoverflow.com/questions/28386553/check-if-a-vpn-connection-is-active-in-android
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);

        if(vpnInUse){
            if(devBool && defAct){
                startActivity(new Intent(this, WebActivity.class));
            }
            else {
                Timber.tag("VPN IS").e("USE");
                vpnstate = true;
            }
        }
        else{
            Timber.tag("VPN IS").e("NOT IN USE");
            iv.setText("VPN IS NOT IN USE");
            vpnstate = false;
        }
    }

    private void Dialog(String ip){
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
        boola = !boola; // Testing purposes only
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
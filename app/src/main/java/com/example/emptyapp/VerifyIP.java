package com.example.emptyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.potterhsu.Pinger;

public class VerifyIP extends AppCompatActivity {

    private TextView iv;
    public boolean ping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_ip);

        iv = findViewById(R.id.tv);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        final String IP_ADDRESS = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        if (IP_ADDRESS.equals("192.168.10.194")){
            startActivity(new Intent(this, LockScreenManager.class));

        }
        else{
            Log.e("Not IP", "NOT THE IP");
            finish();
            System.exit(0);
        }


        iv.setText("");
        Pinger pinger = new Pinger();
        pinger.setOnPingListener(new Pinger.OnPingListener() {
            @Override
            public void onPingSuccess() {
                Log.e("PING","SUCC PINGING");
                ping = true;
                startActivity(new Intent(VerifyIP.this, LockScreenManager.class));

            }

            @Override
            public void onPingFailure() {
                Log.e("PING","NOT PINGING");
                finish();
                System.exit(0);
                ping = false;
            }

            @Override
            public void onPingFinish() {
            }
        });
        //pinger.pingUntilSucceeded("10.147.19.88", 1);
    }
    private void dia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("NOT SUCC")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                });
        builder.create().show();
    }
}
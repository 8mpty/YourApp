package com.example.emptyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText txtPass;
    Button btnSub;


    ScrollView sv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getColor(R.color.black));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        txtPass = findViewById(R.id.txtpass);
        btnSub = findViewById(R.id.btnSub);

        txtPass.setInputType(InputType.TYPE_CLASS_NUMBER);

        btnSub.setOnClickListener(v ->
        {
            if(TextUtils.isEmpty(txtPass.getText().toString()))
            {
                txtPass.setError("Please input code!");
            }
            else
            {
                OpenAct();
            }
        });
    }

    public void OpenAct() {

        int num = Integer.parseInt(txtPass.getText().toString());
        Intent nitter = new Intent(this, Nitter.class);

        txtPass.setText("");

        if (num == 1111)
        {
            Nitter.url = "https://www.youtube.com/";
            startActivity(nitter);
            Nitter.isAudio = false;

        }
        else if (num == 2222)
        {
            Nitter.url = "https://beatbump.ml/home/";
            startActivity(nitter);
            Nitter.isAudio = true;
        }
        else if (num == 3333)
        {
            Nitter.url = "https://animixplay.to/";
            startActivity(nitter);
        }
        else if (num == 4444)
        {
            Nitter.url = "https://nitter.net";
            startActivity(nitter);
        }
        else if(num == 5555)
        {
            Nitter.url = "https://gogoanime.gg/";
            startActivity(nitter);
        }
        else if (num == 6969)
        {
            Nitter.url = "https://canyoublockit.com";
            startActivity(nitter);
        }
        else if (num == 6666)
        {
            Nitter.url = "https://kisskh.me/";
            startActivity(nitter);
        }
        else
        {
            txtPass.setError("Invalid Code");
        }
    }
}
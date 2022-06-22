package com.example.emptyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText txtPass;
    Button btnSub;

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
        //closeKeyboard();

        if (num == 1111)
        {
            Nitter.url = "https://www.youtube.com/";
            //Nitter.ua = "Mozilla/5.0 (Linux; Android 7.1.1; SM-T555 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.96 Safari/537.36";
            startActivity(nitter);
            Nitter.isAudio = false;

        }
        else if (num == 2222)
        {
            Nitter.url = "https://music.youtube.com";
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
        else
        {
            txtPass.setError("Invalid Code");
        }
    }

    private void closeKeyboard()
    {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
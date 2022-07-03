package com.example.emptyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    EditText txtPass;
    Button btnSub;


    private Toolbar toolbar;
    private ImageButton btnSecret;

    private long then;
    private int longClickDuration= 2500;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getColor(R.color.black));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        count = 0;

        txtPass = findViewById(R.id.txtpass);
        btnSub = findViewById(R.id.btnSub);
        txtPass.setInputType(InputType.TYPE_CLASS_NUMBER);

        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setVisibility(View.GONE);

        btnSub.setOnClickListener(v ->
        {
            if(TextUtils.isEmpty(txtPass.getText().toString())) {
                txtPass.setError("Please input code!");
            }
            else OpenAct();
        });

        UnHideToolBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        count = 0;
        toolbar.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void UnHideToolBar()
    {
        btnSecret = findViewById(R.id.btnSecret);

//        btnSecret.setOnTouchListener((v, event) -> {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                then = (long) System.currentTimeMillis();
//            }
//            else if (event.getAction() == MotionEvent.ACTION_UP)
//            {
//                if ((long) (System.currentTimeMillis() - then) > longClickDuration) {
//                    if(toolbar.getVisibility() == View.GONE)
//                    {
//                        toolbar.setVisibility(View.VISIBLE);
//                    }
//                }
//                return false;
//            }
//            return true;
//        });

        btnSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if (count >= 5) {
                    toolbar.setVisibility(View.VISIBLE);
                    count = 0;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.main_exit:
            {
                finish();
            }
            break;

            case R.id.btnSet:{
                if(!item.isChecked())
                {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void OpenAct() {

        int num = Integer.parseInt(txtPass.getText().toString());
        Intent nitter = new Intent(this, Nitter.class);

        txtPass.setText("");

        if (num == 1111) {
            Nitter.url = "https://www.youtube.com/";
            startActivity(nitter);
        }
        else if (num == 2222) {
            Nitter.url = "https://music.youtube.com";
            startActivity(nitter);
        }
        else if (num == 3333) {
            Nitter.url = "https://animixplay.to/";
            startActivity(nitter);
        }
        else if (num == 4444) {
            Nitter.url = "https://nitter.net";
            startActivity(nitter);
        }
        else if(num == 5555) {
            Nitter.url = "https://gogoanime.gg/";
            startActivity(nitter);
        }
        else if (num == 6969) {
            Nitter.url = "https://canyoublockit.com";
            startActivity(nitter);
        }
        else if (num == 6666) {
            Nitter.url = "https://www2.theshit.me/";
            startActivity(nitter);
        }
        else txtPass.setError("Invalid Code");
    }
}
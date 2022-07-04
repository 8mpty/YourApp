package com.example.emptyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText txtPass;
    private Toolbar toolbar;

    private int count = 0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final String PREF_SECKEY = "pref_SecKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getColor(R.color.black));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();



        count = 0;

        txtPass = findViewById(R.id.txtpass);
        Button btnSub = findViewById(R.id.btnSub);
        txtPass.setInputType(InputType.TYPE_CLASS_NUMBER);

        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbar.setVisibility(View.GONE);

        btnSub.setOnClickListener(v -> {
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
    private void UnHideToolBar() {
        ImageButton btnSecret = findViewById(R.id.btnSecret);
        btnSecret.setOnClickListener(v -> {
            count++;
            if (count >= 5) {
                toolbar.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"UNLOCKED DEVELOPER SETTINGS", Toast.LENGTH_SHORT).show();
                count = 0;
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
        switch (item.getItemId()) {
            case R.id.main_exit: {
                finish();
            }
            break;

            case R.id.btnSet:{
                if(!item.isChecked()) {
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
        Intent webLinks = new Intent(this, WebLinksActivity.class);

        txtPass.setText("");
        int pass = Integer.parseInt(pref.getString(PREF_SECKEY,""));

        int backup_key = getResources().getInteger(R.integer.backup_key);

        if(num == pass || num == backup_key){
            startActivity(webLinks);
        }
        else txtPass.setError("Invalid Code");
    }
}
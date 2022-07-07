package com.example.emptyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText txtPass;
    private Toolbar toolbar;

    private int count = 0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final String PREF_SECKEY = "pref_SecKey";
    private ArrayList<LinkModal>linkModalArrayList;

    View keyboardLayout;

    SharedPreferences prevText;
    SharedPreferences.Editor prevEditor;

    private static final String PREF_TXT = "shar_txt";
    private static final String TEXT = "pref_txt";

    private String txt;


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
        keyboardLayout = findViewById(R.id.lay_keypad);
        keyboardLayout.setVisibility(View.GONE);
        txtPass.setInputType(InputType.TYPE_CLASS_NUMBER);

        //txtPass.setShowSoftInputOnFocus(true);


        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbar.setVisibility(View.GONE);

        txtPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyboardLayout.getVisibility() == View.GONE){
                    keyboardLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSub.setOnClickListener(v -> {
            if(TextUtils.isEmpty(txtPass.getText().toString())) {
                txtPass.setError("Please input code!");
            }
            else OpenAct();
        });

        UnHideToolBar();
        LoadBtnNumbers();
    }


    private void LoadBtnNumbers(){
        Button[] btn = new Button[] {

                (Button) findViewById(R.id.btn1),
                (Button) findViewById(R.id.btn2),
                (Button) findViewById(R.id.btn3),
                (Button) findViewById(R.id.btn4),
                (Button) findViewById(R.id.btn5),
                (Button) findViewById(R.id.btn6),
                (Button) findViewById(R.id.btn7),
                (Button) findViewById(R.id.btn8),
                (Button) findViewById(R.id.btn9),
        };

        ArrayList<String> array = new ArrayList<>();
        array.add("1");
        array.add("2");
        array.add("3");
        array.add("4");
        array.add("5");
        array.add("6");
        array.add("7");
        array.add("8");
        array.add("9");

        for(int i = 0; i < btn.length; i++) {

            Random rand = new Random();
            String newString = String.valueOf(array.get(rand.nextInt(array.size())));
            btn[i].setText(newString);

            array.remove(newString);
            String text = btn[i].getText().toString();

            int finalI = i;
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("BTN","NUMBER = "+ text);
                    btn[finalI].setOnClickListener(this);
                    txtPass.setText(text);
                    txtPass.setSelection(txtPass.getText().length());
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        count = 0;
        toolbar.setVisibility(View.GONE);
        LoadBtnNumbers();
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
            keyboardLayout.setVisibility(View.GONE);
            startActivity(webLinks);
        }
        else txtPass.setError("Invalid Code");
    }

    @Override
    public void onBackPressed() {
        if(keyboardLayout.getVisibility() == View.VISIBLE){
            keyboardLayout.setVisibility(View.GONE);
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void SaveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_TXT, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PREF_TXT, txtPass.getText().toString());
        editor.apply();
    }
    private void LoadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_TXT, MODE_PRIVATE);

        txt = sharedPreferences.getString(PREF_TXT,"");
    }
    private void UpdateView(){
        txtPass.setText(txt);
    }
}
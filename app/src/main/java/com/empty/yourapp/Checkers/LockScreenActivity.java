package com.empty.yourapp.Checkers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.empty.yourapp.Settings.SettingsActivity;
import com.empty.yourapp.WebActivity;
import com.empty.yourapp.WebLinksActivity;
import com.example.emptyapp.R;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executor;

public class LockScreenActivity extends AppCompatActivity {

    private EditText txtPass;
    private Toolbar toolbar;

    private int count = 0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final String PREF_SECKEY = "pref_SecKey";
    private static final String PREF_RANDKEYS = "pref_RANDKEYS";
    public static final String PREF_DEF_URL_ACT = "pref_def_ACT";
    public static final String PREF_DEV = "pref_DEV";
    public static final String PREF_IPTOG = "pref_IpTog";
    public static final String PREF_VPN = "pref_VpnTog";
    public static final String PREF_BIO = "pref_BIO";
    public static final String PREF_LOCK = "pref_LOCKSW";

    private View keyboardLayout;

    private Button[] btn;
    private ArrayList<String> array = new ArrayList<String>();

    ImageButton btnBio;
    BiometricPrompt bioPrompt;
    BiometricPrompt.PromptInfo bioPromptInfo;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getColor(R.color.darker_purple));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();

        btnBio = findViewById(R.id.btnBio);

        txtPass = findViewById(R.id.txtpass);
        txtPass.setShowSoftInputOnFocus(false);

        keyboardLayout = findViewById(R.id.lay_keypad);
        keyboardLayout.setVisibility(View.GONE);

        toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbar.setVisibility(View.GONE);

        txtPass.setOnTouchListener((v, event) -> {
            if(keyboardLayout.getVisibility() == View.GONE){
                keyboardLayout.setVisibility(View.VISIBLE);
                btnBio.setVisibility(View.GONE);
                btnBio.setEnabled(false);
            }
            return false;
        });

        BtnClick();

        if(pref.getBoolean(PREF_RANDKEYS, false)){
            RandomBtnNumbers();
        }
        else SetNormalBtnNumbers();

        if(pref.getBoolean(PREF_BIO, false)){
            btnBio.setVisibility(View.VISIBLE);
            btnBio.setEnabled(true);
        }
        else{
            btnBio.setVisibility(View.GONE);
            btnBio.setEnabled(false);
        }
    }

    private void BtnClick(){
        Button btnBack = (Button) findViewById(R.id.btnBackSpace);
        btnBack.setOnClickListener(v -> {

            String curTxt = txtPass.getText().toString();
            int input = curTxt.length();
            if(input > 0) {
                txtPass.setText(curTxt.substring(0,input - 1));
                txtPass.setSelection(txtPass.getText().length());
            }
        });

        btnBack.setOnLongClickListener(v -> {
            String curTxt = txtPass.getText().toString();
            int input = curTxt.length();
            if(input > 0) {
                txtPass.setText(curTxt.substring(0,input - 1));
                txtPass.setSelection(txtPass.getText().length());
            }
            return true;
        });

        Button btnSub = findViewById(R.id.btnSub);
        btnSub.setOnClickListener(v -> {
            if(TextUtils.isEmpty(txtPass.getText().toString())) {
                txtPass.setError("Please input code!");
            }
            else OpenAct();
        });

        Button btnEnt = findViewById(R.id.btnEnt);
        btnEnt.setOnClickListener(v -> {
            if(TextUtils.isEmpty(txtPass.getText().toString())) {
                txtPass.setError("Please input code!");
            }
            else OpenAct();

            count++;
            if (count >= 10) {
                toolbar.setVisibility(View.VISIBLE);
                Toast.makeText(LockScreenActivity.this,"UNLOCKED DEVELOPER SETTINGS", Toast.LENGTH_SHORT).show();
                count = 0;
            }
        });

        btnBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Biometrics();
            }
        });

    }

    private void Biometrics(){
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "BIOMETRICS ARE UNAVAILABLE ON THIS DEVICE!", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "SOMETHING WENT WRONG!", Toast.LENGTH_SHORT).show();

                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "THERE IS ARE NO FINGERPRINTS ASSIGNED!", Toast.LENGTH_SHORT).show();
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        bioPrompt = new BiometricPrompt(LockScreenActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if((pref.getBoolean(PREF_DEV, false) &&
                        pref.getBoolean(PREF_IPTOG, false)) ||
                        (pref.getBoolean(PREF_DEV, false) &&
                                pref.getBoolean(PREF_VPN,false))){
                    startActivity(new Intent(LockScreenActivity.this, ConnectionCheck.class));
                }
                else if(pref.getBoolean(PREF_DEF_URL_ACT, false)) {
                    startActivity(new Intent(LockScreenActivity.this, WebActivity.class));
                }
                else{
                    startActivity(new Intent(LockScreenActivity.this, WebLinksActivity.class));
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        bioPromptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Biometrics")
                .setDescription("Use Fingerprints/Device Screen Lock To Proceed")
                .setDeviceCredentialAllowed(true)
                .build();

        bioPrompt.authenticate(bioPromptInfo);
    }

    private void RandomBtnNumbers() {

        btn = new Button[]{
                (Button) findViewById(R.id.btn0),
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

        array.add("0");
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

            btn[i].setOnClickListener(v -> {
                txtPass.setError(null);
                String endTxt = txtPass.getText().toString() + text;
                txtPass.setText(endTxt);
                txtPass.setSelection(txtPass.getText().length());
            });
        }
    }

    private void SetNormalBtnNumbers() {

        btn = new Button[]{
                (Button) findViewById(R.id.btn0),
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

        array.add("0");
        array.add("1");
        array.add("2");
        array.add("3");
        array.add("4");
        array.add("5");
        array.add("6");
        array.add("7");
        array.add("8");
        array.add("9");

        for(int i = 0; i < array.size(); i++){
            for(int j = 0; j < btn.length; j++){
                String strArr = array.get(i);
                btn[j].setText(strArr);
                array.remove(i);
                btn[j].setOnClickListener(v -> {
                    txtPass.setError(null);
                    String endTxtNorm = txtPass.getText().toString() + strArr;
                    txtPass.setText(endTxtNorm);
                    txtPass.setSelection(txtPass.getText().length());
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        count = 0;
        toolbar.setVisibility(View.GONE);

        if(pref.getBoolean(PREF_RANDKEYS, false)){
            RandomBtnNumbers();
        }
        else SetNormalBtnNumbers();

        if(pref.getBoolean(PREF_BIO, false)){
            btnBio.setVisibility(View.VISIBLE);
            btnBio.setEnabled(true);
        }
        else{
            btnBio.setVisibility(View.GONE);
            btnBio.setEnabled(false);
        }
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
            case R.id.btnSet:{
                if(!item.isChecked()) {
                    startActivity(new Intent(LockScreenActivity.this, SettingsActivity.class));
                }
            }
            break;
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
            if((pref.getBoolean(PREF_DEV, false) &&
                    pref.getBoolean(PREF_IPTOG, false)) ||
                    (pref.getBoolean(PREF_DEV, false) &&
                            pref.getBoolean(PREF_VPN,false))){
                startActivity(new Intent(this, ConnectionCheck.class));
            }
            else if(pref.getBoolean(PREF_DEF_URL_ACT, false)){
                startActivity(new Intent(this, WebActivity.class));
            }
            else  startActivity(webLinks);
        }
        else txtPass.setError("Invalid Code");
    }
    @Override
    public void onBackPressed() {
        if(keyboardLayout.getVisibility() == View.VISIBLE){
            keyboardLayout.setVisibility(View.GONE);
            if(pref.getBoolean(PREF_BIO, false) &&
                    pref.getBoolean(PREF_LOCK, false)){
                btnBio.setVisibility(View.VISIBLE);
                btnBio.setEnabled(true);
            }
        }
        else {
            super.onBackPressed();
        }
    }
}
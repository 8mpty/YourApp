package com.empty.yourapp.Settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.emptyapp.R;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar tb = findViewById(R.id.mtoolbar);
        setSupportActionBar(tb);
        getWindow().setStatusBarColor(getColor(R.color.darker_purple));
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if((findViewById(R.id.set_container) != null))
        {
            if(savedInstanceState != null)
                return;

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.set_container, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
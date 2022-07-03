package com.example.emptyapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {


    private Toolbar tb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tb = findViewById(R.id.mtoolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Settings");
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
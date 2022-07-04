package com.example.emptyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class WebLinksActivity extends AppCompatActivity{

    RecyclerView rv;
    RecyclerView.Adapter rAdap;

    String[] s1, s2;

    private AlertDialog alertDialog;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static String webActURL;
    public static boolean customURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();

        s1 = getResources().getStringArray(R.array.web_name);
        s2 = getResources().getStringArray(R.array.web_links);
        rv = findViewById(R.id.main_Rec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));

        rAdap = new ProgramAdapter(this, s1, s2);
        rv.setAdapter(rAdap);

        Toolbar toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("WebApps");
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
            case R.id.main_exit: {
                finish();
            }
            break;

            case R.id.btnSet: {
                if(!item.isChecked()) {
                    startActivity(new Intent(WebLinksActivity.this, SettingsActivity.class));
                }
            }
            break;

            case R.id.main_addLink: {
                if (!item.isChecked()) {
                    OpenNewLink();
                }
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void OpenNewLink()
    {
        final EditText input = new EditText(this);
        input.setSingleLine();
        input.setHint("https://");

        FrameLayout container = new FrameLayout(WebLinksActivity.this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(65,40,65,4);
        input.setLayoutParams(params);
        container.addView(input);

        // Custom Dialog to Open new link
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Open Custom Site")
                .setView(container)
                .setPositiveButton("OK", (dialog, which) -> {
                    customURL = true;
                    webActURL = input.getText().toString();
                    Nitter.url = input.getText().toString();
                    pref.getString(Nitter.PREF_UA,Nitter.ua);
                    startActivity(new Intent(WebLinksActivity.this, Nitter.class));
                })
                .setNegativeButton("Cancel", (dialog, which) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class WebLinksActivity extends AppCompatActivity{

    RecyclerView rv;

    private AlertDialog alertDialog;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static String webActURL;

    private WebLinkAdapter adapter;
    private ArrayList<LinkModal> linkModalArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weblink);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();

        rv = findViewById(R.id.main_Rec);

        Toolbar toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("WebApps");

        loadData();
        BuildRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    private void BuildRecyclerView(){
        // initializing our adapter class.
        adapter = new WebLinkAdapter(linkModalArrayList, WebLinksActivity.this);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);

        // setting layout manager to our recycler view.
        rv.setLayoutManager(manager);

        // setting adapter to our recycler view.
        rv.setAdapter(adapter);
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
                    SetNewLink();
                }
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }




    public void SetNewLink(){

        final EditText input = new EditText(this);
        input.setSingleLine();
        input.setHint("https://");

        FrameLayout container = new FrameLayout(WebLinksActivity.this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(65,40,65,4);
        input.setLayoutParams(params);
        container.addView(input);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Add Custom Site")
                .setView(container)
                .setPositiveButton("Save", (dialog, which) -> {
                    linkModalArrayList.add(new LinkModal(input.getText().toString(), input.getText().toString()));
                    adapter.notifyItemInserted(linkModalArrayList.size());
                    saveData();
                })
                .setNegativeButton("Cancel", (dialog, which) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("weblinksshared", MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("links", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<LinkModal>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        linkModalArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (linkModalArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            linkModalArrayList = new ArrayList<>();
        }
    }

    private void saveData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("weblinksshared", MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(linkModalArrayList);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("links", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();

        // after saving data we are displaying a toast message.
        Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
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
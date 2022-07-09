package com.example.emptyapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class WebLinksActivity extends AppCompatActivity{

    RecyclerView rv;

    private AlertDialog alertDialog;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private WebLinkAdapter adapter;

    private ArrayList<LinkModal> linkModalArrayList;

    private TextInputEditText et_WebName, et_WebUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getColor(R.color.darker_purple));
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
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT){

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPos = viewHolder.getAdapterPosition();
            int toPos = target.getAdapterPosition();

            Collections.swap(linkModalArrayList, fromPos, toPos);

            recyclerView.getAdapter().notifyItemMoved(fromPos, toPos);
            saveData();

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    linkModalArrayList.remove(pos);
                    rv.getAdapter().notifyItemRemoved(pos);
                    break;

                case ItemTouchHelper.LEFT:
                    SetNewLink();
                    linkModalArrayList.remove(pos);
                    rv.getAdapter().notifyItemRemoved(pos);
                    break;
            }
            saveData();
        }
        public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                    .addSwipeRightBackgroundColor(ContextCompat.getColor(WebLinksActivity.this, R.color.black))
                    .addSwipeRightActionIcon(R.drawable.ic_delete_outline)
                    .addSwipeRightLabel("Delete")
                    .setSwipeRightLabelColor(ContextCompat.getColor(WebLinksActivity.this, R.color.white))

                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(WebLinksActivity.this, R.color.black))
                    .addSwipeLeftActionIcon(R.drawable.ic_edit_24)
                    .addSwipeLeftLabel("Edit")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(WebLinksActivity.this, R.color.white))

                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv);
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

        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.newlinkalert, null);

        et_WebName = view.findViewById(R.id.et_WebName);
        et_WebUrl = view.findViewById(R.id.et_WebUrl);

        alertDialogBuilder.setView(view)
                .setTitle("Add Custom Site")
                .setPositiveButton("OK" , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(et_WebName.getText().toString().trim().equals("") | et_WebUrl.getText().toString().trim().equals("")){
                            Toast.makeText(WebLinksActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            linkModalArrayList.add(new LinkModal(et_WebName.getText().toString(), et_WebUrl.getText().toString()));
                            adapter.notifyItemInserted(linkModalArrayList.size());
                            saveData();
                        }
                        alertDialog.show();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.darker_grey);
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
        //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
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
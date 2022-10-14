package com.example.emptyapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
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

    public static WebLinkAdapter adapter;

    public static ArrayList<LinkModal> linkModalArrayList;

    private TextInputEditText et_WebName, et_WebUrl;

    public static final String PREF_DEF_URL = "pref_def_url";
    private static final String PREF_STORAGE = "pref_STORAGE";
    private static final int STORAGE_PERMISSION_CODE = 100;

    String def_Url;

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

        def_Url = pref.getString(PREF_DEF_URL, def_Url);

        loadData();
        BuildRecyclerView();

        if(pref.getBoolean(PREF_STORAGE,false)){
            permCheck();
        }
    }

    private void permCheck(){
        if(ContextCompat.checkSelfPermission(WebLinksActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            setNewDir();
        }
        else{
            permAsk();
        }
    }

    private void permAsk(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == STORAGE_PERMISSION_CODE){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setNewDir();
            }else{
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setNewDir(){
//        String newPath = Environment.getExternalStorageDirectory() + "/" + ".EmptyHidden";
        String newPath = getExternalFilesDir(".EmptyHidden").getAbsolutePath();
//        File mydir = new File(getApplicationContext().getExternalFilesDir(".EmptyHidden").getAbsolutePath());
        File mydir = new File(newPath);

//        File mydir = new File(Environment.getDataDirectory(),".EmptyHidden");

        if (!mydir.exists()) {
            mydir.mkdir();
            Toast.makeText(getApplicationContext(),"Directory Created",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Directory Exists",Toast.LENGTH_LONG).show();
        }
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
                    EditData(pos);
                    et_WebName.setText(linkModalArrayList.get(pos).getUrlName());
                    et_WebUrl.setText(linkModalArrayList.get(pos).getUrlLink());
                    linkModalArrayList.remove(pos);
                    rv.getAdapter().notifyDataSetChanged();
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

        MenuItem searchItem = menu.findItem(R.id.main_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

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
            case R.id.main_addLink: {
                if (!item.isChecked()) {
                    SetNewLink();
                }
            }
            break;

            case R.id.main_webOpen:{
                if (!item.isChecked()) {
                    def_Url = pref.getString(PREF_DEF_URL, def_Url);
                    Nitter.url = def_Url;
                    Nitter.webSeaching = true;
                    editor.putBoolean("pref_webSearch",true);
                    editor.apply();
                    startActivity(new Intent(WebLinksActivity.this, Nitter.class));
                }
            }
            break;

            case R.id.btnSet: {
                if(!item.isChecked()) {
                    startActivity(new Intent(WebLinksActivity.this, SettingsActivity.class));
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

        et_WebUrl.setText("https://");
        et_WebUrl.setSelection(et_WebUrl.getText().length());

        alertDialogBuilder.setView(view)
                .setTitle("Add Custom Site")
                .setPositiveButton("OK" , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}})
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.darker_grey);
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean wantToClose = false;

                if(et_WebName.getText().toString().trim().equals("")){
                    et_WebName.setError("Invalid");
                    wantToClose = false;
                }
                else if (et_WebUrl.getText().toString().trim().equals("https://"+"")){
                    et_WebUrl.setError("Invalid");
                    wantToClose = false;
                }
                else {
                    linkModalArrayList.add(new LinkModal(et_WebName.getText().toString(), et_WebUrl.getText().toString()));
                    adapter.notifyItemInserted(linkModalArrayList.size());
                    saveData();
                    wantToClose = true;
                }
                alertDialog.show();
                if(wantToClose){
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void EditData(int pos){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.newlinkalert, null);

        et_WebName = view.findViewById(R.id.et_WebName);
        et_WebUrl = view.findViewById(R.id.et_WebUrl);

        alertDialogBuilder.setView(view)
                .setTitle("Edit " + linkModalArrayList.get(pos).getUrlName() + " site")
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
                        linkModalArrayList.add(new LinkModal(et_WebName.getText().toString(), et_WebUrl.getText().toString()));
                        adapter.notifyItemInserted(linkModalArrayList.size());
                        saveData();
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
        if(pref.getBoolean(PREF_STORAGE,false)){
            permCheck();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
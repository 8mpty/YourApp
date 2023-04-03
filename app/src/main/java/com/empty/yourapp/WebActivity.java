package com.empty.yourapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebViewClientCompat;

import com.empty.yourapp.RecyclerViewManagements.LinkModal;
import com.empty.yourapp.Services.AudioService;
import com.empty.yourapp.Settings.SettingsActivity;
import com.example.emptyapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.adblockplus.libadblockplus.android.settings.AdblockHelper;
import org.adblockplus.libadblockplus.android.settings.AdblockSettings;
import org.adblockplus.libadblockplus.android.settings.AdblockSettingsStorage;
import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public class WebActivity extends AppCompatActivity {


    public static AdblockWebView webView;
    WebSettings webSettings;
    public static String ua = "Mozilla/5.0 (X11; CrOS x86_64 8172.45.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.64 Safari/537.36"; // Desktop User Agent
    public static String url = "";

    LockScreenReceiver lockScreenReceiver;

    private Toolbar toolbar;
    private boolean videoEnabled;

    AlertDialog alertDialog;
    CharSequence[] values = {
            "Phone (Android 9, Chrome)",
            "Desktop (Windows 10, Chrome)"
    };

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static String SHARED_PREF_STR = "shared_pref_str";

    private static final String PREF_TB = "pref_TB";
    public static final String PREF_UA = "UA";
    private static final String PREF_ADS = "pref_ads";
    private static final String PREF_AUTOTB = "pref_AUTOTB";
    public static final String PREF_INCOG = "pref_INCOG";
    public static final String PREF_DEF_URL = "pref_def_url";
    private static final String PREF_SERVICE = "pref_Service";
    private static final String PREF_FAVICON = "pref_favicon";
    public static final String PREF_DEF_URL_ACT = "pref_def_ACT";

    private TextInputEditText et_WebName, et_WebUrl;

    String def_Url;

    public static boolean uaChanged;

    private EditText urlText;
    private ProgressBar pb;

    private ImageView iv , backIc;

    public static boolean webSeaching = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_webact);
        toolbar = findViewById(R.id.mtoolbar);
        urlText = findViewById(R.id.urlText);
        pb = findViewById(R.id.progress_bar);
        backIc = findViewById(R.id.backIc);
        iv = findViewById(R.id.fav_icon);


        iv.setVisibility(View.GONE);
        backIc.setVisibility(View.GONE);


        lockScreenReceiver = new LockScreenReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, lockFilter);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_24));
        backIc.setOnClickListener(v -> onBackPressed());

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        pref = getSharedPreferences("shared_pref_str", MODE_PRIVATE);
        editor = pref.edit();

        toolbarHide();

        ua = pref.getString(PREF_UA, ua);
        def_Url = pref.getString(PREF_DEF_URL, def_Url);

        pref.getBoolean("pref_webSearch", false);

        webStuff();

        pb.getProgressDrawable()
                .setColorFilter(Color.rgb(255,255,255),
                        android.graphics.PorterDuff.Mode.SRC_IN);

        urlText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(pref.getBoolean("pref_webSearch", true)) {
                    urlText.setSelection(urlText.getText().length());
                    if(actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(urlText.getWindowToken(), 0);
                        LoadURL(urlText.getText().toString());
                    }
                }
                return false;
            }
        });

        if(webSeaching){
            urlText.setVisibility(View.VISIBLE);
        }
        else{
            urlText.setVisibility(View.GONE);
        }
    }

    private void toolbarHide() {
        if (pref.getBoolean(PREF_TB, false)) {
            toolbar.setVisibility(View.GONE);
        }
        else{
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
//        // If true
//        if(pref.getBoolean(PREF_DEF_URL_ACT, false)){
//            menu.getItem(8).setEnabled(true);
//            Log.e("STATUS_ACT", "Enabled");
//        }
//        // else if false
//        else if(!pref.getBoolean(PREF_DEF_URL_ACT, false)){
//            menu.getItem(8).setEnabled(false);
//            editor.putBoolean(PREF_DEF_URL_ACT, false);
//            Log.e("STATUS_ACT", "Not Enabled");
//        }
//        editor.apply();
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_refresh){
            if(!item.isChecked()){
                webView.reload();
            }
        }
        else if(id == R.id.chk_vid){
            if(item.isChecked()) {
                item.setChecked(false);
                videoEnabled = false;
                webView.reload();
                Toast.makeText(this, "VIDEO DISABLED", Toast.LENGTH_SHORT).show();
            }
            else {
                item.setChecked(true);
                videoEnabled = true;
                webView.reload();
                Toast.makeText(this, "VIDEO ENABLED", Toast.LENGTH_SHORT).show();
            }
        }
        else if(id == R.id.ad_guard){
            if(!item.isChecked()) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(PREF_ADS, true);
                editor.apply();
                webView.reload();
            }
        }
        else if(id == R.id.ad_16x){
            if(!item.isChecked()) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(PREF_ADS,false);
                editor.apply();
                webView.reload();
            }
        }
        else if(id == R.id.hist){
            if(!item.isChecked()) {
                clearHistory(true);
            }
            else {
                clearHistory(false);
            }
        }
        else if(id == R.id.chk_ua){
            if(!item.isChecked()) {
                UaCustomDialog();
            }
        }
        else if(id == R.id.hidetb){
            if(!item.isChecked()) {
                HideTbDialog();
                editor.putBoolean(PREF_TB, true);
                editor.apply();
            }
        }
        else if(id == R.id.menu_set){
            if(!item.isChecked()) {
                startActivity(new Intent(WebActivity.this, SettingsActivity.class));
            }
        }
        else if(id == R.id.menu_saveWeb){
            if(!item.isChecked()) {
                saveCurWeb();
            }
        }
        else if(id == R.id.menu_copyLk){
            if(!item.isChecked()) {
                String link = webView.getUrl();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cd = ClipData.newPlainText("LinkCopy", link);
                cm.setPrimaryClip(cd);
                Toast.makeText(this,"URL Copied", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void HideTbDialog() {
        // Custom Dialog to show *HIDE OR UN-HIDE TOOLBAR*
        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this)
                .setTitle("Hide / Unhide Toolbar")
                .setMessage("! IMPORTANT ! \n\nTo UNHIDE the Toolbar, Long press any blank space. ")
                .setPositiveButton("OK", (dialog, which) -> {
                    if(toolbar.getVisibility() != View.GONE) {
                        toolbar.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }
    private void UaCustomDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this)
                .setTitle("Change User Agent")
                .setSingleChoiceItems(values, -1, (dialog, item) -> {
                    if (item == 0 ) {
                        Toast.makeText(WebActivity.this, "UA CHANGE Android", Toast.LENGTH_SHORT).show();
                        ua = "Mozilla/5.0 (Linux; Android 9; J8110 Build/55.0.A.0.552; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/71.0.3578.99 Mobile Safari/537.36";
                        SaveUAData();
                        webSettings.setUserAgentString(ua);
                    }
                    else if (item == 1) {
                        Toast.makeText(WebActivity.this, "UA CHANGE Desktop", Toast.LENGTH_SHORT).show();
                        ua = "Mozilla/5.0 (X11; CrOS x86_64 8172.45.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.64 Safari/537.36";
                        SaveUAData();
                        webSettings.setUserAgentString(ua);
                    }
                    editor.apply();
                    alertDialog.dismiss();
                    webView.reload();
                })
                .setNeutralButton("cancel", (dialog, which) -> alertDialog.dismiss());

        alertDialog = builder.create();
        alertDialog.show();
    }
    private void saveCurWeb() {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.newlinkalert, null);

        et_WebName = view.findViewById(R.id.et_WebName);
        et_WebUrl = view.findViewById(R.id.et_WebUrl);

        et_WebUrl.setText(webView.getUrl());
        et_WebUrl.setSelection(et_WebUrl.getText().length());

        alertDialogBuilder.setView(view)
                .setTitle("Save Current Website")
                .setPositiveButton("OK" , (dialog, which) -> {})
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

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
                    WebLinksActivity.linkModalArrayList.add(new LinkModal(et_WebName.getText().toString(), et_WebUrl.getText().toString()));
                    WebLinksActivity.adapter.notifyItemInserted(WebLinksActivity.linkModalArrayList.size());
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
        String json = gson.toJson(WebLinksActivity.linkModalArrayList);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("links", json);
        editor.apply();
    }
    // When User Scrolls, Toolbar will get hidden and unhidden automatically if reaches the top.
    private class Scroll implements View.OnScrollChangeListener {
        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            if(pref.getBoolean(PREF_AUTOTB, false)) {
                int move = webView.getScrollY();
                if(!pref.getBoolean(PREF_TB,true)){
                    if (move >= 300) {
                        toolbar.setVisibility(View.GONE);
                    }
                    else if (move <= 50) toolbar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private static class LockScreenReceiver extends  BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//                    webView.onResume();
                }
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void webStuff()
    {
        // Webview
        webView = findViewById(R.id.webview);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.setLongClickable(true);
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearSslPreferences();
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setBackgroundColor(0x00000000);

        // Websettings
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setUserAgentString(ua);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            webView.setForceDarkAllowed(true);
            webSettings.setForceDark(WebSettings.FORCE_DARK_ON);
        }

        webView.setWebChromeClient(new MyChrome());
        webView.setWebViewClient(new MyWebViewClient());

        if(pref.getBoolean(PREF_DEF_URL_ACT, false)){
            LoadURL(pref.getString(PREF_DEF_URL, null));
        }else {
            LoadURL(url);
        }

        webView.setOnScrollChangeListener(new Scroll());
        webView.setOnLongClickListener(v -> {
            if (toolbar.getVisibility() != View.VISIBLE) {
                toolbar.setVisibility(View.VISIBLE);
                editor.putBoolean(PREF_TB, false);
                editor.apply();
            }
            return false;
        });

        if(pref.getBoolean(PREF_INCOG, false)){
            InCognitoMode();
        }

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                DownloadManager.Request request = new DownloadManager.Request(Uri.parse((url)));
//                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                String title = URLUtil.guessFileName(url, contentDisposition, mimetype);
//                request.setMimeType(mimetype);
//                String cookies = CookieManager.getInstance().getCookie(url);
//                request.addRequestHeader("coockie", cookies);
//                request.addRequestHeader("User-Agent", userAgent);
//                request.setDescription("Downloading File...");
//                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
//                request.allowScanningByMediaScanner();
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                String destFolder = Environment.getExternalStorageDirectory().getPath() + "/.EmptyHidden";
//                request.setDestinationInExternalPublicDir(destFolder, title);
//                dm.enqueue(request);
//                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        if(!AdblockHelper.get().isInit()){
            AdblockSettingsStorage storage = AdblockHelper
                    .get()
                    .init(this, getFilesDir().getAbsolutePath(), true, AdblockHelper.PREFERENCE_NAME)
                    .getStorage();
            AdblockSettings settings = storage.load();
            if (settings == null) // not yet saved
            {
                settings = AdblockSettingsStorage.getDefaultSettings(this);
            }

            settings.setAdblockEnabled(true);
            storage.save(settings);
        }
    }
    private void Mover(String file){
        String sourcePath = Environment.DIRECTORY_DOWNLOADS + "/" + file;
        File source = new File(sourcePath);
        Log.e("FILE SOURCE:" ,source.toString());

        String destPath = Environment.getExternalStorageDirectory().getPath() + "/.EmptyHidden";
        File dest = new File(destPath);
        Log.e("FILE SOURCE:" ,dest.toString());


        File tempFile = new File(Environment.DIRECTORY_DOWNLOADS,"temp_" + file);

        boolean suc = source.renameTo(tempFile);

        Log.e("FILE SOURCE:" ,tempFile.toString());

        if(suc){
            File newFile = new File(dest, tempFile.getName());
            tempFile.renameTo(newFile);
            Log.e("MOVING", newFile.toString());
            Log.e("MOVING", newFile.toString());
        }else{
            Log.e("NO", "RENAMED");
        }


        if(tempFile.exists()){
            File newFile = new File(dest, tempFile.getName());
            tempFile.renameTo(newFile);
            Log.e("MOVING", newFile.toString());
            Log.e("MOVING", newFile.toString());
        }
        else{
            Log.e("MOVING", "NOT EXSITS");
        }

    }

    private void LoadURL(String actURL){
        boolean matchURL = Patterns.WEB_URL.matcher(actURL).matches();
        if(matchURL){
            webView.loadUrl(actURL);
        }
        else {
            if(def_Url.contains("google.com") || def_Url.contains("bing.com")){
                webView.loadUrl(def_Url + "/search?q=" + actURL);
            }
            else if(def_Url.contains("duckduckgo.com")){
                webView.loadUrl(def_Url + "/?q=" + actURL);
            }
        }
    }

    private void InCognitoMode(){
        //Make sure No cookies are created
        CookieManager.getInstance().setAcceptCookie(false);

        //Make sure no caching is done
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webView.clearHistory();
        webView.clearCache(true);


        //Make sure no autofill for Forms/ user-name password happens for the app
        webView.clearFormData();
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setSaveFormData(false);
    }

    private void clearHistory(@NonNull Boolean clearHistory) {
        String curUrl = webView.getUrl();

        if(clearHistory){
            ClearCookies();

            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();

            Toast.makeText(this, "Cleared History", Toast.LENGTH_SHORT).show();

            webView.loadUrl(curUrl);
        }
    }

    private class MyWebViewClient extends WebViewClientCompat {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);

            urlText.setText(webView.getUrl());

            ScriptsInjection(view);

            pb.setVisibility(View.INVISIBLE);
            backIc.setVisibility(View.VISIBLE);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            urlText.setText(webView.getUrl());
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }
    private static void ClearCookies(){
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
    }
    private void ScriptsInjection(WebView view){

        // https://stackoverflow.com/questions/21552912/android-web-view-inject-local-javascript-file-to-remote-webpage
        boolean ads = pref.getBoolean(PREF_ADS, true);

        if(ads){
            // Remove ALL YT or YT Music Ads using AdGuard //
            injectScriptFile(view , "scripts/adguard_yt.js");
        }
        else {
            // Remove YT or YT Music Video ads by increasing speed of video ads by 16x (Max). //
            injectScriptFile(view , "scripts/yt_adblocker.js");
        }



        // Allow Background Playback for YT Music but not Youtube.com
        //  ̶T̶O̶D̶O̶ ̶W̶O̶R̶K̶S̶ ̶S̶O̶M̶E̶T̶I̶M̶E̶S̶ ̶N̶O̶W̶
        // FIXED? ONLY IN YT MUSIC (LMAO KMS PLZ)
        // injectScriptFile(view , "scripts/bk.js");
        injectScriptFile(view , "scripts/bk_2.js");

        if(!videoEnabled) {
            injectScriptFile(view , "scripts/videoremover.js");
            videoEnabled = false;
        }


        try{
            if(webView.getUrl().contains("youtube.com")){
                String[] strings = {

                        // https://github.com/4v3ngR/ytm-wrapped //
                        "scripts/testscripts/index.js",
                        "scripts/testscripts/plugins.js",

                        "scripts/testscripts/audioonly.js",
                        "scripts/testscripts/background.js",
                        "scripts/testscripts/config.js",
                        "scripts/testscripts/controls.js",
                        "scripts/testscripts/mediasession.js",
                        "scripts/testscripts/swipe.js",
                        "scripts/testscripts/ui.js",


                        // Bypass Age Restriction Videos On Youtube //
                        "scripts/agerestricbypass.js",

                        // Full AD Blocker? //
                        "scripts/HugeBlock.js",

                        // Remove Sign In Button on YT Music //
                        "scripts/rm_signIN.js",

                        // When did I add this? :( //
                        "scripts/adguard_extra.js",
                };
                injectScriptFileArray(view,strings);
            }
            else {
                Log.e("WEBSITE IS NOT", "YOUTUBE");
            }
        }catch (Exception e){
            Log.e("TESTSCRIPTS: ", "NOT LOADED");
        }

    }

    // JavaScript Injector
    private void injectScriptFile(@NonNull WebView view, String scriptFile) {
        InputStream input;
        try {
            input = getAssets().open(scriptFile);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // JavaScript Injector
    private void injectScriptFileArray(@NonNull WebView view, String[] scriptFile) {
        InputStream input;
        try {
            for(int i = 0; i < scriptFile.length; i++){
                input = getAssets().open(scriptFile[i]);
                byte[] buffer = new byte[input.available()];
                input.read(buffer);
                input.close();

                // String-ify the script byte-array using BASE64 encoding !!!
                String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
                view.loadUrl("javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var script = document.createElement('script');" +
                        "script.type = 'text/javascript';" +
                        // Tell the browser to BASE64-decode the string into your script !!!
                        "script.innerHTML = window.atob('" + encoded + "');" +
//                        "console.error('"+ scriptFile[i] +"');"+
                        "parent.appendChild(script)" +
                        "})()");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            pb.setProgress(newProgress);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if(iv.getVisibility() == View.VISIBLE) {
                iv.setImageBitmap(icon);
            }
            super.onReceivedIcon(view, icon);
        }
    }








































    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }




















































    public void startService(){

//        Intent serviceIntent = new Intent(this, AudioService.class);
//        serviceIntent.putExtra("inputExtra", "webView.getUrl()");
//        startForegroundService(serviceIntent);

        Intent intent = new Intent(this, AudioService.class);
        startForegroundService(intent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, AudioService.class);
        stopService(serviceIntent);
    }
    private void SaveUAData() {
        editor.putString(PREF_UA, ua);
        editor.apply();
    }
















































    @Override
    protected void onResume() {
        super.onResume();
        toolbarHide();

        if(pref.getBoolean(PREF_SERVICE, false) &&
                pref.getBoolean("pref_DEV",false)){
            stopService();
        }

        webView.loadUrl("javascript: (function() { document.getElementsByTagName('video')[0].play();})()");
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //startService(new Intent(WebActivity.this, AudioService.class));
        if(pref.getBoolean(PREF_SERVICE, false) &&
                pref.getBoolean("pref_DEV",false)){
            Log.e("CALLED","SERVICE PAUSE");
            startService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(pref.getBoolean(PREF_SERVICE, false) &&
                pref.getBoolean("pref_DEV",false)){
            Log.e("CALLED","SERVICE STOP");
            startService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
        if(webView != null){
            webView.destroy();
        }
        finish();
    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()) {
            webView.goBack();
        }
        else {
            super.onBackPressed();
            onStop();
            if(webView != null){
                webView.destroy();
            }
            webSeaching = false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        if(webView != null){
            webView.destroy();
        }
        webSeaching = false;
        return super.onSupportNavigateUp();
    }
}
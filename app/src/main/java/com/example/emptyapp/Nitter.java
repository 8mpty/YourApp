package com.example.emptyapp;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public class Nitter extends AppCompatActivity {

    AdblockWebView webView;
    WebSettings webSettings;
    public static String ua = "Mozilla/5.0 (X11; CrOS x86_64 8172.45.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.64 Safari/537.36"; // Desktop User Agent
    public static String url = "";

    LockScreenReceiver lockScreenReceiver;

    private Toolbar toolbar;
    private boolean videoEnabled;

    AlertDialog alertDialog;
    CharSequence[] values = {"Phone (Android 9, Chrome)", "Desktop (Windows 10, Chrome)"};

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final String PREF_TB = "pref_TB";
    private static final String PREF_INCOG = "pref_INCOG";
    private static final String PREF_AUTO_TB = "auto_TB";
    public static final String PREF_UA = "UA";

    public static  boolean uaChanged;

    boolean incog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_nitter);
        toolbar = findViewById(R.id.mtoolbar);

        Adblocker.init(this);

        lockScreenReceiver = new LockScreenReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, lockFilter);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setHideOnContentScrollEnabled(true);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();

        if (pref.getBoolean(PREF_TB, false)) {
            toolbar.setVisibility(View.GONE);
        }
        else toolbar.setVisibility(View.VISIBLE);


        incog = pref.getBoolean(PREF_INCOG, false);
        ua = pref.getString(PREF_UA, ua);

        webStuff();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.chk_vid:
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
                break;

            case R.id.chk_ua:
                if(!item.isChecked()) {
                    UaCustomDialog();
                }
                break;

            case R.id.hidetb:
                if(!item.isChecked()) {
                    HideTbDialog();
                    editor.putBoolean(PREF_TB, true);
                    editor.commit();
                }
                break;
            case R.id.menu_set:
                if(!item.isChecked()) {
                    startActivity(new Intent(Nitter.this, SettingsActivity.class));
                }

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void HideTbDialog()
    {
        // Custom Dialog to show *HIDE OR UN-HIDE TOOLBAR*
        AlertDialog.Builder builder = new AlertDialog.Builder(Nitter.this)
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
    public void UaCustomDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Nitter.this)
                .setTitle("Change User Agent")
                .setSingleChoiceItems(values, -1, (dialog, item) -> {
                    if (item == 0 ) {
                        Toast.makeText(Nitter.this, "UA CHANGE Android", Toast.LENGTH_SHORT).show();
                        ua = "Mozilla/5.0 (Linux; Android 9; J8110 Build/55.0.A.0.552; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/71.0.3578.99 Mobile Safari/537.36";
                        SaveUAData();
                        webSettings.setUserAgentString(ua);
                    }
                    else if (item == 1) {
                        Toast.makeText(Nitter.this, "UA CHANGE Desktop", Toast.LENGTH_SHORT).show();
                        ua = "Mozilla/5.0 (X11; CrOS x86_64 8172.45.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.64 Safari/537.36";
                        SaveUAData();
                        webSettings.setUserAgentString(ua);
                    }
                    editor.commit();
                    alertDialog.dismiss();
                    webView.reload();
                })
                .setNeutralButton("cancel", (dialog, which) -> alertDialog.dismiss());

        alertDialog = builder.create();
        alertDialog.show();
    }

    // When User Scrolls, Toolbar will get hidden and unhidden automatically if reaches the top.
//    private class Scroll implements View.OnScrollChangeListener {
//        @Override
//        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//            int move = webView.getScrollY();
//            if (move >= 5) {
//                toolbar.setVisibility(View.GONE);
//            }
//            else if (move <= 2)toolbar.setVisibility(View.VISIBLE);
//        }
//    }

    public class LockScreenReceiver extends  BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent != null && intent.getAction() != null)
            {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
                {
                    webView.onResume();
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void webStuff()
    {
        webView = findViewById(R.id.webview);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.setLongClickable(true);
        webView.clearHistory();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearSslPreferences();
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setBackgroundColor(0x00000000);

        // Default to NOT INCOG , BY RIGHT ;) //
        webSettings.setAppCacheEnabled(true);
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

        webView.loadUrl(url);

        // Enable this for scrolling hide toolbar feature //
        // Long clicking any space with un-hide the toolbar.

        //webView.setOnScrollChangeListener(new Scroll());

        webView.setOnLongClickListener(v -> {
            if (toolbar.getVisibility() != View.VISIBLE) {
                toolbar.setVisibility(View.VISIBLE);
                editor.putBoolean(PREF_TB, false);
                editor.commit();
            }
            return false;
        });
    }

    private void IncognitoChanger()
    {
        if(!incog){
            editor.putBoolean(PREF_INCOG, false);
            editor.commit();
            webSettings.setAppCacheEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            Toast.makeText(this, "NOT INCOG", Toast.LENGTH_SHORT).show();

        }
        else {
            editor.putBoolean(PREF_INCOG, true);
            editor.commit();
            webSettings.setAppCacheEnabled(false);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            Toast.makeText(this, "IN INCOG", Toast.LENGTH_SHORT).show();
        }
    }


    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);

            // Remove Sign In Button on YT Music //
            injectScriptFile(view , "scripts/rm_signIN.js");

            // Remove YT or YT Music Video ads by increasing speed of video ads by 16x (Max). //
            //injectScriptFile(view , "scripts/yt_adblocker.js");

            // Remove ALL YT or YT Music Ads using AdGuard //
            injectScriptFile(view , "scripts/adguard_yt.js");

            // Bypass Age Restriction Videos On Youtube //
            injectScriptFile(view , "scripts/agerestricbypass.js");

            // Allow Background Playback for YT Music but not Youtube.com
            // TODO FIX BACKGROUND AUDIO
            injectScriptFile(view , "scripts/bk.js");

            injectScriptFile(view , "scripts/mute.js");

            if(!videoEnabled)
            {
                // Remove YT video on YTMusic but will not skip ads.
                injectScriptFile(view , "scripts/videoremover.js");
                videoEnabled = false;
            }
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class MyChrome extends WebChromeClient
    {
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
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }


    private void SaveUAData() {
        editor.putString(PREF_UA, ua);
        editor.commit();
    }
    @Override
    protected void onResume() {
        if (pref.getBoolean(PREF_TB, false))
            toolbar.setVisibility(View.GONE);
        else
            toolbar.setVisibility(View.VISIBLE);

        if(uaChanged){
            ua = pref.getString(PREF_UA, ua);
            webSettings.setUserAgentString(ua);
            webView.loadUrl(WebLinksActivity.webActURL);
            webView.reload();
            uaChanged = false;
        }

        IncognitoChanger();
        webView.loadUrl("javascript: (function() { document.getElementsByTagName('video')[0].play();})()");
        webView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onBackPressed(){
        if(webView.canGoBack()) {
            webView.goBack();
        }
        else {
            webView.clearHistory();
            super.onBackPressed();
            onStop();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
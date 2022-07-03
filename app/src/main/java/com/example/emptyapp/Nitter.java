package com.example.emptyapp;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.monstertechno.adblocker.util.AdBlocker;

import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class Nitter extends AppCompatActivity {

    //WebView webView;
    AdblockWebView webView;
    WebSettings webSettings;
    public static StringBuilder adservers;
    public static String loddnormallist = "0";
    String ua = "Mozilla/5.0 (X11; CrOS x86_64 8172.45.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.64 Safari/537.36"; // Desktop User Agent
    public static String url = "";

    public static boolean isAudio;

    LockScreenReceiver lockScreenReceiver;

    private Toolbar toolbar;
    private boolean videoEnabled;

    AlertDialog alertDialog;
    CharSequence[] values = {"Phone (Android 9, Chrome)", "Desktop (Windows 10, Chrome)"};

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private boolean tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_nitter);
        toolbar = findViewById(R.id.toolbar);

        readAdServers();
        Adblocker.init(this);

        lockScreenReceiver = new LockScreenReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, lockFilter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu));

        pref  = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = pref.edit();
        ua = pref.getString("UA","");
        tb = pref.getBoolean("TB", false);

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
                if(item.isChecked())
                {
                    item.setChecked(false);
                    videoEnabled = false;
                    webView.reload();
                    Toast.makeText(this, "VIDEO DISABLED", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    item.setChecked(true);
                    videoEnabled = true;
                    webView.reload();
                    Toast.makeText(this, "VIDEO ENABLED", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.chk_ua:
                if(!item.isChecked())
                {
                    UaCustomDialog();
                }
                break;

            case R.id.hidetb:
                if(!item.isChecked())
                {
                    HideTbDialog();
                }
                break;
            case R.id.menu_set:
                if(!item.isChecked())
                {
                    startActivity(new Intent(Nitter.this, SettingsActivity.class));
                }

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void HideTbDialog()
    {
        // Custom Dialog to show *HIDE OR UNHIDE TOOLBAR*
        AlertDialog.Builder builder = new AlertDialog.Builder(Nitter.this)
                .setTitle("Hide / Unhide Toolbar")
                .setMessage("! IMPORTANT ! \n\nTo UNHIDE the Toolbar, Long press any blank space. ")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(toolbar.getVisibility() != View.GONE) {
                            toolbar.setVisibility(View.GONE);
                            editor.putBoolean("TB", false);
                            editor.commit();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }
    public void UaCustomDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Nitter.this)
                .setTitle("Change User Agent")
                .setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
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
                    }
                })
                .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();
    }

    // When User Scrolls, Toolbar will get hidden and unhidden automatically if reaches the top.
    private class Scroll implements View.OnScrollChangeListener {
        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            int move = webView.getScrollY();
            if (move >= 2) {
                toolbar.setVisibility(View.GONE);
            }
            else{
                toolbar.setVisibility(View.VISIBLE);
            }
        }
    }

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
        webView.clearHistory();
        webView.clearSslPreferences();
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setBackgroundColor(0x00000000);

        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
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
        //webView.setOnScrollChangeListener(new Scroll());

        // Long clicking any space with un-hide the toolbar.
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (toolbar.getVisibility() != View.VISIBLE) {
                    toolbar.setVisibility(View.VISIBLE);
                    editor.putBoolean("TB", false);
                    editor.commit();
                }
                return false;
            }
        });
    }

    private void readAdServers()
    {
        String line;
        adservers = new StringBuilder();

        InputStream is = this.getResources().openRawResource(R.raw.hosts);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        if (is != null)
        {
            try
            {
                while ((line = br.readLine()) != null)
                {
                    if (loddnormallist.equals("0"))
                    {
                        adservers.append(line);
                        adservers.append("\n");
                    }
                    if (loddnormallist.equals("1"))
                    {
                        adservers.append(":::::" + line);
                        adservers.append("\n");
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
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

            if(!videoEnabled)
            {
                // Remove YT video on YTMusic but will not skip ads.
                injectScriptFile(view , "scripts/videoremover.js");

                videoEnabled = false;
            }
        }

        private Map<String, Boolean> loadedUrls = new HashMap<>();
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            boolean ad;
            if (!loadedUrls.containsKey(url)) {
                ad = AdBlocker.isAd(url);
                loadedUrls.put(url, ad);
            } else {
                ad = loadedUrls.get(url);
            }
            return ad ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, url);
        }

    }

    // JavaScript Injector
    private void injectScriptFile(WebView view, String scriptFile) {
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

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
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


    private void SaveUAData()
    {
        editor.putString("UA", ua);
        editor.commit();
    }
    @Override
    protected void onResume() {
        pref.getBoolean("TB", false);

        Log.e("onMETHOD","RESUME");
        webView.loadUrl("javascript: (function() { document.getElementsByTagName('video')[0].play();})()");
        webView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onMETHOD","PAUSED");
        webView.onResume();
    }

    @Override
    protected void onStop() {
        Log.e("onMETHOD","STOPPED");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("onMETHOD","DESTROYED");
        super.onDestroy();
    }
    @Override
    public void onBackPressed(){
        if(webView.canGoBack())
        {
            webView.goBack();
        }
        else
        {
            webView.clearHistory();
            super.onBackPressed();
            onStop();
        }
    }
}
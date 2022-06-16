package com.example.emptyapp;


import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;


public class Nitter extends AppCompatActivity {


    WebView webView;
    WebSettings webSettings;
    StringBuilder adservers;
    String loddnormallist = "1";
    String ua = "Mozilla/5.0 (Linux; Android 7.0; SM-T827R4 Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.116 Safari/537.36";
    public static String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getColor(R.color.black));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nitter);

        readAdServers();
        webView = findViewById(R.id.webview);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.setLongClickable(true);
        webView.clearHistory();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(true);
        webView.getSettings().setUserAgentString(ua);
        webSettings.setForceDark(webSettings.FORCE_DARK_ON);
        webView.setForceDarkAllowed(true);
        webView.setBackgroundColor(0x00000000);

        webView.setWebChromeClient(new MyChrome());

        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);
    }



    private void readAdServers()
    {
        String line = "";
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
                    if (loddnormallist.equals("2"))
                    {
                        adservers.append("https://" + line);
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
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
        {
            ByteArrayInputStream EMPTY = new ByteArrayInputStream("".getBytes());
            String adds = String.valueOf(adservers);

            if(adds.contains(":::::" + request.getUrl().getHost()))
            {
                Log.d("ADDDDDDS IN FILE","ADDDDDDS");
                return new WebResourceResponse("text/plain", "utf-8", EMPTY);
            }
            else if (adds.contains(request.getUrl().getHost()))
            {
                Log.d("ADDDDDDS IN FILE","ADDDDDDS");

                return new WebResourceResponse("text/plain", "utf-8", EMPTY);
            }

            if(adds.contains("https://" + request.getUrl().getHost()))
            {
                Log.d("ADDDDDDS IN FILE","ADDDDDDS");
                return new WebResourceResponse("text/plain", "utf-8", EMPTY);
            }

            if(adds.contains("youtubei/v1/log_event" + request.getUrl().getHost()) || adds.contains("play.google.com" + request.getUrl().getHost()) || adds.contains("api/stats/atr" + request.getUrl().getHost()) || adds.contains("doubleclick.net" + request.getUrl().getHost()))
            {
                Log.d("ADDDDDDS IN FILE","ADDDDDDS");
                return new WebResourceResponse("text/plain", "utf-8", EMPTY);
            }

            if(adds.contains(request.getUrl().getHost()))
            {
                Log.d("ADDDDDDS IN FILE","ADDDDDDS");
                return new WebResourceResponse("text/plain", "utf-8", EMPTY);
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            CookieManager.getInstance().flush();
            super.onPageFinished(view, url);
            webView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('sign-in-link style-scope ytmusic-nav-bar')[0].style.display='none';})()");

        }
    }

    private class MyChrome extends WebChromeClient
    {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
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
        }
    }
}
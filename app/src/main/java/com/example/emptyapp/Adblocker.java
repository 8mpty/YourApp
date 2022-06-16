package com.example.emptyapp;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebResourceResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Adblocker {

    private static Adblocker instance;

    private Context context;
    private Set<String> hostsBlacklist;

    private Adblocker(Context context) {
        this.context = context;
        hostsBlacklist = new HashSet<String>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open("host.txt")))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() > 0) {
                    hostsBlacklist.add(line);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static Adblocker getInstance(Context context) {
        if (instance == null) {
            instance = new Adblocker(context);
        }
        return instance;
    }

    public boolean isAd(Uri uri) {
        return isAdHost(uri.getHost());
    }

    private boolean isAdHost(String host) {
        if (host == null || host.length() == 0) {
            return false;
        }

        int index = host.indexOf(".");
        return index >= 0 && (hostsBlacklist.contains(host) || index + 1 < host.length() && isAdHost(host.substring(index + 1)));
    }

    public WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }
}
package com.empty.yourapp.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.empty.yourapp.WebActivity;
import com.example.emptyapp.BuildConfig;
import com.example.emptyapp.R;

import timber.log.Timber;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    public static final String PREF_IPSAVE = "";
    public static final String PREF_DEF_URL = "pref_def_url";
    public static String SHARED_PREF_STR = "shared_pref_str";

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ListPreference url;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        AlertDialog alertDialog;

        SwitchPreference sw = (SwitchPreference) findPreference("pref_def_ACT");

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
//        pref = getActivity().getSharedPreferences(SHARED_PREF_STR, Context.MODE_PRIVATE);

        url = (ListPreference) findPreference("pref_def_url");

        if(url.getValue() == null){
//            url.setValue("https://start.duckduckgo.com");
        }

        preferenceChangeListener = (sharedPreferences, key) -> {
            editor = sharedPreferences.edit();
            EditTextPreference ipt = (EditTextPreference) findPreference("pref_Ipa");

            if(BuildConfig.DEBUG){
                Timber.plant(new Timber.DebugTree());
            }

            if(key.equals("pref_Ipa")){
                if(ipt != null) {
                    String act_ip = ipt.getText();
                    saveIP(act_ip);
                    Timber.tag("IP SAVED IS ").e(act_ip);
                }
            }

            if(key.equals(WebActivity.PREF_UA)){
                WebActivity.uaChanged = true;
            }

            if(key.equals("pref_UNINSTALL")){
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:com.empty.yourapp"));
                startActivity(intent);
            }

            if(key.equals("pref_def_ACT")){
                if(sw != null){
                    sw.setSummary(pref.getString(PREF_DEF_URL, null));
                }
            }
        };

        if(sw != null){
            sw.setSummary(pref.getString(PREF_DEF_URL, null));
        }

        androidx.preference.EditTextPreference editTextPreference = getPreferenceManager().findPreference("pref_SecKey");
        assert editTextPreference != null;
        editTextPreference.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED));
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private void saveIP(String ip){
        editor.putString(PREF_IPSAVE, ip);
        editor.apply();
    }
}

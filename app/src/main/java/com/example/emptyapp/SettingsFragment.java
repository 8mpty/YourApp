package com.example.emptyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    public static final String PREF_DEF_URL_ACT = "pref_def_url_act";
    public static final String PREF_DEV = "pref_DEV";
    public static final String PREF_IPTOG = "pref_IpTog";
    public static final String PREF_IPSAVE = "";
    public static final String PREF_DEF_ACT = "pref_def_ACT";
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        AlertDialog alertDialog;
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        preferenceChangeListener = (sharedPreferences, key) -> {
            editor = sharedPreferences.edit();
            EditTextPreference ipt = (EditTextPreference) findPreference("pref_Ipa");
            String act_ip = ipt.getText();

            saveip(act_ip, editor, sharedPreferences);

            if(key.equals(Nitter.PREF_UA)){
                Nitter.uaChanged = true;
            }

            if(key.equals("pref_UNINSTALL")){
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:com.example.emptyapp"));
                startActivity(intent);
            }

//            if(key.equals("pref_DEV")){
//                String test = String.valueOf(pref.getBoolean(PREF_DEV, false));
//                Log.e("TEST",test);
//
//                // if dev-tog is True
//                if(pref.getBoolean(PREF_DEV, false)){
//                    Log.e("TEST","truetrue");
//
//                }else{
//                    if(pref.getBoolean(PREF_IPTOG ,false)){
//
//                    }
//                    Log.e("TEST","falsefalse");
//
//                }
//            }
        };



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


    public void saveip(String ip, SharedPreferences.Editor editors, SharedPreferences pref){
        editors = pref.edit();
        editors.putString(PREF_IPSAVE, ip);
        editors.apply();
    }
}

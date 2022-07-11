package com.example.emptyapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        preferenceChangeListener = (sharedPreferences, key) -> {
            if(key.equals(Nitter.PREF_UA)){
                Nitter.uaChanged = true;
            }
            if(key.equals("pref_hist")){
            }
        };

        androidx.preference.EditTextPreference editTextPreference = getPreferenceManager().findPreference("pref_SecKey");
        assert editTextPreference != null;
        editTextPreference.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED));
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}

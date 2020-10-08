package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.duzhaokun123.bilibilihd.R;
import com.takisoft.preferencex.PreferenceFragmentCompat;
import com.takisoft.preferencex.SimpleMenuPreference;

public class SettingsDisplayFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_display, rootKey);

        SimpleMenuPreference uiMod = findPreference("ui_mod");
        if (uiMod != null) {
            uiMod.setOnPreferenceChangeListener((preference, newValue) -> {
                if ("2".equals(newValue)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) ;
                } else if ("1".equals(newValue)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) ;
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) ;
                }
                return true;
            });
        }
    }
}
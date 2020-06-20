package com.duzhaokun123.bilibilihd.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.duzhaokun123.bilibilihd.R;

public class SettingsDanmakuFragment extends PreferenceFragmentCompat {

    Preference test;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_danamku, rootKey);

        test = findPreference("test");

        if (test != null) {
            test.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getContext(), DanmakuTestActivity.class);
                startActivity(intent);
                return false;
            });
        }
    }
}

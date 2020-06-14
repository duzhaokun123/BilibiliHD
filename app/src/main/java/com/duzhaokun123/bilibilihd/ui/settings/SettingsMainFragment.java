package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.duzhaokun123.bilibilihd.R;

public class SettingsMainFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_main, rootKey);
    }
}

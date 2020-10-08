package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.duzhaokun123.bilibilihd.R;
import com.takisoft.preferencex.PreferenceFragmentCompat;

public class SettingsPlayFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_play, rootKey);
    }
}

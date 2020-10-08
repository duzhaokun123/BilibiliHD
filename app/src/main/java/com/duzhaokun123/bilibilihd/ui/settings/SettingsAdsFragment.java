package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;

import com.duzhaokun123.bilibilihd.R;
import com.takisoft.preferencex.PreferenceFragmentCompat;

public class SettingsAdsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_ads, rootKey);
    }
}

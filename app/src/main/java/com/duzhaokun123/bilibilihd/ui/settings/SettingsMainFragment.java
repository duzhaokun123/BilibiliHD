package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.utils.Handler;

import java.util.Objects;

public class SettingsMainFragment extends PreferenceFragmentCompat {

    Preference users, display, download, ads, about;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_main, rootKey);

        users = findPreference("users");
        display = findPreference("display");
        download = findPreference("download");
        ads = findPreference("ads");
        about = findPreference("about");

        MyOnPreferenceClickListener myOnPreferenceClickListener = new MyOnPreferenceClickListener();

        users.setOnPreferenceClickListener(myOnPreferenceClickListener);
        display.setOnPreferenceClickListener(myOnPreferenceClickListener);
        download.setOnPreferenceClickListener(myOnPreferenceClickListener);
        ads.setOnPreferenceClickListener(myOnPreferenceClickListener);
        about.setOnPreferenceClickListener(myOnPreferenceClickListener);
    }

    @Nullable
    Handler getSettingsActivityHandler() {
        Handler settingsActivityHandler = null;
        if (getActivity() instanceof SettingsActivity) {
            settingsActivityHandler = ((SettingsActivity) getActivity()).getHandler();
        }
        return settingsActivityHandler;
    }

    void changeSettingsActivityTitle(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(SettingsActivity.BUNDLE_KEY_TITLE, title);
        Message message = new Message();
        message.what = SettingsActivity.WHAT_CHANGE_TITLE;
        message.setData(bundle);
        Objects.requireNonNull(getSettingsActivityHandler()).sendMessage(message);
    }

    class MyOnPreferenceClickListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String title = "";
            if (preference == users) {
                title = getString(R.string.users);
            } else if (preference == display) {
                title = getString(R.string.display);
            } else if (preference == download) {
                title = getString(R.string.download);
            } else if (preference == ads) {
                title = getString(R.string.ad);
            } else if (preference == about) {
                title = getString(R.string.about);
            }
            changeSettingsActivityTitle(title);
            return false;
        }
    }
}

package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;
import android.os.Message;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.utils.Handler;

import java.util.Objects;

public class SettingsMainFragment extends PreferenceFragmentCompat {

    Preference users, display, danmaku, download, ads, about;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_main, rootKey);

        users = findPreference("users");
        display = findPreference("display");
        danmaku = findPreference("danmaku");
        download = findPreference("download");
        ads = findPreference("ads");
        about = findPreference("about");

        MyOnPreferenceClickListener myOnPreferenceClickListener = new MyOnPreferenceClickListener();

        users.setOnPreferenceClickListener(myOnPreferenceClickListener);
        display.setOnPreferenceClickListener(myOnPreferenceClickListener);
        danmaku.setOnPreferenceClickListener(myOnPreferenceClickListener);
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

    @Nullable
    FrameLayout getSettingActivity2ndFl() {
        if (getActivity() instanceof SettingsActivity) {
            return ((SettingsActivity) getActivity()).get2ndFl();
        } else {
            return null;
        }
    }

    class MyOnPreferenceClickListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            FrameLayout settingActivity2ndFl = getSettingActivity2ndFl();
            if (settingActivity2ndFl != null) {
                try {
                    getParentFragmentManager().beginTransaction().replace(settingActivity2ndFl.getId(), (Fragment) Class.forName(preference.getFragment()).newInstance()).commitAllowingStateLoss();
                } catch (IllegalAccessException | java.lang.InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                String title = "";
                if (preference == users) {
                    title = getString(R.string.users);
                } else if (preference == display) {
                    title = getString(R.string.display);
                } else if (preference == danmaku) {
                    title = getString(R.string.danmaku);
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
}

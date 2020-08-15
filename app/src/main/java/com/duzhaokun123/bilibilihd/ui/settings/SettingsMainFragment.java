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

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_main, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        FrameLayout settingActivity2ndFl = getSettingActivity2ndFl();
        if (settingActivity2ndFl != null) {
            try {
                getParentFragmentManager().beginTransaction().replace(settingActivity2ndFl.getId(), (Fragment) Class.forName(preference.getFragment()).newInstance()).commitAllowingStateLoss();
            } catch (IllegalAccessException | java.lang.InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            CharSequence title = preference.getTitle();
            changeActivityTitle(title);
            return super.onPreferenceTreeClick(preference);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeActivityTitle(getString(R.string.settings));
    }

    void changeActivityTitle(CharSequence title) {
        if (getActivity() != null) {
            getActivity().setTitle(title);
        }
    }

    @Nullable
    FrameLayout getSettingActivity2ndFl() {
        if (getActivity() instanceof SettingsActivity) {
            return ((SettingsActivity) getActivity()).get2ndFl();
        } else {
            return null;
        }
    }
}


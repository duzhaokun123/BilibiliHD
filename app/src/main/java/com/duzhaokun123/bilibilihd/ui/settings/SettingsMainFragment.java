package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.google.android.material.navigation.NavigationView;

public class SettingsMainFragment extends Fragment {

    private NavigationView mNavSettingsMain;

    private Fragment mFragmentSettingsDevelop, mFragmentSettingsLayout, mFragmentSettingsDownload;
    private Fragment mFragmentSettingsUsers, mFragmentAbout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavSettingsMain = view.findViewById(R.id.nav_settings_main);
        mNavSettingsMain.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.users:
                        if (mFragmentSettingsUsers == null) {
                            mFragmentSettingsUsers = new SettingsUsersFragment();
                        }
                        setSecondFrameLayout(mFragmentSettingsUsers, getString(R.string.users));
                        break;
                    case R.id.layout:
                        if (mFragmentSettingsLayout == null) {
                            mFragmentSettingsLayout = new SettingLayoutFragment();
                        }
                        setSecondFrameLayout(mFragmentSettingsLayout, getString(R.string.layout));
                        break;
                    case R.id.download:
                        if (mFragmentSettingsDownload == null) {
                            mFragmentSettingsDownload = new SettingsDownloadFragment();
                        }
                        setSecondFrameLayout(mFragmentSettingsDownload, getString(R.string.download));
                        break;
                    case R.id.develop:
                        if (mFragmentSettingsDevelop == null) {
                            mFragmentSettingsDevelop = new SettingsDevelopFragment();
                        }
                        setSecondFrameLayout(mFragmentSettingsDevelop, getString(R.string.develop));
                        break;
                    case R.id.about:
                        if (mFragmentAbout == null) {
                            mFragmentAbout = new AboutFragment();
                        }
                        setSecondFrameLayout(mFragmentAbout, getString(R.string.about));
                }
                return false;
            }
        });
        if (Settings.isUninited()) {
            mNavSettingsMain.addHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.layout_settings_manager_uninited_warning, null, false));
        }
    }

    private void setSecondFrameLayout(Fragment fragment, String name) {
        if (getActivity() != null) {
            getActivity().setTitle(name);
            if (((SettingsActivity) getActivity()).getFlSettingSecond() == null) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(getActivity().getSupportFragmentManager().findFragmentByTag("main")).replace(R.id.fl_settings_first, fragment).addToBackStack(name).commitAllowingStateLoss();
            } else {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fl_settings_second, fragment).commitAllowingStateLoss();
            }
        }
    }
}

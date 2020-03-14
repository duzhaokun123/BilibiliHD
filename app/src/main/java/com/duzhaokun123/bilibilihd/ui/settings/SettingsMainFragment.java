package com.duzhaokun123.bilibilihd.ui.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.io.File;

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
                        setSecondFrameLayout(mFragmentSettingsUsers, "users");
                        break;
                    case R.id.layout:
                        if (mFragmentSettingsLayout == null) {
                            mFragmentSettingsLayout = new SettingLayoutFragment();
                        }
                        setSecondFrameLayout(mFragmentSettingsLayout, "layout");
                        break;
                    case R.id.download:
                        if (mFragmentSettingsDownload == null) {
                            mFragmentSettingsDownload = new SettingsDownloadFragment();
                        }
                        setSecondFrameLayout(mFragmentSettingsDownload, "download");
                        break;
                    case R.id.develop:
                        if (mFragmentSettingsDevelop == null) {
                            mFragmentSettingsDevelop = new SettingsDevelopFragment();
                        }
                        setSecondFrameLayout(mFragmentSettingsDevelop, "develop");
                        break;
                    case R.id.about:
                        if (mFragmentAbout == null) {
                            mFragmentAbout = new AboutFragment();
                        }
                        setSecondFrameLayout(mFragmentAbout, "about");
                }
                return false;
            }
        });
    }

    private void setSecondFrameLayout(Fragment fragment, String name) {
        if (getActivity() != null) {
            if (((SettingsActivity) getActivity()).getmFlSettingSecond() == null) {
                getActivity().getSupportFragmentManager().beginTransaction().hide(getActivity().getSupportFragmentManager().findFragmentByTag("main")).replace(R.id.fl_settings_first, fragment).addToBackStack(name).commitAllowingStateLoss();
            } else {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_settings_second, fragment).commitAllowingStateLoss();
            }
        }
    }
}

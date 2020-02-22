package com.duzhaokun123.bilibilihd.ui.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.duzhaokun123.bilibilihd.R;

/**
 * FIXME:写的太恶心了，想办法重写
 */
public class SettingsActivity extends AppCompatActivity {

    private Fragment mFragmentSettingFirst, mFragmentSettingSecond;

    private FragmentManager fragmentManager;

    private FrameLayout mFlSettingSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        try {
            mFlSettingSecond = findViewById(R.id.fl_settings_second);
        } catch (IllegalArgumentException e) {

        }

        mFragmentSettingFirst = new SettingsMainFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fl_settings_first, mFragmentSettingFirst, "main").commitAllowingStateLoss();

    }

    public FrameLayout getmFlSettingSecond() {
        return mFlSettingSecond;
    }
}

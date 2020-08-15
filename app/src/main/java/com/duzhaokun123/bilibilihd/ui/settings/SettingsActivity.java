package com.duzhaokun123.bilibilihd.ui.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Message;
import android.widget.FrameLayout;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivitySettingsBinding;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding> {
    private boolean first = true;

    @Override
    protected int initConfig() {
        return FIX_LAYOUT;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onRestoreInstanceStateSynchronously(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceStateSynchronously(savedInstanceState);
        first = savedInstanceState.getBoolean("first", true);
    }

    @Override
    public void initView() {
        Fragment mFragmentSettingFirst = new SettingsMainFragment();

        if (first) {
            first = false;
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_settings_first, mFragmentSettingFirst, "main")
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void initData() {

    }

    @Nullable
    @Override
    protected CoordinatorLayout initRegisterCoordinatorLayout() {
        return baseBind.clRoot;
    }

    public FrameLayout get2ndFl() {
        return baseBind.flSettings2nd;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("first", first);
    }
}

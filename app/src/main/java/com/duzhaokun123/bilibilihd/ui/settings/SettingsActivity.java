package com.duzhaokun123.bilibilihd.ui.settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Message;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivitySettingsBinding;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding> {

    public static int WHAT_CHANGE_TITLE = 0;
    public static String BUNDLE_KEY_TITLE = "title";

    @Override
    protected int initConfig() {
        return FIX_LAYOUT | NEED_HANDLER;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_settings;
    }

    @Override
    public void initView() {
        Fragment mFragmentSettingFirst = new SettingsMainFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_settings_first, mFragmentSettingFirst, "main")
                .commitAllowingStateLoss();
    }

    @Override
    public void initData() {

    }

//    public FrameLayout getFlSettingSecond() {
//        return baseBind.flSettingsSecond;
//    }

    @Override
    public void onBackPressed() {
        setTitle(R.string.settings);
        super.onBackPressed();
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        if (msg.what == WHAT_CHANGE_TITLE) {
            setTitle(msg.getData().getString(BUNDLE_KEY_TITLE));
        }
    }
}

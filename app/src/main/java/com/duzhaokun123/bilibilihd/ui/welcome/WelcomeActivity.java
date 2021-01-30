package com.duzhaokun123.bilibilihd.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.bases.BaseActivity2;
import com.duzhaokun123.bilibilihd.databinding.ActivityWelcomeBinding;
import com.duzhaokun123.bilibilihd.ui.main.MainActivity;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.hiczp.bilibili.api.app.model.SplashList;

import java.util.HashSet;
import java.util.Set;

// TODO: 20-12-2 重写
public class WelcomeActivity extends BaseActivity2<ActivityWelcomeBinding> {

    private SplashList splashList;

    @NonNull
    @Override
    public Set<Config> initConfig() {
        Set<Config> config = new HashSet<>();
        config.add(Config.NEED_HANDLER);
        config.add(Config.TRANSPARENT_ACTION_BAR);
        return config;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Settings.layout.isDisableWelcome()) {
            startMainActivity();
        }
    }

    @Override
    public void initView() {
        if (Settings.layout.isDisableWelcome()) {
            return;
        }
        if (splashList == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl, new WelcomeFragment()).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl, new WelcomeAdFragment(splashList)).commitAllowingStateLoss();
        }
    }

    @Override
    public void initData() {
        if (Settings.layout.isDisableWelcome()) {
            return;
        }
        if (Settings.ads.shouldShowWelcomeAd()) {
            new Thread(() -> {
                try {
                    splashList = Application.getPBilibiliClient().getPAppAPI().splashList();
                    if (getHandler() != null) {
                        getHandler().sendEmptyMessage(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> TipUtil.showTip(WelcomeActivity.this, e.getMessage()));
                    if (getHandler() != null) {
                        getHandler().sendEmptyMessage(1);
                    }
                }
            }).start();
        } else {
            if (getHandler() != null) {
                getHandler().sendEmptyMessageDelayed(1, 2000);
            }
        }
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 1:
                startMainActivity();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl, new WelcomeAdFragment(splashList)).commitAllowingStateLoss();
                break;
        }
    }

    void startMainActivity() {
        if (getHandler() != null) {
            getHandler().removeCallbacksAndMessages(null);
        }
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

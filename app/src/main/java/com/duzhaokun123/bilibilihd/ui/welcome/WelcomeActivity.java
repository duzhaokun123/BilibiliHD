package com.duzhaokun123.bilibilihd.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityWelcomeBinding;
import com.duzhaokun123.bilibilihd.ui.main.MainActivity;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.hiczp.bilibili.api.app.model.SplashList;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding> {

    private SplashList splashList;

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
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
                    if (handler != null) {
                        handler.sendEmptyMessage(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> TipUtil.showTip(WelcomeActivity.this, e.getMessage()));
                    if (handler != null) {
                        handler.sendEmptyMessage(1);
                    }
                }
            }).start();
        } else {
            if (handler != null) {
                handler.sendEmptyMessageDelayed(1, 2000);
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
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

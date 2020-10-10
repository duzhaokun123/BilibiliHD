package com.duzhaokun123.bilibilihd.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityWelcomeBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.welcomeAd.WelcomeAdApi;
import com.duzhaokun123.bilibilihd.mybilibiliapi.welcomeAd.model.WelcomeAd;
import com.duzhaokun123.bilibilihd.ui.main.MainActivity;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.TipUtil;

import org.jetbrains.annotations.NotNull;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding> {

    private WelcomeAd welcomeAd;

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
        if (welcomeAd == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl, new WelcomeFragment()).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl, new WelcomeAdFragment(welcomeAd)).commitAllowingStateLoss();
        }
    }

    @Override
    public void initData() {
        if (Settings.layout.isDisableWelcome()) {
            return;
        }
        if (Settings.ads.shouldShowWelcomeAd()) {
            new Thread() {
                @Override
                public void run() {
                    WelcomeAdApi.getInstance().getWelcomeAd(new MyBilibiliClient.ICallback<WelcomeAd>() {
                        @Override
                        public void onException(@NotNull Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> TipUtil.showToast(e.getMessage()));
                            if (handler != null) {
                                handler.sendEmptyMessageDelayed(1, 2000);
                            }
                        }

                        @Override
                        public void onSuccess(@NotNull WelcomeAd welcomeAd) {
                            WelcomeActivity.this.welcomeAd = welcomeAd;
                            if (handler != null) {
                                handler.sendEmptyMessage(2);
                            }
                        }
                    });
                }
            }.start();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fl, new WelcomeAdFragment(welcomeAd)).commitAllowingStateLoss();
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

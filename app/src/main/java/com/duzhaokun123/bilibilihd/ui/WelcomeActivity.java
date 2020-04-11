package com.duzhaokun123.bilibilihd.ui;

import android.content.Intent;

import androidx.appcompat.app.AppCompatDelegate;

import com.arthenica.mobileffmpeg.Config;
import com.duzhaokun123.bilibilihd.BuildConfig;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityWelcomeBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.main.MainActivity;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.NotificationUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.google.android.exoplayer2.util.Log;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding> {

    private PBilibiliClient pBilibiliClient;

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initView() {
        baseBind.tvVersion.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public void initData() {
        pBilibiliClient = PBilibiliClient.Companion.getInstance();
        if (Settings.isUninited()) {
            new Thread() {
                @Override
                public void run() {
                    Settings.init(getApplicationContext());
                    LoginResponse loginResponse = Settings.getLoginUserInfoMap(WelcomeActivity.this).getLoggedLoginResponse();
                    if (loginResponse != null) {
                        pBilibiliClient.getBilibiliClient().setLoginResponse(loginResponse);
                    }
//                    Config.enableLogCallback(message -> Log.d(Config.TAG, message.getText()));
                    if (Settings.isFirstStart()) {
                        NotificationUtil.init(getApplicationContext());
                        Settings.setFirstStart(false);
                    }
                    AppCompatDelegate.setDefaultNightMode(Settings.layout.getUiMode());
                }
            }.start();
            if (handler != null) {
                handler.postDelayed(() -> {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000);
            }
        } else {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

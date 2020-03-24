package com.duzhaokun123.bilibilihd.ui;

import android.content.Intent;

import com.duzhaokun123.bilibilihd.BuildConfig;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityWelcomeBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.main.MainActivity;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.Settings;
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
        new Thread() {
            @Override
            public void run() {
                Settings.init(getApplicationContext());
                LoginResponse loginResponse = Settings.getLoginUserInfoMap(WelcomeActivity.this).getLoggedLoginResponse();
                if (loginResponse != null) {
                    pBilibiliClient.getBilibiliClient().setLoginResponse(loginResponse);
                }
            }
        }.start();
        if (handler != null) {
            handler.postDelayed(() -> {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 2000);
        }
    }
}

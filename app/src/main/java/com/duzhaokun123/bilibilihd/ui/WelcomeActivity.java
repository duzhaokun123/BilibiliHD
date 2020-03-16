package com.duzhaokun123.bilibilihd.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.duzhaokun123.bilibilihd.BuildConfig;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.main.MainActivity;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

public class WelcomeActivity extends AppCompatActivity {

    private PBilibiliClient pBilibiliClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        TextView mTvVersion = findViewById(R.id.tv_version);
        mTvVersion.setText(BuildConfig.VERSION_NAME);
        pBilibiliClient = PBilibiliClient.Companion.getInstance();

        new Thread() {
            @Override
            public void run() {
                SettingsManager.init(getApplicationContext());
                LoginResponse loginResponse = SettingsManager.getInstance().getLoginUserInfoMap(WelcomeActivity.this).getLoggedLoginResponse();
                if (loginResponse != null) {
                    pBilibiliClient.getBilibiliClient().setLoginResponse(loginResponse);
                }
            }
        }.start();

         new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}

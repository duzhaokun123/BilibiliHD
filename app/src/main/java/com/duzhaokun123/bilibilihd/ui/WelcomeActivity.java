package com.duzhaokun123.bilibilihd.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.main.MainActivity;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

public class WelcomeActivity extends AppCompatActivity {

    private PBilibiliClient pBilibiliClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        TextView mTvVersion = findViewById(R.id.tv_version);
        try {
            mTvVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();

        new Thread() {
            @Override
            public void run() {
                SettingsManager.init(getApplicationContext());
                LoginResponse loginResponse = SettingsManager.getSettingsManager().getLoginUserInfoMap(WelcomeActivity.this).getLoggedLoginResponse();
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

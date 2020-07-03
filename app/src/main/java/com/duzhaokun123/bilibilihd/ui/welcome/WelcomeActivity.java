package com.duzhaokun123.bilibilihd.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityWelcomeBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.welcomeAd.WelcomeAdApi;
import com.duzhaokun123.bilibilihd.mybilibiliapi.welcomeAd.model.WelcomeAd;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.main.MainActivity;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.NotificationUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding> {

    private PBilibiliClient pBilibiliClient;
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
    public void onRestoreInstanceStateSynchronously(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceStateSynchronously(savedInstanceState);
        welcomeAd = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("welcomeAd"), WelcomeAd.class);
    }

    @Override
    public void initView() {
        if (welcomeAd == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl, new WelcomeFragment()).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl, new WelcomeAdFragment(welcomeAd)).commitAllowingStateLoss();
        }
    }

    @Override
    public void initData() {
        pBilibiliClient = PBilibiliClient.Companion.getInstance();
        if (Settings.isUninited()) {
            new Thread() {
                @Override
                public void run() {
                    Settings.init(getApplicationContext());
                    pBilibiliClient.setLoginResponse(Settings.getLoginUserInfoMap().getLoggedLoginResponse());
                    BrowserUtil.syncLoggedLoginResponse();
//                    Config.enableLogCallback(message -> Log.d(Config.TAG, message.getText()));
                    if (Settings.isFirstStart()) {
                        NotificationUtil.init(getApplicationContext());
                        Settings.setFirstStart(false);
                    }
                    AppCompatDelegate.setDefaultNightMode(Settings.layout.getUiMode());

                    if (Settings.ads.shouldShowWelcomeAd()) {
                        WelcomeAdApi.getInstance().getWelcomeAd(new MyBilibiliClient.ICallback<WelcomeAd>() {
                            @Override
                            public void onException(Exception e) {
                                e.printStackTrace();
                                runOnUiThread(() -> ToastUtil.sendMsg(WelcomeActivity.this, e.getMessage()));
                                if (handler != null) {
                                    handler.sendEmptyMessageDelayed(1, 2000);
                                }
                            }

                            @Override
                            public void onSuccess(WelcomeAd welcomeAd) {
                                WelcomeActivity.this.welcomeAd = welcomeAd;
                                if (handler != null) {
                                    handler.sendEmptyMessage(2);
                                }
                            }
                        });
                    } else {
                        if (handler != null) {
                            handler.sendEmptyMessageDelayed(1, 2000);
                        }
                    }

                }
            }.start();
        } else if (welcomeAd == null && handler != null) {
            handler.sendEmptyMessage(1);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("welcomeAd", GsonUtil.getGsonInstance().toJson(welcomeAd));
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

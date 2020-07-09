package com.duzhaokun123.bilibilihd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.NotificationUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

public class Application extends android.app.Application {
    private static Application application;

    @NonNull
    public static Application getInstance() {
        return application;
    }

    public Application() {
        super();
        application = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Settings.init(this);
        LoginResponse loginResponse = Settings.getLoginUserInfoMap().getLoggedLoginResponse();
        PBilibiliClient.Companion.getInstance().setLoginResponse(loginResponse);
//                    Config.enableLogCallback(message -> Log.d(Config.TAG, message.getText()));
        if (Settings.isFirstStart()) {
            NotificationUtil.init(getApplicationContext());
            Settings.setFirstStart(false);
        }
        AppCompatDelegate.setDefaultNightMode(Settings.layout.getUiMode());
    }
}

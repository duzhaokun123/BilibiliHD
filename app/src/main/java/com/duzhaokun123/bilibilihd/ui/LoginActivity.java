package com.duzhaokun123.bilibilihd.ui;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.pbilibiliapi.utils.BilibiliApiExceptionUtil;
import com.duzhaokun123.bilibilihd.utils.GeetestUtil;
import com.duzhaokun123.bilibilihd.utils.LoginUserInfoMap;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

public class LoginActivity extends MyBaseActivity {

    private EditText mEtUsername, mEtPassword;
    private Button mBtnLogin;

    private PBilibiliClient pBilibiliClient = PBilibiliClient.Companion.getInstance();
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        handler = new Handler();

        mEtUsername = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        LoginResponse loginResponse = null;
                        try {
                            loginResponse = pBilibiliClient.login(mEtUsername.getText().toString(), mEtPassword.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e instanceof BilibiliApiException && ((BilibiliApiException) e).getCommonResponse().getCode() == -105) {
                                Log.d("LoginActivity", "here");
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("url", BilibiliApiExceptionUtil.Companion.getGeetestUrl((BilibiliApiException) e));
                                message.what = 0;
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                            Looper.prepare();
                            ToastUtil.sendMsg(LoginActivity.this, e.getMessage());
                            Looper.loop();
                        }

                        if (loginResponse != null) {
                            if (loginResponse.getData().getUrl() == null) {
                                SettingsManager settingsManager = SettingsManager.getInstance();
                                LoginUserInfoMap loginUserInfoMap = settingsManager.getLoginUserInfoMap(LoginActivity.this);
                                loginUserInfoMap.put(loginResponse.getUserId(), loginResponse);
                                loginUserInfoMap.setLoggedUid(loginResponse.getUserId());
                                settingsManager.saveLoginUserInfoMap(LoginActivity.this);
                                finish();
                            } else {
                                pBilibiliClient.getBilibiliClient().setLoginResponse(null);
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("url", loginResponse.getData().getUrl());
                                message.what = 0;
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }
                    }
                }.start();
            }
        });
    }

    class Handler extends android.os.Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    GeetestUtil.doTest(LoginActivity.this, msg.getData().getString("url"));
                    break;
            }
        }
    }
}

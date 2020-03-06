package com.duzhaokun123.bilibilihd.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.pbilibiliapi.strings.BilibiliApiExceptionStrings;
import com.duzhaokun123.bilibilihd.pbilibiliapi.utils.BilibiliApiExceptionUtil;
import com.duzhaokun123.bilibilihd.utils.GeetestUtil;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.hiczp.bilibili.api.passport.model.LoginResponse;
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class LoginActivity extends AppCompatActivity {

    private EditText mEtUsername, mEtPassword;
    private Button mBtnLogin;

    private PBilibiliClient pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtUsername = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean re = false;
                if (mEtUsername.getText().toString().equals("") && mEtPassword.getText().toString().equals("")) {
                    re = OtherUtils.loadLoginResponse(LoginActivity.this, pBilibiliClient);
                }
                if (!re) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LoginResponse loginResponse = null;
                            try {
                                loginResponse = pBilibiliClient.login(mEtUsername.getText().toString(), mEtPassword.getText().toString());
                            } catch (BilibiliApiException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                ToastUtil.sendMsg(LoginActivity.this, e.getMessage());
                                Looper.loop();
                                if (e.getMessage().equals(BilibiliApiExceptionStrings.VERIFICATION_CODE_ERROR)) {
                                    GeetestUtil.doTest(LoginActivity.this, BilibiliApiExceptionUtil.Companion.getGeetestUrl(e));
                                }
                            }

                            FileOutputStream fileOutputStream = null;
                            ObjectOutputStream objectOutputStream = null;

                            if (loginResponse != null) {
                                try {
                                    fileOutputStream = openFileOutput("LoginResponse", MODE_PRIVATE);
                                    objectOutputStream = new ObjectOutputStream(fileOutputStream);
                                    objectOutputStream.writeObject(loginResponse);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (objectOutputStream != null) {
                                            objectOutputStream.close();
                                        }
                                        if (fileOutputStream != null) {
                                            fileOutputStream.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                finish();
                            }
                        }
                    }).start();
                } else {
                    finish();
                }

            }
        });
    }
}

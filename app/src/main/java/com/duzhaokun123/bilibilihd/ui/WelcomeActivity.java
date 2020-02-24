package com.duzhaokun123.bilibilihd.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.main.MainActivity;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class WelcomeActivity extends AppCompatActivity {

    private PBilibiliClient pBilibiliClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SettingsManager.init(WelcomeActivity.this);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                LoginResponse loginResponse = null;

                FileInputStream fileInputStream = null;
                ObjectInputStream objectInputStream = null;
                try {
                    fileInputStream = openFileInput("LoginResponse");
                    objectInputStream = new ObjectInputStream(fileInputStream);
                    loginResponse = (LoginResponse) objectInputStream.readObject();
                } catch (ClassNotFoundException|IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (objectInputStream != null) {
                            objectInputStream.close();
                        }
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (loginResponse != null) {
                    pBilibiliClient.getBilibiliClient().setLoginResponse(loginResponse);
                }
            }
        }).start();


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

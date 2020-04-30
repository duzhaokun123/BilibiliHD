package com.duzhaokun123.bilibilihd.ui;

import androidx.appcompat.app.AlertDialog;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityLogoutBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.hiczp.bilibili.api.BilibiliClient;
import com.hiczp.bilibili.api.passport.model.LoginResponse;

public class LogoutActivity extends BaseActivity<ActivityLogoutBinding> {

    @Override
    protected int initConfig() {
        return FIX_LAYOUT;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_logout;
    }

    @Override
    protected void initView() {
        baseBind.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.logout)
                    .setIcon(R.drawable.ic_warning)
                    .setMessage(R.string.logout_ask)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        new Thread() {
                            @Override
                            public void run() {
                                BilibiliClient bilibiliClient = PBilibiliClient.Companion.getInstance().getBilibiliClient();
                                LoginResponse tmp = bilibiliClient.getLoginResponse();
                                bilibiliClient.setLoginResponse(Settings.getLoginUserInfoMap(LogoutActivity.this).get(Long.valueOf(baseBind.etUid.getText().toString())));
                                runOnUiThread(() -> ToastUtil.sendMsg(LogoutActivity.this, R.string.logged_out));
                                try {
                                    PBilibiliClient.Companion.getInstance().logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    runOnUiThread(() -> ToastUtil.sendMsg(LogoutActivity.this, e.getMessage()));
                                }
                                bilibiliClient.setLoginResponse(tmp);
                            }
                        }.start();
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        });
    }

    @Override
    protected void initData() {

    }
}

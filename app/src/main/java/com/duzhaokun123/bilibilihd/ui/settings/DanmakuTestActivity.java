package com.duzhaokun123.bilibilihd.ui.settings;

import android.content.Intent;
import android.view.View;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.databinding.ActivityDanmakuTestBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity;
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;

import java.io.BufferedInputStream;
import java.util.Map;

import kotlin.Pair;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import okhttp3.ResponseBody;

public class DanmakuTestActivity extends BaseActivity<ActivityDanmakuTestBinding> {
    private DanmakuContext danmakuContext;

    @Override
    protected int initConfig() {
        return FULLSCREEN;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_danmaku_test;
    }

    @Override
    protected void initView() {
        baseBind.rl.setOnClickListener(view -> {
            if (baseBind.flDanmakuSettings.getVisibility() == View.VISIBLE) {
                baseBind.flDanmakuSettings.setVisibility(View.INVISIBLE);
            } else {
                baseBind.flDanmakuSettings.setVisibility(View.VISIBLE);
            }
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_danmaku_settings, new SettingsDanmakuFragment())
                .commitAllowingStateLoss();
        danmakuContext = DanmakuContext.create();
        DanmakuUtil.INSTANCE.syncDanmakuSettings(danmakuContext, this);
        baseBind.dv.enableDanmakuDrawingCache(true);
        baseBind.dv.showFPS(true);

        new Thread() {
            @Override
            public void run() {
                ResponseBody responseBody = null;
                BaseDanmakuParser parser;
                try {
                    responseBody = PBilibiliClient.Companion.getInstance().getPDanmakuAPI().list(61733031, 107356773);
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> ToastUtil.sendMsg(DanmakuTestActivity.this, "无法加载弹幕\n" + e.getMessage()));
                }
                if (responseBody != null) {
                    Pair<Map<Long, Integer>, BufferedInputStream> pair = DanmakuUtil.INSTANCE.toInputStream(responseBody.byteStream());
                    parser = PlayActivity.createParser(pair.getSecond());
                    BaseDanmakuParser finalParser = parser;
                    runOnUiThread(() -> {
                        baseBind.dv.prepare(finalParser, danmakuContext);
                        ToastUtil.sendMsg(DanmakuTestActivity.this, "加载成功");
                    });
                }
            }
        }.start();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (danmakuContext != null) {
            DanmakuUtil.INSTANCE.syncDanmakuSettings(danmakuContext, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseBind.dv.release();
    }
}
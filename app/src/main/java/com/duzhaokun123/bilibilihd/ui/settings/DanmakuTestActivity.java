package com.duzhaokun123.bilibilihd.ui.settings;

import android.content.Intent;
import android.view.View;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityDanmakuTestBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.ui.widget.BiliPlayerView;
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil;
import com.duzhaokun123.bilibilihd.utils.TipUtil;

import java.io.BufferedInputStream;
import java.util.Map;

import kotlin.Pair;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import okhttp3.ResponseBody;

public class DanmakuTestActivity extends BaseActivity<ActivityDanmakuTestBinding> {
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser mParser;

    private long aid = 61733031;
    private long cid = 107356773;

    @Override
    protected int initConfig() {
        return FIX_LAYOUT;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_danmaku_test;
    }

    @Override
    public void initView() {
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

        danmakuContext = DanmakuUtil.INSTANCE.getDanmakuContext();
        baseBind.dv.enableDanmakuDrawingCache(true);
        baseBind.dv.showFPS(true);
    }

    @Override
    protected void initData() {
        new Thread() {
            @Override
            public void run() {
                ResponseBody responseBody = null;
                try {
                    responseBody = PBilibiliClient.Companion.getInstance().getPDanmakuAPI().list(aid, cid);
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> TipUtil.showToast("无法加载弹幕\n" + e.getMessage()));
                }
                if (responseBody != null) {
                    Pair<Map<Long, Integer>, BufferedInputStream> pair = DanmakuUtil.INSTANCE.toInputStream(responseBody.byteStream());
                    mParser = BiliPlayerView.createParser(pair.getSecond());
                    runOnUiThread(() -> {
                        baseBind.dv.prepare(mParser, danmakuContext);
                        TipUtil.showToast("加载成功");
                    });
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> baseBind.dv.start());
            }
        }.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        DanmakuUtil.INSTANCE.syncDanmakuSettings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseBind.dv.release();
    }
}

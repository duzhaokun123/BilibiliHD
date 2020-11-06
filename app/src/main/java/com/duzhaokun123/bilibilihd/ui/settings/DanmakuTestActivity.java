package com.duzhaokun123.bilibilihd.ui.settings;

import android.content.Intent;
import android.view.View;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.databinding.ActivityDanmakuTestBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.DanmakuAPI;
import com.duzhaokun123.bilibilihd.proto.BiliDanmaku;
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil;
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser.EmptyBiliDanmakuParser;
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser.ProtobufBiliDanmakuParser;
import com.duzhaokun123.bilibilihd.utils.TipUtil;

public class DanmakuTestActivity extends BaseActivity<ActivityDanmakuTestBinding> {
    private static final long aid = 61733031;
    private static final long cid = 107356773;

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

        baseBind.dv.enableDanmakuDrawingCache(true);
        baseBind.dv.showFPS(true);
        baseBind.dv.prepare(EmptyBiliDanmakuParser.INSTANCE, DanmakuUtil.INSTANCE.getDanmakuContext());
    }

    @Override
    protected void initData() {
        new Thread(() -> {
            BiliDanmaku.DmSegMobileReply[] dmSegMobileReply = new BiliDanmaku.DmSegMobileReply[1];
            try {
                dmSegMobileReply[0] = DanmakuAPI.INSTANCE.getBiliDanmaku(aid, cid, 1, 1);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> TipUtil.showToast("无法加载弹幕\n" + e.getMessage()));
            }
            if (dmSegMobileReply[0] != null) {
                baseBind.dv.release();
                baseBind.dv.prepare(new ProtobufBiliDanmakuParser(dmSegMobileReply), DanmakuUtil.INSTANCE.getDanmakuContext());
                runOnUiThread(() -> TipUtil.showToast("加载成功"));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> baseBind.dv.start());
        }).start();
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

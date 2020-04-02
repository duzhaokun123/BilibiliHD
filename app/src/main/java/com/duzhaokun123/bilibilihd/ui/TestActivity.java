package com.duzhaokun123.bilibilihd.ui;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityTestBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.DownloadUtil;

import java.io.File;

public class TestActivity extends BaseActivity<ActivityTestBinding> {
    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        File file = new File(getCacheDir(), "out.mp4");
        if (file.exists() && !file.delete()) {
            baseBind.tvContent.setText("无法删除文件");
        }
        baseBind.btnStart.setOnClickListener(v -> {
            baseBind.vv.setVideoPath(file.getPath());
            baseBind.vv.start();
        });
        baseBind.btnDownload.setOnClickListener(v -> DownloadUtil.downloadVideo(this, baseBind.etVideo.getText().toString(), baseBind.etAudio.getText().toString(), baseBind.etDanmaku.getText().toString(), "测试", "test"));
    }

    @Override
    protected void initData() {

    }
}

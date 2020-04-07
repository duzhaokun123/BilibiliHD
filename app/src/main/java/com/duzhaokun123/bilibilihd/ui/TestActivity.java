package com.duzhaokun123.bilibilihd.ui;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityTestBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.DownloadUtil;

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
        baseBind.btnDownload.setOnClickListener(v -> DownloadUtil.downloadVideo(this,
                baseBind.etVideo.getText().toString(),
                baseBind.etAudio.getText().toString(),
                "测试", "测试1", "test",
                baseBind.cbVideoOnly.isChecked(), 1,
                baseBind.etDanmaku.getText().toString()));
    }

    @Override
    protected void initData() {

    }
}

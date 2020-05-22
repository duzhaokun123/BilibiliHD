package com.duzhaokun123.bilibilihd.ui;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityTestBinding;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;

public class TestActivity extends BaseActivity<ActivityTestBinding> {
    @Override
    protected int initConfig() {
        return FIX_LAYOUT;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        baseBind.btnCustom.setOnClickListener(v -> BrowserUtil.openCustomTab(this, baseBind.et.getText().toString()));
        baseBind.btnActivity.setOnClickListener(v -> BrowserUtil.openWebViewActivity(this, baseBind.et.getText().toString()));
        baseBind.btnDialog.setOnClickListener(v -> BrowserUtil.openWebViewDialog(this, baseBind.et.getText().toString()));
    }

    @Override
    protected void initData() {

    }
}

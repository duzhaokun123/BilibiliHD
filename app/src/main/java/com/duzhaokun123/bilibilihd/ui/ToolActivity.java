package com.duzhaokun123.bilibilihd.ui;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityToolBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;

public class ToolActivity extends BaseActivity<ActivityToolBinding> {

    @Override
    protected int initConfig() {
        return FIX_LAYOUT;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_tool;
    }

    @Override
    protected void initView() {
        baseBind.btnAv2bv.setOnClickListener(v -> {
                    try {
                        baseBind.etBv.setText(MyBilibiliClientUtil.av2bv(Long.parseLong(baseBind.etAv.getText().toString())));
                    } catch (Exception e) {
                        ToastUtil.sendMsg(this, e.getMessage());
                    }
                }
        );
        baseBind.btnBv2av.setOnClickListener(v -> {
                    try {
                        baseBind.etAv.setText(String.valueOf(MyBilibiliClientUtil.bv2av(baseBind.etBv.getText().toString())));
                    } catch (Exception e) {
                        ToastUtil.sendMsg(this, e.getMessage());
                    }
                }
        );
    }

    @Override
    protected void initData() {

    }
}

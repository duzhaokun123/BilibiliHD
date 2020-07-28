package com.duzhaokun123.bilibilihd.ui;

import android.content.Intent;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityToolBinding;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.ui.universal.reply.RootReplyActivity;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.duzhaokun123.bilibilihd.utils.TipUtil;

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
                        TipUtil.showToast(e.getMessage());
                    }
                }
        );
        baseBind.btnBv2av.setOnClickListener(v -> {
                    try {
                        baseBind.etAv.setText(String.valueOf(MyBilibiliClientUtil.bv2av(baseBind.etBv.getText().toString())));
                    } catch (Exception e) {
                        TipUtil.showToast(e.getMessage());
                    }
                }
        );
        baseBind.btnReplyGo.setOnClickListener(v -> {
            Intent intent = new Intent(this, RootReplyActivity.class);
            intent.putExtra(RootReplyActivity.EXTRA_TYPE, Integer.parseInt(baseBind.etType.getText().toString()));
            intent.putExtra(RootReplyActivity.EXTRA_OID, Long.parseLong(baseBind.etOid.getText().toString()));
            startActivity(intent);
        });
    }

    @Override
    protected void initData() {

    }
}

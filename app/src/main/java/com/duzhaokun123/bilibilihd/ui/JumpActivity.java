package com.duzhaokun123.bilibilihd.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityJumpBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.LogUtil;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;

public class JumpActivity extends BaseActivity<ActivityJumpBinding> {

    private Intent intent;

    @Override
    protected int initConfig() {
        return FIX_LAYOUT;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_jump;
    }

    @Override
    public void initView() {
        intent = new Intent();

        baseBind.btnAdd.setOnClickListener(v -> {
            try {
                switch (baseBind.rgChoose.getCheckedRadioButtonId()) {
                    case R.id.rb_int:
                        intent.putExtra(baseBind.etKey.getText().toString(), Integer.parseInt(baseBind.etValue.getText().toString()));
                        break;
                    case R.id.rb_long:
                        intent.putExtra(baseBind.etKey.getText().toString(), Long.parseLong(baseBind.etValue.getText().toString()));
                        break;
                    case R.id.rb_double:
                        intent.putExtra(baseBind.etKey.getText().toString(), Double.parseDouble(baseBind.etValue.getText().toString()));
                        break;
                    case R.id.rb_float:
                        intent.putExtra(baseBind.etKey.getText().toString(), Float.parseFloat(baseBind.etValue.getText().toString()));
                        break;
                    case R.id.rb_boolean:
                        intent.putExtra(baseBind.etKey.getText().toString(), Boolean.parseBoolean(baseBind.etValue.getText().toString()));
                        break;
                    case R.id.rb_string:
                        intent.putExtra(baseBind.etKey.getText().toString(), baseBind.etValue.getText().toString());
                        break;
                }
                Log.d("JumpActivity", baseBind.etKey.getText().toString() + ": " + baseBind.etValue.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.sendMsg(this, e.getMessage());
            }
        });

        baseBind.btnStart.setOnClickListener(v -> {
            Log.d("JumpActivity", "start" + ": " + baseBind.etComponent.getText().toString());
            try {
                Class.forName(baseBind.etComponent.getText().toString());
                intent.setComponent(new ComponentName(JumpActivity.this, baseBind.etComponent.getText().toString()));
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                ToastUtil.sendMsg(this, e.getMessage());
            }
        });
        baseBind.btnSaveLog.setOnClickListener(v -> LogUtil.saveLog(this));
    }

    @Override
    public void initData() {

    }
}

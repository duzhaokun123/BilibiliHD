package com.duzhaokun123.bilibilihd.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityJumpBinding;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.Logcat;
import com.duzhaokun123.bilibilihd.utils.TipUtil;

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
                TipUtil.showToast(e.getMessage());
            }
        });

        baseBind.btnStart.setOnClickListener(v -> {
            Log.d("JumpActivity", "start" + ": " + baseBind.etComponent.getText().toString());
            try {
                Class<?> clazz = Class.forName(baseBind.etComponent.getText().toString());
                if (Activity.class.isAssignableFrom(clazz)) {
                    intent.setComponent(new ComponentName(JumpActivity.this, clazz));
                    startActivity(intent);
                } else {
                    TipUtil.showToast(clazz.getName() + " is not a Activity");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                TipUtil.showToast("class not found " + e.getMessage());
            }
        });
        baseBind.btnSaveLog.setOnClickListener(v -> Logcat.saveLog(this));
    }

    @Override
    public void initData() {

    }
}

package com.duzhaokun123.bilibilihd.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityJumpBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;

public class JumpActivity extends BaseActivity<ActivityJumpBinding> {

    private Intent intent;

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_jump;
    }

    @Override
    public void initView() {
        intent = new Intent();

        baseBind.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("JumpActivity", baseBind.etKey.getText().toString() + ": " + baseBind.etValue.getText().toString());
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
            }
        });

        baseBind.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("JumpActivity", "start" + ": " + baseBind.etComponent.getText().toString());
                intent.setComponent(new ComponentName(JumpActivity.this, baseBind.etComponent.getText().toString()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

    }
}

package com.duzhaokun123.bilibilihd.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.duzhaokun123.bilibilihd.R;

public class JumpActivity extends AppCompatActivity {

    private EditText mEtComponent, mEtKey, mEtValue;
    private Button mBtnAdd, mBtnStart;
    private RadioGroup mRgChoose;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);

        mEtComponent = findViewById(R.id.et_component);
        mEtKey = findViewById(R.id.et_key);
        mEtValue = findViewById(R.id.et_value);
        mBtnAdd = findViewById(R.id.btn_add);
        mBtnStart = findViewById(R.id.btn_start);
        mRgChoose = findViewById(R.id.rg_choose);

        intent = new Intent();

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("JumpActivity", mEtKey.getText().toString() + ": " + mEtValue.getText().toString());
                switch (mRgChoose.getCheckedRadioButtonId()) {
                    case R.id.rb_int:
                        intent.putExtra(mEtKey.getText().toString(), Integer.parseInt(mEtValue.getText().toString()));
                        break;
                    case R.id.rb_long:
                        intent.putExtra(mEtKey.getText().toString(), Long.parseLong(mEtValue.getText().toString()));
                        break;
                    case R.id.rb_double:
                        intent.putExtra(mEtKey.getText().toString(), Double.parseDouble(mEtValue.getText().toString()));
                        break;
                    case R.id.rb_boolean:
                        intent.putExtra(mEtKey.getText().toString(), Boolean.parseBoolean(mEtValue.getText().toString()));
                        break;
                    case R.id.rb_string:
                        intent.putExtra(mEtKey.getText().toString(), mEtValue.getText().toString());
                        break;
                }
            }
        });

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("JumpActivity", "start" + ": " + mEtComponent.getText().toString());
                intent.setComponent(new ComponentName(JumpActivity.this, mEtComponent.getText().toString()));
                startActivity(intent);
            }
        });
    }
}

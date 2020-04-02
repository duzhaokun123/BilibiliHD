package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.FragmentSettingsLayoutBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.Settings;

public class SettingLayoutFragment extends BaseFragment<FragmentSettingsLayoutBinding> {

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_settings_layout;
    }

    @Override
    protected void initView() {
        baseBind.etColumn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Settings.layout.setColumn(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });
        baseBind.etColumn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Settings.layout.setColumnLand(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initData() {
        baseBind.etColumn.setText(String.valueOf(Settings.layout.getColumn()));
        baseBind.etColumnLand.setText(String.valueOf(Settings.layout.getColumnLand()));
    }
}

package com.duzhaokun123.bilibilihd.ui.settings;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatDelegate;

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
        baseBind.rgUiMod.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_night_no:
                    Settings.layout.setUiMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case R.id.rb_night_yes:
                    Settings.layout.setUiMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case R.id.rb_night_follow_system:
                    Settings.layout.setUiMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }
            AppCompatDelegate.setDefaultNightMode(Settings.layout.getUiMode());
        });
    }

    @Override
    protected void initData() {
        baseBind.etColumn.setText(String.valueOf(Settings.layout.getColumn()));
        baseBind.etColumnLand.setText(String.valueOf(Settings.layout.getColumnLand()));
        switch (Settings.layout.getUiMode()) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                baseBind.rbNightNo.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                baseBind.rbNightYes.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                baseBind.rbNightFollowSystem.setChecked(true);
                break;
        }
    }
}

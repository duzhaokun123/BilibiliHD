package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;

public class SettingsDevelopFragment extends Fragment {

    private CheckBox mCbTest;
    private SettingsManager settingsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_develop, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCbTest = view.findViewById(R.id.cb_test);
        settingsManager = SettingsManager.getInstance();
        mCbTest.setChecked(settingsManager.develop.isTest());
        mCbTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsManager.develop.setTest(isChecked);
            }
        });
    }
}

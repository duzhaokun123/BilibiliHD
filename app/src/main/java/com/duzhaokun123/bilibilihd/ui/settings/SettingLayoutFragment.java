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
import com.duzhaokun123.bilibilihd.utils.SettingsManager;

public class SettingLayoutFragment extends Fragment {

    private EditText mEtColumn, mEtColumnLand;

    private SettingsManager settingsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_layout, container, false);
        mEtColumn = view.findViewById(R.id.et_column);
        mEtColumnLand = view.findViewById(R.id.et_columnLand);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsManager = SettingsManager.getInstance();
        mEtColumn.setText(String.valueOf(settingsManager.layout.getColumn()));
        mEtColumnLand.setText(String.valueOf(settingsManager.layout.getColumnLand()));

        mEtColumn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    settingsManager.layout.setColumn(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });
        mEtColumnLand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    settingsManager.layout.setColumnLand(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

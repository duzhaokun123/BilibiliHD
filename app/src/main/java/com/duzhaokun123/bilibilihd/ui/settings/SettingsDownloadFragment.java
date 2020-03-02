package com.duzhaokun123.bilibilihd.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.utils.SettingsManager;

public class SettingsDownloadFragment extends Fragment {

    private RadioGroup mRgDownloader;
    private RadioButton mRbOkhttp, mRbDownloadManager;

    private SettingsManager settingsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_download, container, false);
        mRgDownloader = view.findViewById(R.id.rg_downloader);
        mRbOkhttp = view.findViewById(R.id.rb_okhttp);
        mRbDownloadManager = view.findViewById(R.id.rb_downloadManager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingsManager = SettingsManager.getSettingsManager();
        switch (settingsManager.download.getDownloader()) {
            case SettingsManager.Download.OKHTTP:
                mRbOkhttp.setChecked(true);
                break;
            case SettingsManager.Download.DOWNLOAD_MANAGER:
                mRbDownloadManager.setChecked(true);
                break;
        }
        mRgDownloader.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_okhttp:
                        settingsManager.download.setDownloader(SettingsManager.Download.OKHTTP);
                        break;
                    case R.id.rb_downloadManager:
                        settingsManager.download.setDownloader(SettingsManager.Download.DOWNLOAD_MANAGER);
                        break;
                }
            }
        });
    }
}

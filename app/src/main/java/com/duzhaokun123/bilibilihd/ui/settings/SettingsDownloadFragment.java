package com.duzhaokun123.bilibilihd.ui.settings;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.FragmentSettingsDownloadBinding;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.Settings;

public class SettingsDownloadFragment extends BaseFragment<FragmentSettingsDownloadBinding> {
    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_settings_download;
    }

    @Override
    protected void initView() {

        baseBind.rgDownloader.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_downloadManager:
                    Settings.download.setDownloader(Settings.Download.DOWNLOAD_MANAGER);
                    break;
                case R.id.rb_glideCache:
                    Settings.download.setDownloader(Settings.Download.GLIDE_CACHE_FIRST);
                    break;
            }
        });
    }

    @Override
    protected void initData() {
        switch (Settings.download.getDownloader()) {
            case Settings.Download.DOWNLOAD_MANAGER:
                baseBind.rbDownloadManager.setChecked(true);
                break;
            case Settings.Download.GLIDE_CACHE_FIRST:
                baseBind.rbGlideCache.setChecked(true);
                break;
        }
    }
}

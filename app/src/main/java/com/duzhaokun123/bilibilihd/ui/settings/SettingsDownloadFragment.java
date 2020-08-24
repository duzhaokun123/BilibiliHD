package com.duzhaokun123.bilibilihd.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.utils.Settings;

public class SettingsDownloadFragment extends PreferenceFragmentCompat {

    public static final int REQUEST_OPEN_OFFICIAL_APP_DOWNLOAD_DIR = 0;

    private Preference officialAppDownloadDir;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_download, rootKey);

        officialAppDownloadDir = findPreference("official_app_download_dir");
        assert officialAppDownloadDir != null;
        officialAppDownloadDir.setSummary(Settings.download.getOfficialAppDownloadDir());
        officialAppDownloadDir.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_OPEN_OFFICIAL_APP_DOWNLOAD_DIR);
            return true;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_OPEN_OFFICIAL_APP_DOWNLOAD_DIR && resultCode == Activity.RESULT_OK && data != null) {
            Settings.download.setOfficialAppDownloadDir(data.getDataString());
            officialAppDownloadDir.setSummary(Settings.download.getOfficialAppDownloadDir());
        }
    }
}

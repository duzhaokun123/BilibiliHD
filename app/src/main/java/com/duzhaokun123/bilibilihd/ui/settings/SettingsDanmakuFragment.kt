package com.duzhaokun123.bilibilihd.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil
import com.takisoft.preferencex.PreferenceFragmentCompat

class SettingsDanmakuFragment : PreferenceFragmentCompat() {
    private lateinit var test: Preference
    private lateinit var sync: Preference

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_danamku, rootKey)
        test = findPreference("test")!!
        sync = findPreference("sync")!!

        test.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(context, DanmakuTestActivity::class.java))
            true
        }
        sync.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            DanmakuUtil.syncDanmakuSettings()
            true
        }
    }
}
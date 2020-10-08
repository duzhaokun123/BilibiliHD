package com.duzhaokun123.bilibilihd.ui.settings

import android.os.Bundle
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseFragment
import com.duzhaokun123.bilibilihd.databinding.FragmentSettingsBilibiliApiBinding
import com.duzhaokun123.bilibilihd.utils.Settings
import com.takisoft.preferencex.PreferenceFragmentCompat

@Suppress("unused")
class SettingsBilibiliApiFragment : BaseFragment<FragmentSettingsBilibiliApiBinding>() {
    private val customPreferenceFragment by lazy { CustomPreferenceFragment() }

    override fun initConfig() = 0

    override fun initLayout() = R.layout.fragment_settings_bilibili_api

    override fun initView() {
        baseBind.swCustom.setOnCheckedChangeListener { _, isChecked ->
            Settings.bilibiliApi.isCustom = isChecked
            if (isChecked) {
                childFragmentManager.beginTransaction().replace(R.id.fl, customPreferenceFragment).commitAllowingStateLoss()
            } else {
                childFragmentManager.beginTransaction().remove(customPreferenceFragment).commitAllowingStateLoss()
            }
        }
        baseBind.btnSync.setOnClickListener {
            Application.recreatePBilibiliClient()
            loadBilibiliClientProperties()
        }
    }

    override fun initData() {
        baseBind.swCustom.isChecked = Settings.bilibiliApi.isCustom
        loadBilibiliClientProperties()
    }

    private fun loadBilibiliClientProperties() {
        Application.getPBilibiliClient().apply {
            baseBind.tvHttpLogLevel.text = logLevel.name
        }.bilibiliClientProperties.apply {
            baseBind.tvDefaultUserAgent.text = defaultUserAgent
            baseBind.tvAppKey.text = appKey
            baseBind.tvAppSecret.text = appSecret
            baseBind.tvVideoAppKey.text = videoAppKey
            baseBind.tvVideoAppSecret.text = videoAppSecret
            baseBind.tvPlatform.text = platform
            baseBind.tvChannel.text = channel
            baseBind.tvHardwareId.text = hardwareId
            baseBind.tvScale.text = scale
            baseBind.tvVersion.text = version
            baseBind.tvBuild.text = build
            baseBind.tvBuildVersionId.text = buildVersionId
        }
    }

    class CustomPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_bilibili_api, rootKey)
        }
    }
}
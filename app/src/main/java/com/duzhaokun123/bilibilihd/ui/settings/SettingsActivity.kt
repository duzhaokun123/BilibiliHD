package com.duzhaokun123.bilibilihd.ui.settings

import android.os.Bundle
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.databinding.ActivitySettingsBinding
import com.duzhaokun123.bilibilihd.utils.systemBars

class SettingsActivity : BaseActivity2<ActivitySettingsBinding>() {
    private var first = true
    override fun initConfig() = setOf<Config>()

    override fun initLayout(): Int {
        return R.layout.activity_settings
    }

    override fun onRestoreInstanceState2(savedInstanceState: Bundle) {
        first = savedInstanceState.getBoolean("first", true)
    }

    override fun initView() {
        baseBind.nsv?.setOnScrollChangeListener(NSVAutoSetActionBarUpListener())
        val mFragmentSettingFirst: Fragment = SettingsMainFragment()
        if (first) {
            first = false
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fl_settings_first, mFragmentSettingFirst, "main")
                    .commitAllowingStateLoss()
        }
    }

    override fun initData() {}

    override fun initRegisterCoordinatorLayout() = baseBind.clRoot

    fun get2ndFl() = baseBind.flSettings2nd

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("first", first)
    }

    override fun onApplyWindowInsets(windowInsetsCompat: WindowInsetsCompat) {
        windowInsetsCompat.systemBars.let {
            baseBind.nsv?.updatePadding(top = it.top, bottom = it.bottom)
            if (baseBind.nsv == null) {
                baseBind.clRoot.updatePadding(top = it.top, bottom = it.bottom)
            }
        }
    }
}
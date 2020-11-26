package com.duzhaokun123.bilibilihd.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil
import com.duzhaokun123.bilibilihd.utils.IOUtil
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.takisoft.preferencex.PreferenceFragmentCompat
import com.takisoft.preferencex.SimpleMenuPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class SettingsDanmakuFragment : PreferenceFragmentCompat() {
    companion object {
        const val REQUEST_CODE_OPEN_TTF = 0
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_danamku, rootKey)
        findPreference<Preference>("test")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(context, DanmakuTestActivity::class.java))
            true
        }
        findPreference<Preference>("sync")!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            DanmakuUtil.syncDanmakuSettings()
            true
        }
        findPreference<SimpleMenuPreference>("danmaku_typeface")!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            if (newValue == "5") {
                File(requireContext().filesDir, "font.ttf").delete()
                startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "font/ttf"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }, REQUEST_CODE_OPEN_TTF)
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OPEN_TTF && resultCode == Activity.RESULT_OK) {
            GlobalScope.launch(Dispatchers.IO) {
                val inputStream = requireContext().contentResolver.openInputStream(data!!.data!!)!!
                val outputStream = FileOutputStream(File(requireContext().filesDir, "font.ttf"))
                try {
                    IOUtil.copy(inputStream, outputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    TipUtil.showTip(context, e.message)
                } finally {
                    inputStream.close()
                    outputStream.close()
                }
            }
        }
    }
}
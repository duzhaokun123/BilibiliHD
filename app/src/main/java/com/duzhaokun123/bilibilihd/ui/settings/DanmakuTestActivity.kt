package com.duzhaokun123.bilibilihd.ui.settings

import android.content.Intent
import android.view.View
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.databinding.ActivityDanmakuTestBinding
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.DanmakuAPI.getBiliDanmaku
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser.ProtobufBiliDanmakuParser
import com.duzhaokun123.bilibilihd.proto.BiliDanmaku.DmSegMobileReply
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil.syncDanmakuSettings
import com.duzhaokun123.bilibilihd.utils.TipUtil

class DanmakuTestActivity : BaseActivity2<ActivityDanmakuTestBinding>() {
    override fun initConfig() = setOf(Config.FIX_LAYOUT)

    override fun initLayout() = R.layout.activity_danmaku_test

    override fun initView() {
        baseBind.rl.setOnClickListener { view: View? ->
            if (baseBind.flDanmakuSettings.visibility == View.VISIBLE) {
                baseBind.flDanmakuSettings.visibility = View.INVISIBLE
            } else {
                baseBind.flDanmakuSettings.visibility = View.VISIBLE
            }
        }
        supportFragmentManager.beginTransaction()
                .add(R.id.fl_danmaku_settings, SettingsDanmakuFragment())
                .commitAllowingStateLoss()
        baseBind.dv.drawDebugInfo = true
    }

    override fun initData() {
        Thread {
            val dmSegMobileReply = arrayOfNulls<DmSegMobileReply>(1)
            try {
                dmSegMobileReply[0] = getBiliDanmaku(aid, cid, 1, 1)
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    TipUtil.showToast("""
                        无法加载弹幕
                        ${e.message}
                    """.trimIndent())
                }
            }
            if (dmSegMobileReply[0] != null) {
                baseBind.dv.parse(ProtobufBiliDanmakuParser(dmSegMobileReply))
                runOnUiThread { TipUtil.showToast("加载成功") }
            }
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            runOnUiThread { baseBind.dv.start() }
        }.start()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        syncDanmakuSettings()
    }

    override fun onDestroy() {
        super.onDestroy()
        baseBind.dv.destroy()
    }

    companion object {
        private const val aid: Long = 61733031
        private const val cid: Long = 107356773
    }
}
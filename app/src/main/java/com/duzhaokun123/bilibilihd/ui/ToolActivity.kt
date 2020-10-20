package com.duzhaokun123.bilibilihd.ui

import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.duzhaokun123.bilibilihd.databinding.ActivityToolBinding
import com.duzhaokun123.bilibilihd.ui.play.online.OnlinePlayActivity
import com.duzhaokun123.bilibilihd.ui.universal.reply.RootReplyActivity
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity
import com.duzhaokun123.bilibilihd.utils.toIntOrDefault
import okhttp3.internal.toLongOrDefault
import android.content.Intent
import com.duzhaokun123.bilibilihd.utils.Logcat
import java.lang.Exception

class ToolActivity : BaseActivity<ActivityToolBinding>() {
    override fun initConfig() = FIX_LAYOUT

    override fun initLayout() = R.layout.activity_tool

    override fun initView() {
        baseBind.btnAv2bv.setOnClickListener {
            try {
                baseBind.etBv.setText(MyBilibiliClientUtil.av2bv(baseBind.etAv.text.toString().toLong()))
            } catch (e: Exception) {
                TipUtil.showToast(e.message)
            }
        }
        baseBind.btnBv2av.setOnClickListener {
            try {
                baseBind.etAv.setText(MyBilibiliClientUtil.bv2av(baseBind.etBv.text.toString()).toString())
            } catch (e: Exception) {
                TipUtil.showToast(e.message)
            }
        }
        baseBind.btnReplyGo.setOnClickListener {
            val intent = Intent(this, RootReplyActivity::class.java)
            intent.putExtra(RootReplyActivity.EXTRA_TYPE, baseBind.etType.text.toString().toInt())
            intent.putExtra(RootReplyActivity.EXTRA_OID, baseBind.etOid.text.toString().toLong())
            startActivity(intent)
        }
        baseBind.btnUidGo.setOnClickListener {
            UserSpaceActivity.enter(this, baseBind.etUid.text.toString().toLongOrDefault(0))
        }
        baseBind.btnVideoGo.setOnClickListener {
            val intent = Intent(this, OnlinePlayActivity::class.java).apply {
                val videoN = baseBind.etVideoN.text.toString()
                if (videoN.startsWith("BV")) {
                    putExtra(OnlinePlayActivity.EXTRA_BVID, videoN)
                } else {
                    putExtra(OnlinePlayActivity.EXTRA_AID, videoN.toLongOrDefault(0))
                }
                putExtra(OnlinePlayActivity.EXTRA_PAGE, baseBind.etVideoPage.text.toString().toIntOrDefault(1))
            }
            startActivity(intent)
        }
        baseBind.btnSaveLog.setOnClickListener { Logcat.saveLog(this) }
    }

    override fun initData() {}
}
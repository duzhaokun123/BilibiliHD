package com.duzhaokun123.bilibilihd.ui

import android.app.Activity
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.databinding.ActivityToolBinding
import com.duzhaokun123.bilibilihd.ui.play.online.OnlinePlayActivity
import com.duzhaokun123.bilibilihd.ui.universal.reply.RootReplyActivity
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity
import okhttp3.internal.toLongOrDefault
import android.content.Intent
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.ui.play.live.LivePlayActivity
import com.duzhaokun123.bilibilihd.ui.play.season.SeasonPLayActivity
import com.duzhaokun123.bilibilihd.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class ToolActivity : BaseActivity2<ActivityToolBinding>() {
    companion object {
        const val REQUEST_CODE_SAVE_LOG = 0
    }

    override fun initConfig() = setOf<Config>()
    override fun initLayout() = R.layout.activity_tool

    override fun initView() {
        baseBind.nsv.setOnScrollChangeListener(NSVAutoSetActionBarUpListener())
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
        baseBind.btnLiveGo.setOnClickListener {
            LivePlayActivity.enter(this, baseBind.etLiveCid.text.toString().toLong())
        }
        baseBind.btnSsGo.setOnClickListener {
            val intent = Intent(this, SeasonPLayActivity::class.java).apply {
                putExtra(SeasonPLayActivity.EXTRA_SSID, baseBind.etSs.text.toString().toLong())
            }
            startActivity(intent)
        }
        baseBind.btnSaveLog.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/log"
                putExtra(Intent.EXTRA_TITLE, "${System.currentTimeMillis()}.log")
            }, REQUEST_CODE_SAVE_LOG)
        }
    }

    override fun initData() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SAVE_LOG && resultCode == Activity.RESULT_OK && data != null) {
            GlobalScope.launch(Dispatchers.IO) {
                contentResolver.openOutputStream(data.data!!).use {
                    Logcat.saveLog(it!!)
                }
                launch(Dispatchers.Main) { TipUtil.showToast(getString(R.string.saved_to_s, data.dataString)) }
            }
        }
    }

    override fun onApplyWindowInsets(windowInsetsCompat: WindowInsetsCompat) {
        windowInsetsCompat.systemBars.let {
            baseBind.nsv.updatePadding(top = it.top, bottom = it.bottom)
        }
    }
}
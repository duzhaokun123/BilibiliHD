package com.duzhaokun123.bilibilihd.ui.play.live

import android.content.Context
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseDialogBuilder
import com.duzhaokun123.bilibilihd.databinding.DialogLiveDanmakuSendBinding
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.duzhaokun123.bilibilihd.utils.kRunOnUiThread
import com.duzhaokun123.bilibilihd.utils.pBilibiliClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DanmakuSendDialog(context: Context, private val cid: Long, private val uid: Long = pBilibiliClient.uid) : BaseDialogBuilder<DialogLiveDanmakuSendBinding>(context) {
    override fun initConfig() = 0

    override fun initLayout() = R.layout.dialog_live_danmaku_send

    override fun initView() {
        baseBind.btnSend.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    pBilibiliClient.bilibiliClient.liveAPI.sendMessage(cid = cid, mid = uid, message = baseBind.etMessage.text.toString()).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                    kRunOnUiThread { TipUtil.showTip(context, e.message) }
                }
            }
            dismiss()
        }
    }

    override fun initData() {}

    override fun onShow() {
        baseBind.etMessage.requestFocus()
    }
}
package com.duzhaokun123.bilibilihd.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Message
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.Params
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseDialog
import com.duzhaokun123.bilibilihd.databinding.DialogGeetestBinding
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient
import com.duzhaokun123.bilibilihd.mybilibiliapi.combine.CombineAPI
import com.duzhaokun123.bilibilihd.mybilibiliapi.combine.model.Combine
import com.duzhaokun123.bilibilihd.utils.Handler
import com.duzhaokun123.bilibilihd.utils.TipUtil
import java.lang.Exception

class GeetestDialog(context: Context, private val callBackHandler: Handler?) : BaseDialog<DialogGeetestBinding>(context) {
    companion object {
        const val WHAT_GET_COMBINE = 0
        const val WHAT_COMBINE_GETTED = 1
    }

    private var combine: Combine? = null
    private var challenge: String? = null

    override fun initConfig() = NEED_HANDLER

    override fun initLayout() = R.layout.dialog_geetest

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        baseBind.wvGeetest.settings.javaScriptEnabled = true
        baseBind.btnFinish.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(LoginActivity.EXTRA_CHALLENGE, challenge)
            bundle.putString(LoginActivity.EXTRA_VALIDATE, baseBind.etValidate.text.toString())
            val message = Message()
            message.data = bundle
            message.what = LoginActivity.WHAT_GEETEST_PASSED
            callBackHandler?.sendMessage(message)
            dismiss()
        }
    }

    override fun initData() {
        baseBind.wvGeetest.loadUrl(Params.GEETEST_VALIDATOR)
        handler?.sendEmptyMessage(WHAT_GET_COMBINE)
    }

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_GET_COMBINE -> Thread {
                CombineAPI.getCombine(object : MyBilibiliClient.ICallback<Combine> {
                    override fun onException(e: Exception) {
                        e.printStackTrace()
                        Application.runOnUiThread { TipUtil.showToast(e.message) }
                    }

                    override fun onSuccess(t: Combine) {
                        this@GeetestDialog.combine = t
                        handler?.sendEmptyMessage(WHAT_COMBINE_GETTED)
                    }
                })
            }.start()
            WHAT_COMBINE_GETTED -> {
                combine?.data?.result?.let {
                    challenge = it.challenge
                    baseBind.tvChallenge.text = challenge
                    baseBind.tvGt.text = it.gt
                }
            }
        }
    }
}
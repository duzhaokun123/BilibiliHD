package com.duzhaokun123.bilibilihd.ui.login

import android.os.Message
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.databinding.ActivityLoginBinding
import com.duzhaokun123.bilibilihd.utils.Settings
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.duzhaokun123.bilibilihd.utils.kRunOnUiThread
import com.duzhaokun123.bilibilihd.utils.pBilibiliClient
import com.hiczp.bilibili.api.passport.model.LoginResponse
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException

class LoginActivity : BaseActivity2<ActivityLoginBinding>() {
    companion object {
        const val WHAT_FINISH = 0
        const val WHAT_DO_GEETEST = 1
    }

    override fun initConfig() = setOf(Config.NEED_HANDLER, Config.FIX_LAYOUT, Config.TRANSPARENT_ACTION_BAR)

    override fun initLayout() = R.layout.activity_login

    override fun initView() {
        baseBind.btnLogin.setOnClickListener {
            Thread {
                var loginResponse: LoginResponse? = null
                try {
                    loginResponse = pBilibiliClient.login(baseBind.tietUsername.text.toString(), baseBind.tietPassword.text.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (e is BilibiliApiException && e.commonResponse.code == -105) {
                        handler?.sendEmptyMessage(WHAT_DO_GEETEST)
                    }
                    runOnUiThread { TipUtil.showTip(this@LoginActivity, e.message) }
                }
                loginResponse?.let {
                    if (it.data.url.isNullOrEmpty()) {
                        val loginUserInfoMap = Settings.getLoginUserInfoMap()
                        loginUserInfoMap[it.userId] = it
                        loginUserInfoMap.setLoggedUid(it.userId)
                        Settings.saveLoginUserInfoMap()
                        handler?.sendEmptyMessage(WHAT_FINISH)
                    } else {
                        pBilibiliClient.loginResponse = null
                        kRunOnUiThread {
                            TipUtil.showToast("无法登录")
                        }
                    }
                }
            }.start()
        }
    }

    override fun initData() {}

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_FINISH -> finish()
            WHAT_DO_GEETEST -> GeetestDialog(this).show()
        }
    }
}
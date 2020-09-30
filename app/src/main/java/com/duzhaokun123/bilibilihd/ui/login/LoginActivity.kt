package com.duzhaokun123.bilibilihd.ui.login

import android.os.Message
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.ActivityLoginBinding
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient
import com.duzhaokun123.bilibilihd.utils.Settings
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.hiczp.bilibili.api.passport.model.LoginResponse
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    companion object {
        const val WHAT_FINISH = 0
        const val WHAT_DO_GEETEST = 1
    }

    override fun initConfig() = NEED_HANDLER or FIX_LAYOUT

    public override fun initLayout() = R.layout.activity_login

    public override fun initView() {
        baseBind.btnLogin.setOnClickListener {
            Thread {
                var loginResponse: LoginResponse? = null
                try {
                    loginResponse = PBilibiliClient.login(baseBind.etUsername.text.toString(), baseBind.etPassword.text.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (e is BilibiliApiException && e.commonResponse.code == -105) {
                        handler?.sendEmptyMessage(WHAT_DO_GEETEST)
                    }
                    runOnUiThread { TipUtil.showTip(this@LoginActivity, e.message) }
                }
                loginResponse?.let {
                    if (it.data.url == null) {
                        val loginUserInfoMap = Settings.getLoginUserInfoMap()
                        loginUserInfoMap[it.userId] = it
                        loginUserInfoMap.setLoggedUid(it.userId)
                        Settings.saveLoginUserInfoMap()
                        handler?.sendEmptyMessage(WHAT_FINISH)
                    } else {
                        PBilibiliClient.loginResponse = null
                        TipUtil.showToast("无法登录")
                    }
                }
            }.start()
        }
    }

    public override fun initData() {}

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_FINISH -> finish()
            WHAT_DO_GEETEST -> GeetestDialog(this).show()
        }
    }
}
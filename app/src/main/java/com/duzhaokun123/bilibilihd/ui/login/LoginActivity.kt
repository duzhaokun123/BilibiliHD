package com.duzhaokun123.bilibilihd.ui.login

import android.os.Message
import android.util.Log
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.ActivityLoginBinding
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient
import com.duzhaokun123.bilibilihd.utils.Settings
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.hiczp.bilibili.api.passport.model.LoginResponse
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException

class LoginActivity : BaseActivity<ActivityLoginBinding?>() {
    companion object {
        const val WHAT_FINISH = 0
        const val WHAT_DO_GEETEST = 1
        const val WHAT_GEETEST_PASSED = 2

        const val EXTRA_CHALLENGE = "challenge"
        const val EXTRA_VALIDATE = "validate"
    }

    override fun initConfig() = NEED_HANDLER or FIX_LAYOUT

    public override fun initLayout() = R.layout.activity_login

    public override fun initView() {
        baseBind!!.btnLogin.setOnClickListener {
            Thread {
                var loginResponse: LoginResponse? = null
                try {
                    loginResponse = PBilibiliClient.getInstance().login(baseBind!!.etUsername.text.toString(), baseBind!!.etPassword.text.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (e is BilibiliApiException && e.commonResponse.code == -105) {
                        handler?.sendEmptyMessage(WHAT_DO_GEETEST)
                    }
                    runOnUiThread { TipUtil.showTip(this@LoginActivity, e.message) }
                }
                loginResponse?.let {
                    if (loginResponse.data.url == null) {
                        val loginUserInfoMap = Settings.getLoginUserInfoMap()
                        loginUserInfoMap[loginResponse.userId] = loginResponse
                        loginUserInfoMap.setLoggedUid(loginResponse.userId)
                        Settings.saveLoginUserInfoMap()
                        handler?.sendEmptyMessage(WHAT_FINISH)
                    } else {
                        PBilibiliClient.getInstance().setLoginResponse(null)
                        handler?.sendEmptyMessage(WHAT_DO_GEETEST)
                    }
                }
            }.start()
        }
    }

    public override fun initData() {}

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_FINISH -> finish()
            WHAT_DO_GEETEST -> GeetestDialog(this, handler).show()
            WHAT_GEETEST_PASSED ->
                Thread {
                    Log.d(CLASS_NAME, "here1")
                    var loginResponse: LoginResponse? = null
                    try {
                        loginResponse = PBilibiliClient.getInstance().login(
                                baseBind!!.etUsername.text.toString(),
                                baseBind!!.etPassword.text.toString(),
                                msg.data.getString(EXTRA_CHALLENGE, ""),
                                msg.data.getString(EXTRA_VALIDATE, "") + "|jordan",
                                msg.data.getString(EXTRA_VALIDATE, ""))
                        Log.d(CLASS_NAME, "here2")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        runOnUiThread { TipUtil.showTip(this, e.message) }
                    }
                    loginResponse?.let {
                        val loginUserInfoMap = Settings.getLoginUserInfoMap()
                        loginUserInfoMap[loginResponse.userId] = loginResponse
                        loginUserInfoMap.setLoggedUid(loginResponse.userId)
                        Settings.saveLoginUserInfoMap()
                        handler?.sendEmptyMessage(WHAT_FINISH)
                    }
                }.start()
        }
    }
}
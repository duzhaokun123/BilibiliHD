package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.duzhaokun123.bilibilihd.utils.BrowserUtil
import com.hiczp.bilibili.api.BilibiliClient
import com.hiczp.bilibili.api.passport.model.LoginResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PBilibiliClient private constructor() {
    val bilibiliClient by lazy { BilibiliClient() }
    val pAppAPI by lazy { PAppAPI(bilibiliClient.appAPI) }
    val pPlayerAPI by lazy { PPlayerAPI(bilibiliClient.playerAPI) }
    val pMainAPI by lazy { PMainAPI(bilibiliClient.mainAPI) }

    companion object {
        private val pBilibiliClient by lazy { PBilibiliClient() }
        fun getInstance() = pBilibiliClient
    }

    fun logout() {
        GlobalScope.future { bilibiliClient.logout() }.get()
    }

    @Throws(Exception::class)
    fun login(username: String, password: String): LoginResponse? {
        var exception: Exception? = null
        var loginResponse: LoginResponse? = null
        GlobalScope.future {
            try {
                loginResponse = bilibiliClient.login(username, password)
            } catch (e: Exception) {
                exception = e
            }
        }.get()

        if (exception != null) {
            throw exception as Exception
        }

        BrowserUtil.syncLoggedLoginResponse()
        return loginResponse
    }

    @Throws(Exception::class)
    fun login(username: String, password: String, challenge: String, secCode: String, validate: String): LoginResponse? {
        var exception: Exception? = null
        var loginResponse: LoginResponse? = null
        GlobalScope.future {
            try {
                loginResponse = bilibiliClient.login(username, password, challenge, secCode, validate)
            } catch (e: Exception) {
                exception = e
            }
        }.get()

        if (exception != null) {
            throw exception as Exception
        }

        BrowserUtil.syncLoggedLoginResponse()
        return loginResponse
    }

    fun getLoginResponse() = bilibiliClient.loginResponse

    fun setLoginResponse(loginResponse: LoginResponse?) {
        bilibiliClient.loginResponse = loginResponse
        BrowserUtil.syncLoggedLoginResponse()
    }
}
package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.duzhaokun123.bilibilihd.utils.BrowserUtil
import com.hiczp.bilibili.api.BilibiliClient
import com.hiczp.bilibili.api.BilibiliClientProperties
import com.hiczp.bilibili.api.passport.model.LoginResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import okhttp3.logging.HttpLoggingInterceptor

class PBilibiliClient(val bilibiliClientProperties: BilibiliClientProperties,
                      val logLevel: HttpLoggingInterceptor.Level) {
    constructor() : this(object : BilibiliClientProperties {}, HttpLoggingInterceptor.Level.NONE)

    val bilibiliClient by lazy { BilibiliClient(bilibiliClientProperties, logLevel) }
    val pAppAPI by lazy { PAppAPI(bilibiliClient.appAPI) }
    val pPlayerAPI by lazy { PPlayerAPI(bilibiliClient.playerAPI) }
    val pMainAPI by lazy { PMainAPI(bilibiliClient.mainAPI) }
    val pWebAPI by lazy { PWebAPI(bilibiliClient.webAPI) }

    fun logout() {
        GlobalScope.future { bilibiliClient.logout() }.get()
    }

    @Throws(Exception::class)
    fun login(username: String, password: String): LoginResponse? {
        return login(username, password, null, null, null)
    }

    @Throws(Exception::class)
    fun login(username: String, password: String, challenge: String?, secCode: String?, validate: String?): LoginResponse? {
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

    var loginResponse
        get() = bilibiliClient.loginResponse
        set(value) {
            bilibiliClient.loginResponse = value
            BrowserUtil.syncLoggedLoginResponse()
        }

    val uid
        get() = loginResponse?.userId ?: 0

    val isLogin
        get() = bilibiliClient.isLogin
}
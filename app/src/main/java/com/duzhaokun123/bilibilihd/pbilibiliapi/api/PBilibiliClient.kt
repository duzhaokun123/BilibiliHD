package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.duzhaokun123.bilibilihd.model.BilibiliWebCookie
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

    private var bilibiliWebCookie: BilibiliWebCookie? = null

    companion object {
        private val pBilibiliClient by lazy { PBilibiliClient() }
        fun getInstance() = pBilibiliClient
    }

    fun logout() {
        GlobalScope.future { bilibiliClient.logout() }.get()
    }

    @Throws(Exception::class)
    fun login(username: String, password: String): LoginResponse? {
        return login(username, password, null, null, null)
    }

    @Throws(Exception::class)
    fun login(username: String, password: String, challenge: String?, secCode: String?, validate: String?): LoginResponse? {
        bilibiliWebCookie = null
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
        bilibiliWebCookie = null
        bilibiliClient.loginResponse = loginResponse
        BrowserUtil.syncLoggedLoginResponse()
    }

    fun getBilibiliWebCookie(): BilibiliWebCookie? {
        if (bilibiliWebCookie == null && getLoginResponse() != null) {
            var biliJct: String? = null
            var dedeUserID: String? = null
            var dedeUserIDckMd5: String? = null
            var sid: String? = null
            var sessdata: String? = null
            for ((_, _, name, value) in getLoginResponse()!!.data.cookieInfo.cookies) {
                when (name) {
                    "bili_jct" -> biliJct = value
                    "DedeUserID" -> dedeUserID = value
                    "DedeUserID__ckMd5" -> dedeUserIDckMd5 = value
                    "sid" -> sid = value
                    "SESSDATA" -> sessdata = value

                }
            }
            if (biliJct != null && dedeUserID != null && dedeUserIDckMd5 != null && sid != null && sessdata != null) {
                bilibiliWebCookie = BilibiliWebCookie(biliJct, dedeUserID, dedeUserIDckMd5, sid, sessdata)
            }
        }
        return bilibiliWebCookie
    }
}
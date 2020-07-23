package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.duzhaokun123.bilibilihd.utils.BrowserUtil
import com.hiczp.bilibili.api.BilibiliClient
import com.hiczp.bilibili.api.passport.model.LoginResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PBilibiliClient private constructor() {
    private var bilibiliClient: BilibiliClient = BilibiliClient()

    private lateinit var pAppAPI: PAppAPI
    private lateinit var pPlayerAPI: PPlayerAPI
    private lateinit var pMainAPI: PMainAPI

    companion object {
        private lateinit var pBilibiliClient: PBilibiliClient
        fun getInstance(): PBilibiliClient {
            if (::pBilibiliClient.isInitialized.not()) {
                pBilibiliClient = PBilibiliClient()
            }

            return pBilibiliClient
        }
    }

    fun getBilibiliClient() = bilibiliClient

    fun getPAppAPI(): PAppAPI {
        if (::pAppAPI.isInitialized.not()) {
            pAppAPI = PAppAPI(bilibiliClient.appAPI)
        }
        return pAppAPI
    }

    fun getPPlayerAPI(): PPlayerAPI {
        if (::pPlayerAPI.isInitialized.not()) {
            pPlayerAPI = PPlayerAPI(bilibiliClient.playerAPI)
        }
        return pPlayerAPI
    }

    fun getPMainAPI(): PMainAPI {
        if (::pMainAPI.isInitialized.not()) {
            pMainAPI = PMainAPI(bilibiliClient.mainAPI)
        }
        return pMainAPI
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
        getBilibiliClient().loginResponse = loginResponse
        BrowserUtil.syncLoggedLoginResponse()
    }
}
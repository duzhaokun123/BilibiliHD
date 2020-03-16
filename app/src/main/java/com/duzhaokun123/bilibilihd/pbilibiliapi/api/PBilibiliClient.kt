package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.hiczp.bilibili.api.BilibiliClient
import com.hiczp.bilibili.api.passport.model.LoginResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PBilibiliClient private constructor() {
    private var bilibiliClient: BilibiliClient = BilibiliClient()

    private lateinit var pAppAPI: PAppAPI
    private lateinit var pPlayerAPI: PPlayerAPI

    companion object{
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
            }}.get()

        if (exception != null) {
            throw exception as Exception
        }

        return loginResponse
    }

}
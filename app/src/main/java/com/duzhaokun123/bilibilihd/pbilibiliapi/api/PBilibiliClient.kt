package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.hiczp.bilibili.api.BilibiliClient
import com.hiczp.bilibili.api.passport.model.LoginResponse
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PBilibiliClient {
    private var bilibiliClient: BilibiliClient

    private lateinit var pAppAPI: PAppAPI
    private lateinit var pPlayerAPI: PPlayerAPI

    companion object{
        private lateinit var pBilibiliClient: PBilibiliClient
        fun getPBilibiliClient(): PBilibiliClient {
            if (::pBilibiliClient.isInitialized.not()) {
                pBilibiliClient = PBilibiliClient()
            }

            return pBilibiliClient
        }
    }

    private constructor() {
        bilibiliClient = BilibiliClient()

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

    @Throws(BilibiliApiException::class)
    fun login(username: String, password: String): LoginResponse? {
        var bilibiliApiException: BilibiliApiException? = null
        var loginResponse: LoginResponse? = null
        GlobalScope.future {
            try {
                loginResponse = bilibiliClient.login(username, password)
            } catch (e: BilibiliApiException) {
                bilibiliApiException = e
            }}.get()

        if (bilibiliApiException != null) {
            throw bilibiliApiException as BilibiliApiException
        }

        return loginResponse
    }

}
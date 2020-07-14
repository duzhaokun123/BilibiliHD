package com.duzhaokun123.bilibilihd.mybilibiliapi.combine

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient
import com.duzhaokun123.bilibilihd.mybilibiliapi.combine.model.Combine
import com.duzhaokun123.bilibilihd.utils.GsonUtil
import com.hiczp.bilibili.api.retrofit.CommonResponse
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException
import okhttp3.Request
import java.lang.Exception

object CombineAPI {
    fun getCombine(callback: MyBilibiliClient.ICallback<Combine>) {
        val request = Request.Builder()
                .url("https://passport.bilibili.com/web/captcha/combine?plat=6")
                .build()
        try {
            val combine = GsonUtil.getGsonInstance().fromJson(MyBilibiliClient.getInstance().okHttpClient.newCall(request).execute().body?.string(), Combine::class.java)
            if (combine.code != 0) {
                throw BilibiliApiException(CommonResponse(
                        combine.code,
                        null,
                        null,
                        System.currentTimeMillis(),
                        null,
                        null
                ))
            }
            callback.onSuccess(combine)
        } catch (e: Exception) {
            callback.onException(e)
        }
    }
}
package com.duzhaokun123.bilibilihd.mybilibiliapi.shot

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient
import com.duzhaokun123.bilibilihd.mybilibiliapi.shot.model.VideoShot
import com.duzhaokun123.bilibilihd.utils.GsonUtil
import com.hiczp.bilibili.api.retrofit.CommonResponse
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException
import okhttp3.Request
import java.lang.Exception

object ShotAPi {
    fun getShot(aid: Long, cid: Long, callback: MyBilibiliClient.ICallback<VideoShot>) {
        val request = Request.Builder()
                .url("https://api.bilibili.com/x/player/videoshot?aid=$aid&cid=$cid&index=1")
                .build()
        try {
            val videoShot = GsonUtil.getGsonInstance().fromJson(MyBilibiliClient.getInstance().okHttpClient.newCall(request).execute().body?.string(), VideoShot::class.java)
            if (videoShot.code != 0) {
                throw BilibiliApiException(CommonResponse(
                        videoShot.code,
                        videoShot.message,
                        videoShot.message,
                        System.currentTimeMillis(),
                        null,
                        videoShot.ttl
                ))
            }
            callback.onSuccess(videoShot)
        } catch (e: Exception) {
            callback.onException(e)
        }
    }
}
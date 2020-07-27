package com.duzhaokun123.bilibilihd.mybilibiliapi.reply

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient
import com.duzhaokun123.bilibilihd.utils.GsonUtil
import com.hiczp.bilibili.api.retrofit.CommonResponse
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException

object ReplyAPI {
    fun del(type: Int, oid: Long, rpid: Long): CommonResponse {
        val re = GsonUtil.getGsonInstance().fromJson(MyBilibiliClient.getInstance().getResponseByPost(object : MyBilibiliClient.Request {
            override fun getUrl() = "https://api.bilibili.com/x/v2/reply/del"

            override fun addUserParams(paramsMap: MutableMap<String, String>) {
                paramsMap["type"] = type.toString()
                paramsMap["oid"] = oid.toString()
                paramsMap["rpid"] = rpid.toString()
            }

        }), CommonResponse::class.java)
        if (re.code != 0) {
            throw BilibiliApiException(re)
        }
        return re
    }
}
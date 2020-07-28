package com.duzhaokun123.bilibilihd.mybilibiliapi.reply

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient
import com.duzhaokun123.bilibilihd.mybilibiliapi.reply.model.ChildReply
import com.duzhaokun123.bilibilihd.utils.GsonUtil
import com.github.salomonbrys.kotson.fromJson
import com.hiczp.bilibili.api.retrofit.CommonResponse
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException

object ReplyAPI {
    fun del(type: Int, oid: Long, rpid: Long): CommonResponse {
        val re = GsonUtil.getGsonInstance().fromJson<CommonResponse>(MyBilibiliClient.getInstance().getResponseByPost(object : MyBilibiliClient.Request {
            override fun getUrl() = "https://api.bilibili.com/x/v2/reply/del"

            override fun addUserParams(paramsMap: MutableMap<String, String>) {
                paramsMap["type"] = type.toString()
                paramsMap["oid"] = oid.toString()
                paramsMap["rpid"] = rpid.toString()
            }

        }))
        if (re.code != 0) {
            throw BilibiliApiException(re)
        }
        return re
    }

    fun getChildReply(oid: Long, root: Long, type: Int, next: Long? = null): ChildReply {
        val re = GsonUtil.getGsonInstance().fromJson<ChildReply>(MyBilibiliClient.getInstance().getResponseByGet(object : MyBilibiliClient.Request {
            override fun getUrl() = "https://api.bilibili.com/x/v2/reply/detail"

            override fun addUserParams(paramsMap: MutableMap<String, String>) {
                paramsMap["oid"] = oid.toString()
                paramsMap["root"] = root.toString()
                paramsMap["type"] = type.toString()
                paramsMap["next"] = next?.toString() ?: "0"
            }
        }))
        if (re.code != 0) {
            throw BilibiliApiException(GsonUtil.getGsonInstance().fromJson(GsonUtil.getGsonInstance().toJson(re, re::class.java)))
        }
        return re
    }
}
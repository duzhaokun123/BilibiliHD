package com.duzhaokun123.bilibilihd.pBilibiliApi.utils

import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException

class BilibiliApiExceptionUtil {
    companion object {
        fun getGeetestUrl(bilibiliApiException: BilibiliApiException) = bilibiliApiException.commonResponse.data!!.obj.get("url")!!.string
    }
}
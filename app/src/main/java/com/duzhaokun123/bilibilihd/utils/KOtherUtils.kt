package com.duzhaokun123.bilibilihd.utils

import com.duzhaokun123.bilibilihd.Application
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.hiczp.bilibili.api.main.model.Reply

fun Reply.Data.Top.Upper.toCommonReply(): Reply.Data.Reply {
    return gson.fromJson(GsonUtil.getGsonInstance().toJson(this))
}

fun String?.notEmptyOrNull(): String? {
    return if (this.isNullOrEmpty()) {
        null
    } else {
        this
    }
}

val pBilibiliClient
    get() = Application.getPBilibiliClient()

val gson
    get() = GsonUtil.getGsonInstance()

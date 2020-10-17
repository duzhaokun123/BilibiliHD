package com.duzhaokun123.bilibilihd.utils

import com.duzhaokun123.bilibilihd.Application
import com.github.salomonbrys.kotson.fromJson
import com.hiczp.bilibili.api.main.model.Reply
import okhttp3.OkHttpClient

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

val okHttpClient by lazy {
    OkHttpClient()
}

fun runOnUiThread(block: () -> Unit) = Application.runOnUiThread(block)

fun String.toIntOrDefault(defaultValue: Int): Int {
    return try {
        toInt()
    } catch (_: NumberFormatException) {
        defaultValue
    }
}

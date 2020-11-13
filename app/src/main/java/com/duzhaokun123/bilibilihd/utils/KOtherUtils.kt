package com.duzhaokun123.bilibilihd.utils

import com.duzhaokun123.bilibilihd.Application
import com.github.salomonbrys.kotson.fromJson
import com.hiczp.bilibili.api.main.model.Reply
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

fun runOnUiThread(block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(Dispatchers.Main, block = block)
}

fun kRunOnUiThread(block: suspend CoroutineScope.() -> Unit) = runOnUiThread(block)

fun String.toIntOrDefault(defaultValue: Int): Int {
    return try {
        toInt()
    } catch (_: NumberFormatException) {
        defaultValue
    }
}

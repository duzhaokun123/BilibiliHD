package com.duzhaokun123.bilibilihd.utils

import com.github.salomonbrys.kotson.fromJson
import com.hiczp.bilibili.api.main.model.Reply

fun Reply.Data.Top.Upper.toCommonReply(): Reply.Data.Reply {
    return GsonUtil.getGsonInstance().fromJson(GsonUtil.getGsonInstance().toJson(this))
}

fun String?.notEmptyOrNull(): String? {
    return if (this.isNullOrEmpty()) {
        null
    } else {
        this
    }
}
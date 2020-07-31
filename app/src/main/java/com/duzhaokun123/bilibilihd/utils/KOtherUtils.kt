package com.duzhaokun123.bilibilihd.utils

import com.github.salomonbrys.kotson.fromJson
import com.hiczp.bilibili.api.main.model.Reply

fun Reply.Data.Top.Upper.toCommonReply(): Reply.Data.Reply {
    return GsonUtil.getGsonInstance().fromJson(GsonUtil.getGsonInstance().toJson(this))
}
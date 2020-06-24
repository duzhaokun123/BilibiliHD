package com.duzhaokun123.bilibilihd.mybilibiliapi.history.model

import com.google.gson.annotations.SerializedName

data class Heartbeat(
        @SerializedName("code")
        var code: Int,
        @SerializedName("message")
        var message: String,
        @SerializedName("ttl")
        var ttl: Int
)
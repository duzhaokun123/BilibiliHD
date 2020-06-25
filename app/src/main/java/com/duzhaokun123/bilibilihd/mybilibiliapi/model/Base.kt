package com.duzhaokun123.bilibilihd.mybilibiliapi.model

import com.google.gson.annotations.SerializedName

data class Base(
        @SerializedName("code")
        var code: Int,
        @SerializedName("message")
        var message: String,
        @SerializedName("ttl")
        var ttl: Int
)
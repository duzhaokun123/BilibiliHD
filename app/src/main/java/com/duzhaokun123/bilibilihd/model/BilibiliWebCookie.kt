package com.duzhaokun123.bilibilihd.model

import com.google.gson.annotations.SerializedName

data class BilibiliWebCookie(
        @SerializedName("bili_jct")
        val biliJct: String,
        @SerializedName("DedeUserID")
        val dedeUserID: String,
        @SerializedName("DedeUserID__ckMd5")
        val dedeUserIDckMd5: String,
        @SerializedName("sid")
        val sid: String,
        @SerializedName("SESSDATA")
        val sessdata: String
) {
}
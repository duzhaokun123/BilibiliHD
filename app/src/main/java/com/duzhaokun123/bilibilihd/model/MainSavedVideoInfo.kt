package com.duzhaokun123.bilibilihd.model

import com.google.gson.annotations.SerializedName

data class MainSavedVideoInfo(
        @SerializedName("main_title")
        var mainTitle: String,
        @SerializedName("bvid")
        var bvid: String
)
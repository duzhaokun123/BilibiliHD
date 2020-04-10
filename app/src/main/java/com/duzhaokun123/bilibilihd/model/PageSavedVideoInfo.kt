package com.duzhaokun123.bilibilihd.model

import com.google.gson.annotations.SerializedName

data class PageSavedVideoInfo(
        @SerializedName("page")
        var page: Int,
        @SerializedName("video_title")
        var videoTitle: String,
        @SerializedName("cid")
        var cid: Long,
        @SerializedName("save_ts")
        var saveTs: Long
)
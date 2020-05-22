package com.duzhaokun123.bilibilihd.mybilibiliapi.welcomeAd.model

import com.google.gson.annotations.SerializedName

/**
 * 我只写了需要的
 */
data class WelcomeAd(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String,
        @SerializedName("ttl")
        val ttl: Int,
        @SerializedName("data")
        val data: Data
) {
    data class Data(
            @SerializedName("list")
            val list: List<List_>?,
            @SerializedName("show")
            val show: List<Show>?
    ) {
        data class List_(
                @SerializedName("id")
                val id: Long,
                @SerializedName("thumb")
                val thumb: String,
                @SerializedName("uri")
                val uri: String?,
                @SerializedName("video_url")
                val videoUrl: String?,
                @SerializedName("is_ad")
                val isAd: Boolean
        )

        data class Show(
                @SerializedName("id")
                val id: Long
        )
    }
}
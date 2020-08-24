package com.duzhaokun123.bilibilihd.model

import com.google.gson.annotations.SerializedName

data class Entry(
        @SerializedName("has_dash_audio")
        var hasDashAudio: Boolean,
        @SerializedName("title")
        var title: String,
        @SerializedName("type_tag")
        var typeTag: String,
        @SerializedName("cover")
        var cover: String,
        @SerializedName("avid")
        var aid: Long,
        @SerializedName("page_data")
        var pageData: PageData?,
        @SerializedName("ep")
        var ep: Ep?
) {
        data class PageData(
                @SerializedName("part")
                var part: String,
                @SerializedName("width")
                var width: Int,
                @SerializedName("height")
                var height: Int,
                @SerializedName("rotate")
                var rotate: Int
        )

        data class Ep(
                @SerializedName("av_id")
                var aid: Long,
                @SerializedName("index")
                var index: String,
                @SerializedName("index_title")
                var indexTitle: String,
                @SerializedName("width")
                var width: Int,
                @SerializedName("height")
                var height: Int,
                @SerializedName("rotate")
                var rotate: Int

        )
}
package com.duzhaokun123.bilibilihd.mybilibiliapi.reply.model

import com.google.gson.annotations.SerializedName
import com.hiczp.bilibili.api.main.model.Reply

/**
 * 只写了需要到
 */
data class ChildReply(
        @SerializedName("code")
        var code: Int,
        @SerializedName("message")
        var message: String,
        @SerializedName("data")
        var data: Data
) {
    data class Data(
            @SerializedName("cursor")
            var cursor: Reply.Data.Cursor,
            @SerializedName("root")
            var root: Root
    ) {
        data class Root(
                @SerializedName("rcount")
                var rcount: Int,
                @SerializedName("replies")
                var replies: List<Reply.Data.Reply>?
        )
    }
}
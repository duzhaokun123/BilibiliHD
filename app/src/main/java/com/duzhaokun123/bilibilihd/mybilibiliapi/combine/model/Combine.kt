package com.duzhaokun123.bilibilihd.mybilibiliapi.combine.model

import com.google.gson.annotations.SerializedName

data class Combine(
        @SerializedName("code")
        var code: Int,
        @SerializedName("data")
        var data: Data
) {
    data class Data(
            @SerializedName("result")
            var result: Result,
            @SerializedName("type")
            var type: Int
    ) {
        data class Result(
                @SerializedName("success")
                var success: Int,
                @SerializedName("gt")
                var gt: String,
                @SerializedName("challenge")
                var challenge: String,
                @SerializedName("key")
                var key: String
        )
    }
}
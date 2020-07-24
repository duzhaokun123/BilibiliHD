package com.duzhaokun123.bilibilihd.utils

import android.text.util.Linkify
import android.widget.TextView
import androidx.core.text.util.LinkifyCompat

object LinkifyUtil {
    fun addAllLinks(text: TextView) {
        LinkifyCompat.addLinks(text, PatternUtil.avPattern, "bilibili://", null, Linkify.TransformFilter { _, url ->
            val id = url.substring(2)
            "video/$id"
        })
        LinkifyCompat.addLinks(text, PatternUtil.bvPattern, "bilibili://", null, Linkify.TransformFilter { _, url ->
            try {
                val id = MyBilibiliClientUtil.bv2av(url)
                "video/$id"
            } catch (e: StringIndexOutOfBoundsException) {
                ""
            }
        })
        LinkifyCompat.addLinks(text, PatternUtil.cvPattern, "bilibili://", null, Linkify.TransformFilter { _, url ->
            val id = url.substring(2)
            "article/$id"
        })
        LinkifyCompat.addLinks(text, PatternUtil.smPattern, "https://", null, Linkify.TransformFilter { _, url ->
            "sp.nicovideo.jp/watch/$url"
        })
        LinkifyCompat.addLinks(text, PatternUtil.acPattern, "https://", null, Linkify.TransformFilter { _, url ->
            "www.acfun.cn/v/$url"
        })
    }
}
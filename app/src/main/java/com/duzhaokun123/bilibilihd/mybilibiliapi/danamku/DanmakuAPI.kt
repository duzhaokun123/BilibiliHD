package com.duzhaokun123.bilibilihd.mybilibiliapi.danamku

import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient
import com.duzhaokun123.bilibilihd.proto.BiliDanmaku
import okhttp3.Request
import java.lang.Exception

object DanmakuAPI {
    fun getBiliDanmaku(aid: Long, cid: Long, type: Int = 1, segmentIndex: Int): BiliDanmaku.DmSegMobileReply {
        val request = Request.Builder()
                .url("https://api.bilibili.com/x/v2/dm/web/seg.so?oid=$cid&pid=$aid&type=$type&segment_index=$segmentIndex")
                .build()
        val response = MyBilibiliClient.getInstance().okHttpClient.newCall(request).execute().body?.bytes()
        return BiliDanmaku.DmSegMobileReply.parseFrom(response)
    }
}
package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.hiczp.bilibili.api.danmaku.DanmakuAPI
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import okhttp3.ResponseBody

class PDanmakuAPI(private var danmakuAPI: DanmakuAPI) {
    fun list(aid: Long, cid: Long): ResponseBody {
        return GlobalScope.future { danmakuAPI.list(aid, cid).await() }.get()
    }
}
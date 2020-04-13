package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.hiczp.bilibili.api.main.MainAPI
import com.hiczp.bilibili.api.main.model.SendDanmakuResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PMainAPI(private var mainAPI: MainAPI) {
    fun sendDanmaku(aid: Long, cid: Long, progress: Long, message: String, mode: Int, color: Int): SendDanmakuResponse {
        return GlobalScope.future { mainAPI.sendDanmaku(aid = aid, oid = cid, oidInBody = cid, progress = progress, message = message, mode = mode, color = color).await() }.get()
    }
}
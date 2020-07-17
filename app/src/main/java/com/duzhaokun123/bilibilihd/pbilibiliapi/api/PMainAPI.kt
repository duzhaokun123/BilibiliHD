package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.hiczp.bilibili.api.main.MainAPI
import com.hiczp.bilibili.api.main.model.Reply
import com.hiczp.bilibili.api.main.model.SendDanmakuResponse
import com.hiczp.bilibili.api.main.model.SendReplyResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PMainAPI(private var mainAPI: MainAPI) {
    fun sendDanmaku(aid: Long, cid: Long, progress: Long, message: String, mode: Int, color: Int): SendDanmakuResponse {
        return GlobalScope.future { mainAPI.sendDanmaku(aid = aid, oid = cid, oidInBody = cid, progress = progress, message = message, mode = mode, color = color).await() }.get()
    }

    fun reply(aid: Long, next: Long? = null): Reply {
        return GlobalScope.future { mainAPI.reply(oid = aid, next = next).await() }.get()
    }

    fun sendReply(aid: Long, message: String, parent: Long? = null, root: Long? = null): SendReplyResponse {
        return GlobalScope.future { mainAPI.sendReply(oid = aid, message = message, parent = parent, root = root).await() }.get()
    }
}
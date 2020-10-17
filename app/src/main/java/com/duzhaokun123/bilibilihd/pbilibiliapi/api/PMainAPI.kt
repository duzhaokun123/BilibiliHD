package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.hiczp.bilibili.api.main.MainAPI
import com.hiczp.bilibili.api.main.model.*
import com.hiczp.bilibili.api.retrofit.CommonResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.lang.RuntimeException

class PMainAPI(private var mainAPI: MainAPI) {
    fun sendDanmaku(aid: Long, cid: Long, progress: Long, message: String, mode: Int, color: Int): SendDanmakuResponse {
        return GlobalScope.future { mainAPI.sendDanmaku(aid = aid, oid = cid, oidInBody = cid, progress = progress, message = message, mode = mode, color = color).await() }.get()
    }

    fun reply(oid: Long, next: Long? = null, mode: Int, type: Int): Reply {
        return GlobalScope.future { mainAPI.reply(oid = oid, next = next, mode = mode, type = type).await() }.get()
    }

    fun sendReply(oid: Long, message: String, parent: Long? = null, root: Long? = null, type: Int): SendReplyResponse {
        return GlobalScope.future { mainAPI.sendReply(oid = oid, message = message, parent = parent, root = root, type = type).await() }.get()
    }

    fun likeReply(action: Int, oid: Long, replyId: Long, type: Int): CommonResponse {
        return GlobalScope.future { mainAPI.likeReply(action, oid, replyId, type).await() }.get()
    }

    fun dislikeReply(action: Int, oid: Long, replyId: Long, type: Int): CommonResponse {
        return GlobalScope.future { mainAPI.dislikeReply(action, oid, replyId, type).await() }.get()
    }

    fun addFavoriteVideo(aid: Long, fid: Long): CommonResponse {
        return GlobalScope.future { mainAPI.addFavoriteVideo(aid, fid.toString()).await() }.get()
    }

    fun toView(aid: Long? = null, bvid: String? = null): CommonResponse {
        return GlobalScope.future {
            when {
                aid != null -> mainAPI.toView(aid).await()
                bvid != null -> mainAPI.toView(bvid).await()
                else -> throw RuntimeException("both aid and bvid is null")
            }
        }.get()
    }

    fun delReply(type: Int, oid: Long, rpid: Long): CommonResponse {
        return GlobalScope.future { mainAPI.delReply(type, oid, rpid).await() }.get()
    }

    fun childReply2(oid: Long, root: Long, type: Int, next: Long?): ChildReply2 {
        return GlobalScope.future { mainAPI.childReply2(oid, root, type, next ?: 0).await() }.get()
    }

    fun resourceIds(mediaId: Long, mid: Long): ResourceIds {
        return GlobalScope.future { mainAPI.resourceIds(mediaId, mid).await() }.get()
    }

    fun resourceInfos(mediaId: Long, resources: String, mid: Long): ResourceInfos {
        return GlobalScope.future { mainAPI.resourceInfos(mediaId, resources, mid).await() }.get()
    }
}
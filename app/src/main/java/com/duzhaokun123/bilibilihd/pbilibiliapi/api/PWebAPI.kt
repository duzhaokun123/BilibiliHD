package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.hiczp.bilibili.api.retrofit.CommonResponse
import com.hiczp.bilibili.api.web.WebAPI
import com.hiczp.bilibili.api.web.model.VideoShot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PWebAPI(private val webApi: WebAPI) {
    fun heartbeat(aid: Long? = null, bvid: String? = null, cid: Long? = null, playedTime: Long): CommonResponse {
        return GlobalScope.future { webApi.heartbeat(aid, bvid, cid, playedTime).await() }.get()
    }

    fun videoshot(aid: Long, cid: Long?): VideoShot {
        return GlobalScope.future { webApi.videoshot(aid, cid, 1).await() }.get()
    }
}
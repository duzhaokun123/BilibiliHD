package com.duzhaokun123.bilibilihd.pBilibiliApi.app

import com.hiczp.bilibili.api.app.AppAPI
import com.hiczp.bilibili.api.app.model.MyInfo
import com.hiczp.bilibili.api.app.model.SearchResult
import com.hiczp.bilibili.api.app.model.Space
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PAppAPI {
    private var appAPI: AppAPI

    constructor(appAPI: AppAPI) {
        this.appAPI = appAPI
    }

    fun getMyInfo(): MyInfo {
        return GlobalScope.future{ appAPI.myInfo().await() }.get()
    }

    fun search(keyword: String): SearchResult {
        return GlobalScope.future { appAPI.search(keyword = keyword).await() }.get()
    }

    fun search(keyword: String, from_source: String): SearchResult {
        return GlobalScope.future { appAPI.search(keyword = keyword, from_source = from_source).await() }.get()
    }

    fun search(keyword: String, pageNumber: Int): SearchResult {
        return GlobalScope.future { appAPI.search(keyword = keyword, pageNumber = pageNumber).await() }.get()
    }

    fun search(keyword: String, from_source: String, pageNumber: Int): SearchResult {
        return GlobalScope.future { appAPI.search(keyword = keyword, from_source = from_source, pageNumber = pageNumber).await() }.get()
    }

    fun space(uid: Long): Space {
        return GlobalScope.future { appAPI.space(vmId = uid).await() } .get()
    }
}
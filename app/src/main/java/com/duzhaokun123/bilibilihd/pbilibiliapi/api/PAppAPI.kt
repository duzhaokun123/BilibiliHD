package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.hiczp.bilibili.api.app.AppAPI
import com.hiczp.bilibili.api.app.model.*
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

    fun homePage(pull: Boolean): HomePage{
        return GlobalScope.future { appAPI.homePage(pull = pull).await() }.get()
    }

    fun view(aid: Long): View {
        return GlobalScope.future { appAPI.view(aid = aid).await() }.get()
    }
}
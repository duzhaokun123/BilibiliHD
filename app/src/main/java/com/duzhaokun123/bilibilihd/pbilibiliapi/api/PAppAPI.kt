package com.duzhaokun123.bilibilihd.pbilibiliapi.api

import com.hiczp.bilibili.api.app.AppAPI
import com.hiczp.bilibili.api.app.model.*
import com.hiczp.bilibili.api.retrofit.exception.BilibiliApiException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PAppAPI(private var appAPI: AppAPI) {

    fun getMyInfo(): MyInfo {
        return GlobalScope.future { appAPI.myInfo().await() }.get()
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
        return GlobalScope.future { appAPI.space(vmId = uid).await() }.get()
    }

    fun homePage(pull: Boolean): HomePage {
        return GlobalScope.future { appAPI.homePage(pull = pull).await() }.get()
    }

    @Throws(BilibiliApiException::class)
    fun view(aid: Long): View? {
        var exception: Exception? = null
        var view: View? = null
        GlobalScope.future {
            try {
                view = appAPI.view(aid = aid).await()
            } catch (e: Exception) {
                exception = e
            }
        }.get()
        if (exception != null) {
            throw exception as Exception;
        }

        return view
    }
}
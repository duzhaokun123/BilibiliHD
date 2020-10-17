package com.duzhaokun123.bilibilihd.utils

import android.content.Context
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.hiczp.bilibili.api.app.model.SplashList
import com.hiczp.bilibili.api.main.model.ResourceIds
import java.util.*
import kotlin.collections.ArrayList

object ApiUtil {
    val SplashList.showList: SplashList.Data.List_?
        get() {
            if (this.data.list == null) {
                return null
            }

            if (this.data.show == null) { //没有 show 就随机来一个
                val random = Random(System.currentTimeMillis())
                while (true) {
                    val i: Int = random.nextInt() % this.data.list!!.size
                    if (i >= 0 && i < this.data.list!!.size) {
                        return this.data.list!![i]
                    }
                }
            }

            for (show in this.data.show!!) {
                for (list_ in this.data.list!!) {
                    if (list_.id == show.id) {
                        return list_
                    }
                }
            }

            return null
        }

    val ResourceIds.resources: List<String>
        get() {
            val re = ArrayList<String>()
            var i = 0
            while (i < this.data.size) {
                val resourcesSB = StringBuilder()
                var j = 0
                while (j < 20 && i + j < this.data.size) {
                    val data: ResourceIds.Data = this.data[i + j]
                    resourcesSB.append(data.id).append(':').append(data.type).append(',')
                    j++
                }
                if (resourcesSB.isNotEmpty()) {
                    resourcesSB.deleteCharAt(resourcesSB.length - 1)
                }
                re.add(resourcesSB.toString())
                i += 20
            }
            return re
        }

    fun addToView(context: Context, aid: Long? = null, bvid: String? = null) {
        Thread {
            try {
                pBilibiliClient.pMainAPI.toView(aid, bvid)
                Application.runOnUiThread { TipUtil.showTip(context, R.string.added) }
            } catch (e: Exception) {
                e.printStackTrace()
                Application.runOnUiThread { TipUtil.showTip(context, e.message) }
            }
        }.start()
    }
}
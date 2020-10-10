package com.duzhaokun123.bilibilihd.utils

import com.hiczp.bilibili.api.app.model.SplashList
import java.util.*

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
}
package com.duzhaokun123.bilibilihd.utils

import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser

object EmptyBiliDanmakuParser: BaseDanmakuParser() {
    private lateinit var danmakus: Danmakus

    override fun parse(): IDanmakus {
        if (::danmakus.isInitialized.not()) {
            danmakus = Danmakus()
        }
        return danmakus
    }
}
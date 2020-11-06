package com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser

import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser

object EmptyBiliDanmakuParser : BaseDanmakuParser() {
    private val danmakus by lazy {
        Danmakus()
    }

    override fun parse() = danmakus
}
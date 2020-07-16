package com.duzhaokun123.bilibilihd.utils

import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.google.gson.stream.JsonReader
import com.hiczp.bilibili.api.bounded
import com.hiczp.bilibili.api.readUInt
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.*
import java.util.zip.GZIPInputStream

object DanmakuUtil {
    private lateinit var danmakuContext: DanmakuContext

    /**
     * 解析弹幕文件
     *
     * @param inputStream 输入流, 可以指向任何位置
     *
     * @return 返回弹幕的输入流
     */
    fun toInputStream(inputStream: InputStream): Pair<Map<Long, Int>, BufferedInputStream> {
        //Json 的长度
        val jsonLength = inputStream.readUInt()

        //弹幕ID-Flag
        val danmakuFlags = HashMap<Long, Int>()
        //gson 会从 reader 中自行缓冲 1024 个字符, 这会导致额外的字符被消费. 因此要限制其读取数量
        //流式解析 Json
        with(JsonReader(inputStream.bounded(jsonLength).reader())) {
            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "dmflags" -> {
                        beginArray()
                        while (hasNext()) {
                            var danmakuId = 0L
                            var flag = 0
                            beginObject()
                            while (hasNext()) {
                                when (nextName()) {
                                    "dmid" -> danmakuId = nextLong()
                                    "flag" -> flag = nextInt()
                                    else -> skipValue()
                                }
                            }
                            endObject()
                            danmakuFlags[danmakuId] = flag
                        }
                        endArray()
                    }
                    else -> skipValue()
                }
            }
            endObject()
        }

        //json 解析完毕后, 剩下的内容是一个 gzip 压缩过的 xml, 直接返回
        return Pair(danmakuFlags, GZIPInputStream(inputStream).buffered())
    }

    fun syncDanmakuSettings() {
        val maxLinesPair = HashMap<Int, Int>()
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 10 // 滚动弹幕最大显示10行


        val overlappingEnablePair = HashMap<Int, Boolean>()
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = false // 允许从右至左的弹幕重合

        overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true // 不允许从顶部弹幕重合

        danmakuContext
                .setDuplicateMergingEnabled(Settings.danmaku.isDuplicateMerging)
                .setScrollSpeedFactor(Settings.danmaku.scrollSpeedFactor)
                //                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
                //        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                //                .setMaximumLines(maxLinesPair) //设置最大行数
                .preventOverlapping(overlappingEnablePair)
                .setMaximumVisibleSizeInScreen(Settings.danmaku.maximumVisibleSizeInScreen)


        when (Settings.danmaku.danmakuStyle) {
            IDisplayer.DANMAKU_STYLE_NONE -> danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
            IDisplayer.DANMAKU_STYLE_SHADOW -> danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_SHADOW, Settings.danmaku.p1)
            IDisplayer.DANMAKU_STYLE_STROKEN -> danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, Settings.danmaku.p1)
            IDisplayer.DANMAKU_STYLE_PROJECTION -> danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_PROJECTION, Settings.danmaku.p1, Settings.danmaku.p2, Settings.danmaku.p3)
        }

        if (Settings.danmaku.textSize != 0f) {
            danmakuContext.setScaleTextSize(Settings.danmaku.textSize)
        } else {
            danmakuContext.setScaleTextSize(Application.getInstance().resources.getInteger(R.integer.danmaku_scale_text_size).toFloat())
        }

        if (Settings.danmaku.danmakuMargin != 0) {
            danmakuContext.setDanmakuMargin(Settings.danmaku.danmakuMargin)
        } else {
            danmakuContext.setDanmakuMargin(Application.getInstance().resources.getInteger(R.integer.danmaku_margin))
        }
    }

    fun getDanmakuContext(): DanmakuContext {
        if (::danmakuContext.isInitialized.not()) {
            danmakuContext = DanmakuContext.create()
        }
        syncDanmakuSettings()
        return danmakuContext
    }
}
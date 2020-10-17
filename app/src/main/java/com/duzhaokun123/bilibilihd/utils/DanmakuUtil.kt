package com.duzhaokun123.bilibilihd.utils

import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import java.util.*

object DanmakuUtil {
    private lateinit var danmakuContext: DanmakuContext

    fun syncDanmakuSettings() {
        if (::danmakuContext.isInitialized.not()) {
            return
        }

        val overlappingEnablePair = HashMap<Int, Boolean>()
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_BOTTOM] = true
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_LR] = true

        for (allow in Settings.danmaku.allowDanmakuOverlapping) {
            when (allow) {
                "top" -> overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = false
                "bottom" -> overlappingEnablePair[BaseDanmaku.TYPE_FIX_BOTTOM] = false
                "r2l" -> overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = false
                "l2r" -> overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_LR] = false
            }
        }

        danmakuContext
                .setDuplicateMergingEnabled(Settings.danmaku.isDuplicateMerging)
                .setScrollSpeedFactor(Settings.danmaku.scrollSpeedFactor)
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

        danmakuContext.ftDanmakuVisibility = true
        danmakuContext.fbDanmakuVisibility = true
        danmakuContext.r2LDanmakuVisibility = true
        danmakuContext.l2RDanmakuVisibility = true
        danmakuContext.specialDanmakuVisibility = true
        for (place in Settings.danmaku.blockByPlace) {
            when (place) {
                "top" -> danmakuContext.ftDanmakuVisibility = false
                "bottom" -> danmakuContext.fbDanmakuVisibility = false
                "r2l" -> danmakuContext.r2LDanmakuVisibility = false
                "l2r" -> danmakuContext.l2RDanmakuVisibility = false
                "special" -> danmakuContext.specialDanmakuVisibility = false
            }
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
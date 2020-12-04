package com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser

import android.graphics.Color
import android.graphics.PointF
import android.text.TextUtils
import com.duzhaokun123.bilibilihd.proto.BiliDanmaku
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil.toDanmakuType
import com.duzhaokun123.bilibilihd.utils.toFloatOrDefault
import com.duzhaokun123.danmakuview.Value
import com.duzhaokun123.danmakuview.danmaku.BiliSpecialDanmaku
import com.duzhaokun123.danmakuview.danmaku.SpecialDanmaku
import com.duzhaokun123.danmakuview.interfaces.DanmakuParser
import com.duzhaokun123.danmakuview.model.Danmakus
import org.json.JSONArray
import org.json.JSONException

class ProtobufBiliDanmakuParser(private val dmSegMobileReplies: Array<BiliDanmaku.DmSegMobileReply?>) : DanmakuParser {
    companion object {
        const val BILI_PLAYER_WIDTH = 682.0F
        const val BILI_PLAYER_HEIGHT = 438.0F

        fun initialSpecialDanmakuData(danmaku: SpecialDanmaku) {
            val text = danmaku.text.trim { it <= ' ' }
            if (text.startsWith('[')) {
                var textArray: Array<String?>? = null
                try {
                    val jsonArray = JSONArray(text)
                    textArray = arrayOfNulls(jsonArray.length())
                    for (i in textArray.indices) {
                        textArray[i] = jsonArray.getString(i)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                // TODO: 20-12-2
                if (textArray != null && textArray.size >= 5 && textArray[4].isNullOrEmpty().not()) {
                    danmaku.text = textArray[4]!!
                    danmaku.fillText()
                    var beginX = textArray[0]!!.toFloatOrDefault()
                    var beginY = textArray[1]!!.toFloatOrDefault()
                    var endX = beginX
                    var endY = beginY
                    val alphaArray = textArray[2]!!.split("-".toRegex()).toTypedArray()
                    val beginAlpha = (Value.ALPHA_MAX * alphaArray[0].toFloatOrDefault()).toInt()
                    var endAlpha = beginAlpha
                    if (alphaArray.size > 1) {
                        endAlpha = (Value.ALPHA_MAX * alphaArray[1].toFloatOrDefault()).toInt()
                    }
                    val alphaDuration = (textArray[3]!!.toFloatOrDefault() * 1000).toLong()
                    var translationDuration = alphaDuration
                    var translationStartDelay = 0L
                    var rotateY = 0F
                    var rotateZ = 0F
                    if (textArray.size >= 7) {
                        rotateZ = textArray[5]!!.toFloatOrDefault()
                        rotateY = textArray[6]!!.toFloatOrDefault()
                    }
                    if (textArray.size >= 11) {
                        endX = textArray[7]!!.toFloatOrDefault()
                        endY = textArray[8]!!.toFloatOrDefault()
                        if (textArray[9].isNullOrEmpty().not())
                            translationDuration = textArray[9]!!.toLong()
                        if (textArray[10].isNullOrEmpty().not())
                            translationStartDelay = textArray[10]!!.toFloatOrDefault().toLong()

                    }
                    if (textArray[0]!!.contains('.')) {
                        beginX *= BiliSpecialDanmaku.BILI_PLAYER_WIDTH
                    }
                    if (textArray[1]!!.contains('.')) {
                        beginY *= BiliSpecialDanmaku.BILI_PLAYER_HEIGHT
                    }
                    if (textArray.size >= 8 && textArray[7]!!.contains('.')) {
                        endX *= BiliSpecialDanmaku.BILI_PLAYER_WIDTH
                    }
                    if (textArray.size >= 9 && textArray[8]!!.contains('.')) {
                        endY *= BiliSpecialDanmaku.BILI_PLAYER_HEIGHT
                    }
                    danmaku.duration = alphaDuration
//                    danmaku.rotationZ = rotateZ
                    danmaku.keyframes[0F] = Triple(PointF(beginX / BILI_PLAYER_WIDTH, beginY / BILI_PLAYER_HEIGHT), rotateY, beginAlpha)
                    danmaku.keyframes[1F] = Triple(PointF(endX / BILI_PLAYER_WIDTH, endY / BILI_PLAYER_HEIGHT), rotateY, endAlpha)

//                    danmaku.rotationY = rotateY
//                    danmaku.beginX = beginX
//                    danmaku.beginY = beginY
//                    danmaku.endX = endX
//                    danmaku.endY = endY
//                    danmaku.translationDuration = translationDuration
//                    danmaku.translationStartDelay = translationStartDelay
//                    danmaku.beginAlpha = beginAlpha
//                    danmaku.endAlpha = endAlpha
                    if (textArray.size >= 12) {
                        if (textArray[11].isNullOrEmpty().not() && textArray[11].toBoolean()) {
                            danmaku.textShadowColor = Color.TRANSPARENT
                        }
                    }
                    if (textArray.size >= 13) {
                        //TODO 字体 textArray[12]
                    }
//                    if (textArray.size >= 14) {
//                        // Linear.easeIn or Quadratic.easeOut
//                        danmaku.isQuadraticEaseOut = "0" == textArray[13]
//                    }
                    if (textArray.size >= 15) {
                        // 路径数据
                        if ("" != textArray[14]) {
                            val motionPathString = textArray[14]!!.substring(1)
                            if (!TextUtils.isEmpty(motionPathString)) {
                                val pointStrArray = motionPathString.split("L".toRegex()).toTypedArray()
                                if (pointStrArray.isNotEmpty()) {
                                    val points = Array(pointStrArray.size) { FloatArray(2) }
                                    for (i in pointStrArray.indices) {
                                        val pointArray =
                                                pointStrArray[i].split(",".toRegex()).toTypedArray()
                                        if (pointArray.size >= 2) {
                                            points[i][0] = pointArray[0].toFloatOrDefault()
                                            points[i][1] = pointArray[1].toFloatOrDefault()
                                        }
                                    }
                                    val t = danmaku.duration.toFloat() / points.size
                                    var a = 0F
                                    points.forEachIndexed { index, floats ->
                                        danmaku.keyframes[a] = Triple(PointF(floats[0] / BILI_PLAYER_WIDTH, floats[1] / BILI_PLAYER_HEIGHT), rotateY, beginAlpha + (endAlpha - beginAlpha) * (index / points.size))
                                        a += t
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private lateinit var danmakus: Danmakus

    override fun parse(): Danmakus {
        if (::danmakus.isInitialized.not()) {
            danmakus = Danmakus()
            for (dmSegMobileReply in dmSegMobileReplies) {
                if (dmSegMobileReply != null) {
                    for (danmakuElem in dmSegMobileReply.elemsList) {
                        val type = danmakuElem.mode.toDanmakuType()
                        val color = -0x1000000 or danmakuElem.color
                        val danmaku = DanmakuUtil.simpleDanmakuFactory.create(type)
                        danmaku.offset = danmakuElem.progress.toLong()
                        danmaku.textSize = danmakuElem.fontzsie.toFloat()
                        danmaku.textColor = color
                        danmaku.textShadowColor = if (color <= Color.BLACK) Color.WHITE else Color.BLACK
                        danmaku.text = danmakuElem.content

                        if (danmaku is SpecialDanmaku)
                            initialSpecialDanmakuData(danmaku)

                        danmakus.add(danmaku)
                    }
                }
            }
        }
        return danmakus
    }
}
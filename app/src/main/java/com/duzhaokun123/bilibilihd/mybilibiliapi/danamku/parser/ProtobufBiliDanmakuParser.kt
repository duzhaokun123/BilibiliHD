package com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser

import android.graphics.Color
import android.text.TextUtils
import com.duzhaokun123.bilibilihd.proto.BiliDanmaku
import master.flame.danmaku.danmaku.model.*
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.DanmakuFactory
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.danmaku.util.DanmakuUtils
import org.json.JSONArray
import org.json.JSONException

class ProtobufBiliDanmakuParser(private val dmSegMobileReplies: Array<BiliDanmaku.DmSegMobileReply?>) : BaseDanmakuParser() {
    companion object {
        private const val TRUE_STRING = "true"

        private fun isPercentageNumber(number: String?): Boolean {
            //return number >= 0f && number <= 1f;
            return number != null && number.contains(".")
        }

        private fun parseFloat(floatStr: String?): Float {
            return try {
                floatStr?.toFloat() ?: 0.0f
            } catch (e: NumberFormatException) {
                0.0f
            }
        }

        private fun parseInteger(intStr: String?): Int {
            return try {
                intStr?.toInt() ?: 0
            } catch (e: java.lang.NumberFormatException) {
                0
            }
        }

        fun initialSpecailDanmakuData(baseDanmaku: BaseDanmaku, context: DanmakuContext, dispScaleX: Float, dispScaleY: Float) {
            val text: String = baseDanmaku.text.toString().trim { it <= ' ' }
            if (baseDanmaku.type == BaseDanmaku.TYPE_SPECIAL && text.startsWith("[")
                    && text.endsWith("]")) {
                //text = text.substring(1, text.length() - 1);
                var textArr: Array<String?>? = null //text.split(",", -1);
                try {
                    val jsonArray = JSONArray(text)
                    textArr = arrayOfNulls(jsonArray.length())
                    for (i in textArr.indices) {
                        textArr[i] = jsonArray.getString(i)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (textArr != null && textArr.size >= 5 && TextUtils.isEmpty(textArr[4]).not()) {
                    DanmakuUtils.fillText(baseDanmaku, textArr[4])
                    var beginX: Float = parseFloat(textArr[0])
                    var beginY: Float = parseFloat(textArr[1])
                    var endX = beginX
                    var endY = beginY
                    val alphaArr = textArr[2]!!.split("-".toRegex()).toTypedArray()
                    val beginAlpha = (AlphaValue.MAX * parseFloat(alphaArr[0])).toInt()
                    var endAlpha = beginAlpha
                    if (alphaArr.size > 1) {
                        endAlpha = (AlphaValue.MAX * parseFloat(alphaArr[1])).toInt()
                    }
                    val alphaDuraion = (parseFloat(textArr[3]) * 1000).toLong()
                    var translationDuration = alphaDuraion
                    var translationStartDelay: Long = 0
                    var rotateY = 0f
                    var rotateZ = 0f
                    if (textArr.size >= 7) {
                        rotateZ = parseFloat(textArr[5])
                        rotateY = parseFloat(textArr[6])
                    }
                    if (textArr.size >= 11) {
                        endX = parseFloat(textArr[7])
                        endY = parseFloat(textArr[8])
                        if ("" != textArr[9]) {
                            translationDuration = parseInteger(textArr[9]).toLong()
                        }
                        if ("" != textArr[10]) {
                            translationStartDelay = parseFloat(textArr[10]).toLong()
                        }
                    }
                    if (isPercentageNumber(textArr[0])) {
                        beginX *= DanmakuFactory.BILI_PLAYER_WIDTH
                    }
                    if (isPercentageNumber(textArr[1])) {
                        beginY *= DanmakuFactory.BILI_PLAYER_HEIGHT
                    }
                    if (textArr.size >= 8 && isPercentageNumber(textArr[7])) {
                        endX *= DanmakuFactory.BILI_PLAYER_WIDTH
                    }
                    if (textArr.size >= 9 && isPercentageNumber(textArr[8])) {
                        endY *= DanmakuFactory.BILI_PLAYER_HEIGHT
                    }
                    baseDanmaku.duration = Duration(alphaDuraion)
                    baseDanmaku.rotationZ = rotateZ
                    baseDanmaku.rotationY = rotateY
                    context.mDanmakuFactory.fillTranslationData(baseDanmaku, beginX,
                            beginY, endX, endY, translationDuration, translationStartDelay, dispScaleX, dispScaleY)
                    context.mDanmakuFactory.fillAlphaData(baseDanmaku, beginAlpha, endAlpha, alphaDuraion)
                    if (textArr.size >= 12) {
                        // 是否有描边
                        if (!TextUtils.isEmpty(textArr[11]) && TRUE_STRING.equals(textArr[11], ignoreCase = true)) {
                            baseDanmaku.textShadowColor = Color.TRANSPARENT
                        }
                    }
                    if (textArr.size >= 13) {
                        //TODO 字体 textArr[12]
                    }
                    if (textArr.size >= 14) {
                        // Linear.easeIn or Quadratic.easeOut
                        (baseDanmaku as SpecialDanmaku).isQuadraticEaseOut = "0" == textArr[13]
                    }
                    if (textArr.size >= 15) {
                        // 路径数据
                        if ("" != textArr[14]) {
                            val motionPathString = textArr[14]!!.substring(1)
                            if (!TextUtils.isEmpty(motionPathString)) {
                                val pointStrArray = motionPathString.split("L".toRegex()).toTypedArray()
                                if (pointStrArray.isNotEmpty()) {
                                    val points = Array(pointStrArray.size) { FloatArray(2) }
                                    for (i in pointStrArray.indices) {
                                        val pointArray = pointStrArray[i].split(",".toRegex()).toTypedArray()
                                        if (pointArray.size >= 2) {
                                            points[i][0] = parseFloat(pointArray[0])
                                            points[i][1] = parseFloat(pointArray[1])
                                        }
                                    }
                                    DanmakuFactory.fillLinePathData(baseDanmaku, points, dispScaleX,
                                            dispScaleY)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private var danmakus: Danmakus? = null

    private var mDispScaleX = 0f
    private var mDispScaleY = 0f

    override fun parse(): IDanmakus {
        if (danmakus == null) {
            danmakus = Danmakus(IDanmakus.ST_BY_TIME, false, mContext.baseComparator)
            var index = 0
            for (dmSegMobileReply in dmSegMobileReplies) {
                if (dmSegMobileReply != null) {
                    for (danmakuElem in dmSegMobileReply.elemsList) {
                        val type = danmakuElem.mode
                        val color = -0x1000000 or danmakuElem.color
                        val baseDanmaku = mContext.mDanmakuFactory.createDanmaku(type, mContext)
                        if (baseDanmaku != null) {
                            baseDanmaku.time = danmakuElem.progress.toLong()
                            baseDanmaku.textSize = danmakuElem.fontzsie * (mDispDensity - 0.6f)
                            baseDanmaku.textColor = color
                            baseDanmaku.textShadowColor = if (color <= Color.BLACK) Color.WHITE else Color.BLACK

                            DanmakuUtils.fillText(baseDanmaku, danmakuElem.content)
                            baseDanmaku.index = index++

                            initialSpecailDanmakuData(baseDanmaku, mContext, mDispScaleX, mDispScaleY)

                            if (baseDanmaku.duration != null) {
                                baseDanmaku.timer = mTimer
                                baseDanmaku.flags = mContext.mGlobalFlagValues
                                danmakus!!.addItem(baseDanmaku)
                            }
                        }
                    }
                }
            }
        }
        return danmakus!!
    }

    override fun setDisplayer(disp: IDisplayer): BaseDanmakuParser {
        super.setDisplayer(disp)
        mDispScaleX = mDispWidth / DanmakuFactory.BILI_PLAYER_WIDTH
        mDispScaleY = mDispHeight / DanmakuFactory.BILI_PLAYER_HEIGHT
        return this
    }
}
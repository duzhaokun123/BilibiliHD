package com.duzhaokun123.bilibilihd.utils

import android.graphics.Color
import android.net.Uri
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.utils.ProtobufBiliDanmakuParser.Companion.initialSpecailDanmakuData
import master.flame.danmaku.danmaku.model.*
import master.flame.danmaku.danmaku.model.android.DanmakuFactory
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.danmaku.util.DanmakuUtils
import java.util.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants

class XmlBiliDanmakuParser(private val xmlFile: Uri?) : BaseDanmakuParser() {

    private lateinit var danmakus: Danmakus
    var index = 0
    private var mDispScaleX = 0f
    private var mDispScaleY = 0f

    override fun parse(): IDanmakus {
        if (::danmakus.isInitialized.not()) {
            danmakus = Danmakus(IDanmakus.ST_BY_TIME, false, mContext.baseComparator)
            xmlFile?.let { Application.getInstance().contentResolver.openInputStream(it) }?.use {
                //流式解析 xml
                val xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(it)
                var startD = false  //之前解析到的 element 是否是 d
                var p: String? = null   //之前解析到的 p 的值
                while (xmlEventReader.hasNext()) {
                    val event = xmlEventReader.nextEvent()
                    when (event.eventType) {
                        XMLStreamConstants.START_ELEMENT -> {
                            with(event.asStartElement()) {
                                startD = name.localPart == "d"
                                if (startD) {
                                    p = getAttributeByName(QName("p")).value
                                }
                            }
                        }
                        XMLStreamConstants.CHARACTERS -> {
                            //如果前一个解析到的是 d 标签, 那么此处得到的一定是 d 标签的 body
                            if (startD) {
                                StringTokenizer(p, ",").let { tokens ->
                                    tokens.nextToken() // id
                                    tokens.nextToken() // unknownAttribute1
                                    val time = tokens.nextToken().toLong() // time
                                    val baseDanmaku = mContext.mDanmakuFactory.createDanmaku(tokens.nextToken().toInt() /* mode */, mContext)
                                    // FIXME: 20-9-19 baseDanmaku 总是为 null
                                    if (baseDanmaku != null) {
                                        baseDanmaku.time = time
                                        baseDanmaku.textSize = tokens.nextToken().toInt() * (mDispDensity - 0.6f) // textSize
                                        val color = -0x1000000 or tokens.nextToken().toInt() // color
                                        baseDanmaku.textColor = color
                                        baseDanmaku.time = time
                                        baseDanmaku.textShadowColor = if (color <= Color.BLACK) Color.WHITE else Color.BLACK
//                                    tokens.nextToken() // timestamp
//                                    tokens.nextToken() // unknownAttribute7
//                                    tokens.nextToken() // user
                                        DanmakuUtils.fillText(baseDanmaku, event.asCharacters().data)
                                        baseDanmaku.index = index++
                                        initialSpecailDanmakuData(baseDanmaku, mContext, mDispScaleX, mDispScaleY)
                                        if (baseDanmaku.duration != null) {
                                            baseDanmaku.timer = mTimer
                                            baseDanmaku.flags = mContext.mGlobalFlagValues
                                            danmakus.addItem(baseDanmaku)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return danmakus
    }

    override fun setDisplayer(disp: IDisplayer): BaseDanmakuParser {
        super.setDisplayer(disp)
        mDispScaleX = mDispWidth / DanmakuFactory.BILI_PLAYER_WIDTH
        mDispScaleY = mDispHeight / DanmakuFactory.BILI_PLAYER_HEIGHT
        return this
    }


}
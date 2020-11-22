package com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser

import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser.ProtobufBiliDanmakuParser.Companion.initialSpecialDanmakuData
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil.toDanmakuType
import com.duzhaokun123.danmakuview.danmaku.SpecialDanmaku
import com.duzhaokun123.danmakuview.interfaces.DanmakuParser
import com.duzhaokun123.danmakuview.model.Danmakus
import java.io.InputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants

class XmlBiliDanmakuParser(inputStream: InputStream) : DanmakuParser {
    private val danmakus by lazy {
        val danmakus = Danmakus()

        val xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(inputStream)
        var startD = false
        var p: String? = null
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
                        java.util.StringTokenizer(p, ",").let { tokens ->
                            val text = event.asCharacters().data
                            //FIXME: 见鬼了, 解析出一堆 \n
                            if (text.startsWith("\n")) return@let
                            val offset = (tokens.nextToken().toFloat() * 1000).toLong() // offset
                            val danmaku = DanmakuUtil. simpleDanmakuFactory.create(
                                    tokens.nextToken().toInt().toDanmakuType() // type
                            )
                            danmaku.text = text
                            danmaku.offset = offset
                            danmaku.textSize = tokens.nextToken().toFloat() // textSize
                            val color =
                                    (-0x1000000L or tokens.nextToken().toLong() and -0x1).toInt()
                            danmaku.textColor = color
                            danmaku.textShadowColor =
                                    if (color <= android.graphics.Color.BLACK) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                            if (danmaku is SpecialDanmaku) initialSpecialDanmakuData(danmaku)
//                        initialSpecailDanmakuData(danmaku, mContext, mDispScaleX, mDispScaleY)
                            danmakus.add(danmaku)
                        }
                    }
                }
            }
        }
        danmakus
    }

    override fun parse() = danmakus
}
package com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser

import android.util.Xml
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser.ProtobufBiliDanmakuParser.Companion.initialSpecialDanmakuData
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil.toDanmakuType
import com.duzhaokun123.danmakuview.danmaku.BiliSpecialDanmaku
import com.duzhaokun123.danmakuview.interfaces.DanmakuParser
import com.duzhaokun123.danmakuview.model.Danmakus
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class XmlBiliDanmakuParser(inputStream: InputStream) : DanmakuParser {
    private val danmakus by lazy {
        val danmakus = Danmakus()

        val xmlParser = Xml.newPullParser()
        xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        xmlParser.setInput(inputStream, null)
        xmlParser.nextTag()
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            if (xmlParser.eventType != XmlPullParser.START_TAG) continue
            if (xmlParser.name == "d") {
                val p = xmlParser.getAttributeValue(null,"p")
                p.split(",").let { s ->
                    xmlParser.require(XmlPullParser.START_TAG, null, "d")
                    val text = readText(xmlParser)
                    xmlParser.require(XmlPullParser.END_TAG, null, "d")
                    val offset = (s[0].toFloat() * 1000).toLong() // offset
                    val danmaku = DanmakuUtil. simpleDanmakuFactory.create(
                            s[1].toInt().toDanmakuType() // type
                    )
                    danmaku.text = text
                    danmaku.offset = offset
                    danmaku.textSize = s[2].toFloat() // textSize
                    val color =
                            (-0x1000000L or s[3].toLong() and -0x1).toInt()
                    danmaku.textColor = color
                    danmaku.textShadowColor =
                            if (color <= android.graphics.Color.BLACK) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                    if (danmaku is BiliSpecialDanmaku) initialSpecialDanmakuData(danmaku)
                    danmakus.add(danmaku) }
            } else {
                var depth = 1
                while (depth != 0) {
                    when (xmlParser.next()) {
                        XmlPullParser.END_TAG -> depth--
                        XmlPullParser.START_TAG -> depth++
                    }
                }
            }
        }
        danmakus
    }

    override fun parse() = danmakus

    private fun readText(xmlParser: XmlPullParser): String {
        var result = ""
        if (xmlParser.next() == XmlPullParser.TEXT) {
            result = xmlParser.text
            xmlParser.nextTag()
        }
        return result
    }
}
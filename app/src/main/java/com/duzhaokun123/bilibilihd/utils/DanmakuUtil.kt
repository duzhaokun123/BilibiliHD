package com.duzhaokun123.bilibilihd.utils

import com.google.gson.stream.JsonReader
import com.hiczp.bilibili.api.bounded
import com.hiczp.bilibili.api.readUInt
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.HashMap
import java.util.zip.GZIPInputStream

object DanmakuUtil {
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
}
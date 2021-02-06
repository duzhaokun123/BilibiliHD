package com.duzhaokun123.bilibilihd.grpcbiliclient

import bilibili.app.playurl.v1.PlayURLGrpc
import bilibili.app.playurl.v1.PlayurlV1
import bilibili.app.view.v1.ViewGrpc
import bilibili.app.view.v1.ViewV1
import com.duzhaokun123.bilibilihd.Application
import io.grpc.android.AndroidChannelBuilder

class GrpcBiliClient{
    private val channel = AndroidChannelBuilder.forTarget("grpc.biliapi.net").context(Application.getInstance()).build()

    private val viewStub = ViewGrpc.newBlockingStub(channel)
    private val playUrlStub = PlayURLGrpc.newBlockingStub(channel)

    fun view(aid: Long = 0, bvid: String? = null): ViewV1.ViewReply {
        val viewReq = ViewV1.ViewReq.newBuilder().apply {
            if (aid != 0L) this.aid = aid
            if (bvid != null) this.bvid = bvid
        }.build()
        return viewStub.view(viewReq)
    }

    fun playView(aid: Long, cid: Long): PlayurlV1.PlayViewReply {
        val playViewReq = PlayurlV1.PlayViewReq.newBuilder().apply {
            this.aid = aid
            this.cid = cid
//            qn = 32
//            fnver= 16
//            fnval = 0
//            download = 0
//            forceHost = 0
//            fourk = true
        }.build()
        return playUrlStub.playView(playViewReq)
    }

    fun shutdown() {
        channel.shutdown()
    }
}
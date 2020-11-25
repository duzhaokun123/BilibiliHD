package com.duzhaokun123.bilibilihd.ui.play.live

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.databinding.PlayExtLiveBinding
import com.duzhaokun123.bilibilihd.ui.play.base.BasePlayActivity
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity
import com.duzhaokun123.bilibilihd.ui.widget.BiliPlayerViewWrapperView
import com.duzhaokun123.bilibilihd.utils.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.hiczp.bilibili.api.live.model.AnchorInRoom
import com.hiczp.bilibili.api.live.model.RoomInfo
import com.hiczp.bilibili.api.weblive.model.PlayUrl
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class LivePlayActivity : BasePlayActivity<PlayExtLiveBinding>() {
    companion object {
        const val EXTRA_CID = "cid"

        fun enter(context: Context, cid: Long) {
            if (Settings.layout.isLiveUseWebView) {
                BrowserUtil.openWebViewActivity(context, "https://live.bilibili.com/$cid", false)
                return
            }
            context.startActivity(Intent(context, LivePlayActivity::class.java).apply {
                putExtra(EXTRA_CID, cid)
            })
        }
    }

    private var cid by Delegates.notNull<Long>()
    private lateinit var roomInfo: RoomInfo
    private lateinit var anchorInRoom: AnchorInRoom

    private lateinit var civFace: CircleImageView
    private lateinit var tvName: TextView

    override fun initConfig() = 0
    override fun initExtLayout() = R.layout.play_ext_live

    override fun findViews2() {
        civFace =   extBind.root. findViewById(R.id.civ_face)
        tvName = extBind.root. findViewById(R.id.tv_name)
    }

    override fun initView() {
        super.initView()
        setLive(true)
        baseBind.bpvwv.biliPlayerView.setOnIbNextClickListener(null)
    }

    override fun initData() {
        cid = startIntent.getLongExtra(EXTRA_CID, 0)
        extBind.tvId.text = cid.toString()
        loadRoomInfo()
        loadAnchorInRoom()
        loadLiveVideo()
        if (Settings.play.isAutoRecordingHistory) {
            enterAction()
        }
    }

    override fun onGetShareUrl() = "https://live.bilibili.com/${cid}"

    override fun onGetShareTitle() =
            if (::roomInfo.isInitialized) roomInfo.data.title else cid.toString()

    override fun onCheckCover() {
        if (::roomInfo.isInitialized) {
            ImageViewUtil.viewImage(this, roomInfo.data.userCover)
        }
    }

    override fun onDownload() {}

    override fun onStartAddToHistory() {
        enterAction()
    }

    override fun onSendDanmaku() {
        DanmakuSendDialog(this, cid).show()
    }

    private fun enterAction() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                pBilibiliClient.bilibiliClient.liveAPI.roomEntryAction(cid).await()
            } catch (e: Exception) {
                e.printStackTrace()
                kRunOnUiThread { TipUtil.showTip(this@LivePlayActivity, e.message) }
            }
        }
    }

    private fun loadRoomInfo() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                roomInfo = pBilibiliClient.bilibiliClient.liveAPI.getInfo(cid).await()
            } catch (e: Exception) {
                e.printStackTrace()
                kRunOnUiThread { TipUtil.showTip(this@LivePlayActivity, e.message) }
            }
            if (::roomInfo.isInitialized)
                kRunOnUiThread {
                    roomInfo.data.let { data ->
                        title = data.title
                        setCover(data.userCover)
                        extBind.tvTitle.text = data.title
                        extBind.htvDesc.setHtml(data.description)
                    }
                }
        }
    }

    private fun loadAnchorInRoom() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                anchorInRoom = pBilibiliClient.bilibiliClient.liveAPI.getAnchorInRoom(cid).await()
            } catch (e: Exception) {
                e.printStackTrace()
                kRunOnUiThread { TipUtil.showTip(this@LivePlayActivity, e.message) }
            }
            if (::anchorInRoom.isInitialized)
                kRunOnUiThread {
                    anchorInRoom.data.info.let { info ->
                        Glide.with(this@LivePlayActivity).load(info.face).into(civFace)
                        tvName.text = info.uname
                        val enterUserSpace = { _: View ->
                            UserSpaceActivity.enter(this@LivePlayActivity, info.uid, civFace, null)
                        }
                        civFace.setOnClickListener(enterUserSpace)
                        tvName.setOnClickListener(enterUserSpace)
                    }
                }
        }
    }

    private fun loadLiveVideo() {
        GlobalScope.launch(Dispatchers.IO) {
            val pns = listOf(80, 150, 400, 10000)
            val playUrls = mutableListOf<PlayUrl>()
            pns.forEach { pn ->
                try {
                    playUrls.add(pBilibiliClient.bilibiliClient.webLiveAPI.playUrl(cid, "h5", pn).await())
                } catch (e: Exception) {
                    e.printStackTrace()
                    com.duzhaokun123.bilibilihd.utils.runOnUiThread { TipUtil.showTip(this@LivePlayActivity, e.message) }
                }
            }

            playUrls.let { urls ->
                com.duzhaokun123.bilibilihd.utils.runOnUiThread {
                    setVideoMediaSourceAdapter(object : BiliPlayerViewWrapperView.VideoMediaSourceAdapter {
                        val dataSourceFactory = DefaultHttpDataSourceFactory(pBilibiliClient.bilibiliClientProperties.defaultUserAgent)

                        override fun getMediaSources(id: Int): MutableList<Pair<String, MediaSource>>? {
                            val mediaSources: MutableList<Pair<String, MediaSource>> = ArrayList()
                            urls.forEach { url ->
                                if (url.data.currentQn == id) {
                                    url.data.durl.forEachIndexed { index, durl ->
                                        mediaSources.add(Pair(if (index == 0) getString(R.string.main_line) else getString(R.string.spare_line_d, index),
                                                HlsMediaSource.Factory(dataSourceFactory)
                                                        .createMediaSource(Uri.parse(durl.url))))
                                    }
                                    return if (mediaSources.isEmpty()) null else mediaSources
                                }
                            }
                            return null
                        }

                        override val count
                            get() = if (urls.size > 0)
                                urls[0].data.qualityDescription.size
                            else
                                0


                        override fun getName(index: Int) = urls[0].data.qualityDescription[index].desc

                        override val defaultIndex
                            get() = 0

                        override fun getId(index: Int) = urls[0].data.qualityDescription[index].qn
                    })
                }
            }
        }
    }
}
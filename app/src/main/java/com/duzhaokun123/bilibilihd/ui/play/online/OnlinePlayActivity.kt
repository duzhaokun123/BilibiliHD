package com.duzhaokun123.bilibilihd.ui.play.online

import android.net.Uri
import android.os.Message
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.databinding.PlayExtOnlineBinding
import com.duzhaokun123.bilibilihd.ui.play.base.BasePlayActivity
import com.duzhaokun123.bilibilihd.ui.universal.reply.RootReplyFragment
import com.duzhaokun123.bilibilihd.ui.widget.BiliPlayerViewWrapperView
import com.duzhaokun123.bilibilihd.utils.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.hiczp.bilibili.api.player.model.VideoPlayUrl
import com.hiczp.bilibili.api.app.model.View as BiliView

class OnlinePlayActivity : BasePlayActivity<PlayExtOnlineBinding>() {
    companion object {
        const val EXTRA_FAST_LOAD_COVER_URL = "fast_load_cover_url"
        const val EXTRA_AID = "aid"
        const val EXTRA_BVID = "bvid"
        const val EXTRA_PAGE = "page"

        const val WHAT_LOAD_BILIVIEW = 0
        const val WHAT_BILIVIEW_LOAD_OVER = 1
        const val WHAT_INTRO_FRAGMENT_SEND_BACK = 2
        const val WHAT_ADD_HISTORY = 3
    }

    private var introFragment: IntroFragment? = null
    private var rootReplyFragment: RootReplyFragment? = null

    private var videoPlayUrl: VideoPlayUrl? = null
    private var biliView: BiliView? = null
    private var aid = 0L
    private var cid = 0L
    private var page = 0

    private var isActivityStopped = true

    override fun initConfig() = NEED_HANDLER

    override fun initView() {
        super.initView()
        setCover(startIntent.getStringExtra(EXTRA_FAST_LOAD_COVER_URL))
    }

    override fun initData() {
        if (aid == 0L) {
            aid = startIntent.getLongExtra(EXTRA_AID, 0)
            if (aid == 0L) {
                aid = MyBilibiliClientUtil.bv2av(startIntent.getStringExtra(EXTRA_BVID))
            }
            page = startIntent.getIntExtra(EXTRA_PAGE, 1)
            handler?.sendEmptyMessage(WHAT_LOAD_BILIVIEW)
        }
        title = ""
        val model: RootReplyFragment.AllCountViewModel by viewModels()
        model.allCount.observe(this, { allCount ->
            extBind.tl.getTabAt(1)?.text = getString(R.string.comment_num, allCount)
        })
    }

    override fun onStop() {
        super.onStop()
        isActivityStopped = true
    }

    override fun onStart() {
        super.onStart()
        isActivityStopped = false
    }

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_LOAD_BILIVIEW ->
                Thread {
                    try {
                        biliView = Application.getPBilibiliClient().pAppAPI.view(aid)
                        handler?.sendEmptyMessage(WHAT_BILIVIEW_LOAD_OVER)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread { TipUtil.showTip(this, e.message) }
                    }
                }.start()
            WHAT_BILIVIEW_LOAD_OVER -> {
                extBind.vp.adapter = MyFragmentStateAdapter(this)
                TabLayoutMediator(extBind.tl, extBind.vp) { tab, position ->
                    when (position) {
                        0 -> tab.setText(R.string.intro)
                        1 -> tab.text = getString(R.string.comment_num, biliView?.data?.stat?.reply)
                    }
                }.attach()
                setCover(biliView?.data?.pic)
                title = biliView?.data?.title
                biliView?.data?.history?.let { history ->
                    var p = 0
                    for (page in biliView!!.data.pages) {
                        if (page.cid == history.cid) {
                            p = page.page
                        }
                    }
                    Snackbar.make(baseBind.clRoot,
                            getString(R.string.last_time_view_to_dp_s, p, DateTimeFormatUtil.getStringForTime(history.progress * 1000)),
                            BaseTransientBottomBar.LENGTH_LONG)
                            .setAction(R.string.jump) {
                                if (page != p) {
                                    val message = Message()
                                    message.what = IntroFragment.WHAT_LOAD_NEW_PAGE
                                    message.arg1 = p
                                    introFragment?.handler?.sendMessage(message)
                                }
                                Thread {
                                    Thread.sleep(1000)
                                    runOnUiThread {
                                        setCover(null)
                                        seekTo(history.progress * 1000)
                                    }
                                }.start()
                            }
                            .show()
                }
            }
            WHAT_INTRO_FRAGMENT_SEND_BACK -> {
                videoPlayUrl = GsonUtil.getGsonInstance().fromJson(msg.data.getString("videoPlayUrl"), VideoPlayUrl::class.java)
                page = msg.data.getInt("page")
                cid = biliView!!.data.pages[page - 1].cid.toLong()
                baseBind.bpvwv.biliPlayerView.loadShot(aid, cid)
                loadDanmakuByAidCid(aid, cid, biliView!!.data.pages[page - 1].duration)
                setVideoMediaSourceAdapter(object : BiliPlayerViewWrapperView.VideoMediaSourceAdapter {
                    val dataSourceFactory = DefaultDataSourceFactory(Application.getInstance(), pBilibiliClient.bilibiliClientProperties.defaultUserAgent)

                    override fun getMediaSources(id: Int): MutableList<Pair<String, MediaSource>>? {
                        var videoUrl: String? = null
                        val audioUrl: String? = videoPlayUrl!!.data.dash.audio?.get(0)?.baseUrl
                        var videoIndex = 0
                        videoPlayUrl!!.data.dash.video.forEachIndexed { index, video ->
                            if (video.id == id) {
                                videoUrl = video.baseUrl
                                videoIndex = index
                            }
                        }
                        val mediaSources: MutableList<Pair<String, MediaSource>> = ArrayList()
                        when {
                            videoUrl == null -> TipUtil.showTip(this@OnlinePlayActivity, R.string.not_vip)
                            audioUrl != null -> {
                                val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                                        .createMediaSource(Uri.parse(videoUrl))
                                val audioSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                                        .createMediaSource(Uri.parse(audioUrl))
                                mediaSources.add(Pair(getString(R.string.main_line), MergingMediaSource(videoSource, audioSource)))
                                videoPlayUrl!!.data.dash.video[videoIndex].backupUrl.forEachIndexed { index, url ->
                                    val backupVideoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                                            .createMediaSource(Uri.parse(url))
                                    mediaSources.add(Pair(getString(R.string.spare_line_d, index + 1), MergingMediaSource(backupVideoSource, audioSource)))
                                }
                            }
                            else -> {
                                mediaSources.add(Pair(getString(R.string.main_line), ProgressiveMediaSource.Factory(dataSourceFactory)
                                        .createMediaSource(Uri.parse(videoUrl))))
                                videoPlayUrl!!.data.dash.video[videoIndex].backupUrl.forEachIndexed { index, url ->
                                    mediaSources.add(Pair(getString(R.string.spare_line_d, index + 1), ProgressiveMediaSource.Factory(dataSourceFactory)
                                            .createMediaSource(Uri.parse(url))))
                                }
                            }
                        }
                        return if (mediaSources.isEmpty()) null else mediaSources
                    }

                    override val defaultIndex
                        get() = if (Settings.play.defaultQualityType == 1) {
                            videoPlayUrl!!.data.acceptQuality.indexOf(videoPlayUrl!!.data.quality)
                        } else {
                            videoPlayUrl!!.data.acceptQuality.indexOf(videoPlayUrl!!.data.dash.video[0].id)
                        }

                    override fun getName(index: Int) = videoPlayUrl!!.data.acceptDescription[index]

                    override val count
                        get() = videoPlayUrl!!.data.acceptQuality.size

                    override fun getId(index: Int) = videoPlayUrl!!.data.acceptQuality[index]
                })
                biliView!!.data.pages[page - 1].dimension.let {
                    if (it.rotate == 0) {
                        setWidthHeight(it.width, it.height)
                    } else {
                        setWidthHeight(it.height, it.width)
                    }
                }
                biliView!!.data.pages.let {
                    if (it.size > page) {
                        baseBind.bpvwv.biliPlayerView.setOnIbNextClickListener {
                            val message = Message()
                            message.what = IntroFragment.WHAT_LOAD_NEW_PAGE
                            message.arg1 = page + 1
                            introFragment?.handler?.sendMessage(message)
                        }
                    } else {
                        baseBind.bpvwv.biliPlayerView.setOnIbNextClickListener(null)
                    }
                }
                setNotificationContentText(biliView?.data?.pages?.get(page - 1)?.part)
            }
            WHAT_ADD_HISTORY -> {
                val playedTime = baseBind.bpvwv.player.currentPosition
                Thread {
                    try {
                        pBilibiliClient.pWebAPI.heartbeat(aid = aid, cid = cid, playedTime = playedTime / 1000)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread { TipUtil.showToast(e.message) }
                    }
                    Thread.sleep(15000)
                    if (isActivityStopped) {
                        Thread.yield()
                    }
                    if (Settings.play.isAutoRecordingHistory) {
                        handler?.sendEmptyMessage(WHAT_ADD_HISTORY)
                    }
                }.start()
            }
        }
    }

    inner class MyFragmentStateAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                if (introFragment == null) {
                    introFragment = IntroFragment.getInstance(biliView, aid, page)
                }
                introFragment!!
            } else {
                if (rootReplyFragment == null) {
                    rootReplyFragment = RootReplyFragment(aid, 3, 1)
                }
                rootReplyFragment!!
            }
        }

        override fun getItemCount() = 2
    }

    override fun initExtLayout() = R.layout.play_ext_online

    override fun onGetShareUrl() = MyBilibiliClientUtil.getB23Url(aid) ?: ""

    override fun onGetShareTitle() = biliView?.data?.title

    override fun onCheckCover() {
        biliView?.let { ImageViewUtil.viewImage(this, it.data.pic) }
    }

    override fun onDownload() {
        TipUtil.showTip(this, "没有实现")
    }

    override fun onStartAddToHistory() {
        handler?.sendEmptyMessage(WHAT_ADD_HISTORY)
    }

    override fun onSendDanmaku() {
        TipUtil.showTip(this, "没有实现")
    }

    override fun onReloadDanmaku() {
        if (biliView != null)
            loadDanmakuByAidCid(aid, cid, biliView!!.data.pages[page - 1].duration)
    }
}
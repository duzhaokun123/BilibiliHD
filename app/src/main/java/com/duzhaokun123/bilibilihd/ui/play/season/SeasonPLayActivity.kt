package com.duzhaokun123.bilibilihd.ui.play.season

import android.annotation.SuppressLint
import android.net.Uri
import android.os.SystemClock
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.setPadding
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.databinding.PlayExtSeasonBinding
import com.duzhaokun123.bilibilihd.ui.play.base.BasePlayActivity
import com.duzhaokun123.bilibilihd.ui.widget.BiliPlayerViewWrapperView
import com.duzhaokun123.bilibilihd.utils.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.hiczp.bilibili.api.main.model.Season
import com.hiczp.bilibili.api.md5
import com.hiczp.bilibili.api.player.model.BangumiPlayUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("SetTextI18n")
class SeasonPLayActivity : BasePlayActivity<PlayExtSeasonBinding>() {
    companion object {
        const val EXTRA_SSID = "ssid"
    }

    lateinit var season: Season
    var bangumiPlayUrl: BangumiPlayUrl? = null

    override fun initExtLayout() = R.layout.play_ext_season

    override fun initConfig() = 0

    override fun initData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                season = bilibiliClient.mainAPI.season(seasonId = startIntent.getLongExtra(EXTRA_SSID, 0)).await()
                kRunOnUiThread {
                    extBind.season = season
                    setCover(season.result.cover)

                    season.result.episodes.forEach { ep ->
                        val rb = RadioButton(this@SeasonPLayActivity).apply {
                            text = """
                                第 ${ep.title} 话
                                ${ep.longTitle}
                            """.trimIndent() // TODO: 21-1-21 i18n
                            buttonDrawable = null
                            setBackgroundResource(R.drawable.rb_video_page_bg)
                            setTextColor(getColorStateList(R.color.rb_video_page_text))
                            setPadding(OtherUtils.dp2px(10F))
                            setOnClickListener {
                                title = "第${ep.title}话-${ep.longTitle}"
                                GlobalScope.launch(Dispatchers.IO) {
                                    try {
                                        bangumiPlayUrl = bilibiliClient.playerAPI.bangumiPlayUrl(ep.aid.toLong(), ep.cid.toLong(), session = SystemClock.uptimeMillis().toString().md5()).await()
                                        kRunOnUiThread {
                                            setVideoMediaSourceAdapter(object : BiliPlayerViewWrapperView.VideoMediaSourceAdapter { // TODO: 21-1-21
                                                val dataSourceFactory = DefaultDataSourceFactory(Application.getInstance(), pBilibiliClient.bilibiliClientProperties.defaultUserAgent)

                                                override fun getMediaSources(id: Int): List<Pair<String, MediaSource>>? {
                                                    var videoUrl: String? = null
                                                    val audioUrl = bangumiPlayUrl!!.dash.audio[0].baseUrl
                                                    var videoIndex = 0
                                                    bangumiPlayUrl!!.dash.video.forEachIndexed { index, video ->
                                                        if (video.id == id) {
                                                            videoUrl = video.baseUrl
                                                            videoIndex = index
                                                        }
                                                    }
                                                    val mediaSources: MutableList<Pair<String, MediaSource>> = ArrayList()
                                                    when (videoUrl) {
                                                        null -> TipUtil.showTip(this@SeasonPLayActivity, R.string.not_vip)
                                                        else -> {
                                                            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                                                                    .createMediaSource(Uri.parse(videoUrl))
                                                            val audioSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                                                                    .createMediaSource(Uri.parse(audioUrl))
                                                            mediaSources.add(Pair(getString(R.string.main_line), MergingMediaSource(videoSource, audioSource)))
                                                            bangumiPlayUrl!!.dash.video[videoIndex].backupUrl.forEachIndexed { index, url ->
                                                                val backupVideoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                                                                        .createMediaSource(Uri.parse(url))
                                                                mediaSources.add(Pair(getString(R.string.spare_line_d, index + 1), MergingMediaSource(backupVideoSource, audioSource)))
                                                            }
                                                        }
                                                    }
                                                    return if (mediaSources.isEmpty()) null else mediaSources
                                                }

                                                override val count
                                                get() = bangumiPlayUrl!!.acceptQuality.size

                                                override fun getName(index: Int) = bangumiPlayUrl!!.acceptDescription[index]

                                                override val defaultIndex: Int
                                                    get() = if (Settings.play.defaultQualityType == 1) {
                                                        bangumiPlayUrl!!.acceptQuality.indexOf(bangumiPlayUrl!!.quality)
                                                    } else {
                                                        bangumiPlayUrl!!.acceptQuality.indexOf(bangumiPlayUrl!!.dash.video[0].id)
                                                    }

                                                override fun getId(index: Int) = bangumiPlayUrl!!.acceptQuality[index]

                                            })
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        kRunOnUiThread { TipUtil.showTip(this@SeasonPLayActivity, e.message) }
                                    }
                                }
                            }
                        }
                        val params = RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            rightMargin = OtherUtils.dp2px(5F)
                        }
                        extBind.rgPages.addView(rb, params)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                kRunOnUiThread { TipUtil.showTip(this@SeasonPLayActivity, e.message) }
            }
        }
    }

    override fun onGetShareUrl() = season.result.shareUrl

    override fun onGetShareTitle() = season.result.title

    override fun onCheckCover() {
        ImageViewUtil.viewImage(this, season.result.cover)
    }

    override fun onDownload() {
//        TODO("Not yet implemented")
    }

    override fun onStartAddToHistory() {
//        TODO("Not yet implemented")
    }

    override fun onSendDanmaku() {
//        TODO("Not yet implemented")
    }

    override fun onReloadDanmaku() {
//        TODO("Not yet implemented")
    }
}
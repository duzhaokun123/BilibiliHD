package com.duzhaokun123.bilibilihd.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.databinding.LayoutBiliPlayerViewWapperViewBinding
import com.duzhaokun123.bilibilihd.utils.GlideUtil
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.duzhaokun123.danmakuview.interfaces.DanmakuParser
import com.duzhaokun123.danmakuview.model.Danmakus
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerControlView

class BiliPlayerViewWrapperView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private lateinit var baseBind: LayoutBiliPlayerViewWapperViewBinding

    var onPlayingStatusChangeListener: ((playState: PlayingStatus) -> Unit)? = null

    var onPlayerErrorListener: ((error: ExoPlaybackException) -> Unit)? = null

    var videoMediaSourceAdapter: VideoMediaSourceAdapter? = null
        set(value) {
            field = value
            player.seekTo(0)
            if (value != null) {
                baseBind.bpv.btnLine.visibility = VISIBLE
                baseBind.bpv.btnQuality.visibility = VISIBLE
                changeQuality(value.defaultIndex)
            } else {
                baseBind.bpv.btnLine.visibility = GONE
                baseBind.bpv.btnQuality.visibility = GONE
            }
        }

    lateinit var player: SimpleExoPlayer
        private set

    var qualityId = 0

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        player.addListener(object : Player.EventListener {
            override fun onSeekProcessed() {
                baseBind.bpv.danmakuSeekTo(player.contentPosition)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    baseBind.bpv.danmakuResume()
                    baseBind.bpv.setPbExoBufferingVisibility(INVISIBLE)
                    if (onPlayingStatusChangeListener != null) {
                        onPlayingStatusChangeListener!!(PlayingStatus.PLAYING)
                    }
                } else {
                    baseBind.bpv.danmakuPause()
                    if (onPlayingStatusChangeListener != null) {
                        onPlayingStatusChangeListener!!(PlayingStatus.PAUSED)
                    }
                    val contentPosition = player.contentPosition
                    val contentDuration = player.contentDuration
                    val contentBufferedPosition = player.contentBufferedPosition
                    if (contentBufferedPosition - contentPosition <= 1000 && contentDuration - contentPosition > 100) {
                        baseBind.bpv.setPbExoBufferingVisibility(VISIBLE)
                        if (onPlayingStatusChangeListener != null) {
                            onPlayingStatusChangeListener!!(PlayingStatus.LOADING)
                        }
                    } else {
                        baseBind.bpv.showController()
                        if (onPlayingStatusChangeListener != null) {
                            onPlayingStatusChangeListener!!(PlayingStatus.ENDED)
                        }
                    }
                }
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                if (onPlayingStatusChangeListener != null) {
                    onPlayingStatusChangeListener!!(PlayingStatus.ERROR)
                }
                if (onPlayerErrorListener != null) {
                    onPlayerErrorListener!!(error)
                }
            }
        })
        baseBind.ivCover.setOnClickListener {
            setCover(null)
            resume()
        }
        baseBind.bpv.setDanmakuLoadListener { e: Exception? ->
            if (e != null) {
                TipUtil.showTip(context, e.message)
            }
        }
        baseBind.bpv.btnQuality.setOnClickListener {
            if (videoMediaSourceAdapter == null) {
                return@setOnClickListener
            }
            val popupMenu = PopupMenu(context, baseBind.bpv.btnQuality)
            val menu = popupMenu.menu
            for (i in 0 until videoMediaSourceAdapter!!.count) {
                val name = videoMediaSourceAdapter!!.getName(i)
                menu.add(0, i, i, name ?: i.toString())
            }
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                changeQuality(menuItem.itemId)
                true
            }
            popupMenu.show()
        }
        baseBind.bpv.setOnClickListener(object : OnClickListener {
            private var lastClickTime: Long = 0
            override fun onClick(v: View) {
                val thisClickTime = System.currentTimeMillis()
                if (thisClickTime - lastClickTime < 400) {
                    if (player.playWhenReady) {
                        pause()
                    } else {
                        resume()
                    }
                }
                lastClickTime = thisClickTime
            }
        })
    }

    fun release() {
        player.release()
        baseBind.bpv.release()
    }

    fun setCover(url: String?) {
        if (url != null) {
            baseBind.ivCover.visibility = VISIBLE
            GlideUtil.loadUrlInto(context, url, baseBind.ivCover, false)
        } else {
            baseBind.ivCover.visibility = GONE
            baseBind.ivCover.setImageDrawable(null)
        }
    }

    fun pause() {
        player.playWhenReady = false
        baseBind.bpv.danmakuPause()
    }

    fun resume() {
        player.playWhenReady = true
        baseBind.bpv.danmakuResume()
    }

    var onFullscreenClickListener: OnFullscreenClickListener?
        get() = baseBind.bpv.onFullscreenClickListener
        set(onFullscreenClickListener) {
            baseBind.bpv.onFullscreenClickListener = onFullscreenClickListener
        }

    fun setControllerVisibilityListener(visibilityListener: PlayerControlView.VisibilityListener?) {
        baseBind.bpv.setControllerVisibilityListener(visibilityListener)
    }

    val biliPlayerView: BiliPlayerView
        get() = baseBind.bpv

    fun loadDanmakuByAidCid(aid: Long, cid: Long, durationS: Int) {
        baseBind.bpv.loadDanmakuByAidCid(aid, cid, durationS)
    }

    fun loadDanmakuByBiliDanmakuParser(danmakuParser: DanmakuParser, onEnd: ((danmakus: Danmakus) -> Unit)? =null) {
        baseBind.bpv.danmakuView.parse(danmakuParser, onEnd)
    }

    private fun changeQuality(index: Int) {
        if (videoMediaSourceAdapter != null) {
            val count = videoMediaSourceAdapter!!.count
            if (count > index) {
                val qualityIdTemp = videoMediaSourceAdapter!!.getId(index)
                baseBind.bpv.btnQuality.visibility = VISIBLE
                val mediaSources = videoMediaSourceAdapter!!.getMediaSources(qualityIdTemp)
                if (mediaSources != null) {
                    updateMediaSource(mediaSources[0].second)
                    baseBind.bpv.btnQuality.text = videoMediaSourceAdapter!!.getName(index)
                    baseBind.bpv.btnLine.text = mediaSources[0].first
                    qualityId = qualityIdTemp
                    baseBind.bpv.btnLine.setOnClickListener(
                            object : OnClickListener {
                                private var order = 0
                                override fun onClick(v: View) {
                                    val popupMenu = PopupMenu(context, v)
                                    for (i in mediaSources.indices) {
                                        popupMenu.menu.add(0, i, i, mediaSources[i].first)
                                    }
                                    popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                                        if (order != item.order) {
                                            order = item.order
                                            updateMediaSource(mediaSources[order].second)
                                            baseBind.bpv.btnLine.text = mediaSources[order].first
                                        }
                                        true
                                    }
                                    popupMenu.show()
                                }
                            }
                    )
                }
            }
        }
    }

    val isPlaying: Boolean
        get() = player.isPlaying

    fun clickIbFullscreen() {
        baseBind.bpv.clickIbFullscreen()
    }

    fun syncDanmakuProgress() {
        baseBind.bpv.danmakuView.seekTo(player.contentPosition)
    }

    fun setLive(isLive: Boolean) {
        baseBind.bpv.setLive(isLive)
    }

    fun setOnDanmakuSendClickListener(listener: OnClickListener?) {
        baseBind.bpv.setDanmakuSendClickListener(listener)
    }

    private fun updateMediaSource(mediaSource: MediaSource) {
        val playedTime = player.currentPosition
        player.prepare(mediaSource)
        player.seekTo(playedTime)
    }

    interface OnFullscreenClickListener {
        fun onClick(isFullscreen: Boolean)
    }

    interface VideoMediaSourceAdapter {
        fun getMediaSources(id: Int): List<Pair<String, MediaSource>>?
        val count: Int
        fun getName(index: Int): String?
        val defaultIndex: Int
        fun getId(index: Int): Int
    }

    enum class PlayingStatus {
        PLAYING, PAUSED, LOADING, ENDED, ERROR
    }

    init {
        if (isInEditMode) {
            LayoutInflater.from(context).inflate(R.layout.layout_bili_player_view_wapper_view, this)

        } else {
            baseBind = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_bili_player_view_wapper_view, this, true)
            player = SimpleExoPlayer.Builder(context).build()
            baseBind.bpv.player = player
        }
    }
}
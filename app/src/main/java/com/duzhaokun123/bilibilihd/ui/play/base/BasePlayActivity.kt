package com.duzhaokun123.bilibilihd.ui.play.base

import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Rational
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.mediarouter.app.MediaRouteActionProvider
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import com.bumptech.glide.Glide
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.ActivityPlayBaseBinding
import com.duzhaokun123.bilibilihd.services.PlayControlService
import com.duzhaokun123.bilibilihd.ui.settings.SettingsDanmakuFragment
import com.duzhaokun123.bilibilihd.ui.settings.SettingsPlayFragment
import com.duzhaokun123.bilibilihd.ui.widget.BiliPlayerViewWrapperView
import com.duzhaokun123.bilibilihd.utils.*
import com.duzhaokun123.danmakuview.interfaces.DanmakuParser
import com.duzhaokun123.danmakuview.model.Danmakus
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BasePlayActivity<extLayout : ViewDataBinding> : BaseActivity<ActivityPlayBaseBinding>() {
    companion object {
        const val ACTION_FINISH = "com.duzhaokun123.bilibilihd.action.FINISH"
    }

    protected lateinit var extBind: extLayout

    private var showingFragment: Fragment? = null

    private var pictureInPictureParamsBuilder: PictureInPictureParams.Builder = PictureInPictureParams.Builder()
    private var notificationBuilder: NotificationCompat.Builder? = null
    private lateinit var mediaRouter: MediaRouter
    private var presentation: PlayPresentation? = null
    private var remotePlayControlFragment: Fragment? = null
    private var mediaRouteSelector: MediaRouteSelector? = null
    private val finishBroadcastReceiver = FinishBroadcastReceiver()

    private var playId = 0L
    private val notificationId = NotificationUtil.getNewId()
    private var isFullscreen = false
    private var isPlayingBeforeActivityPause = false
    private var firstPlay = true
    private var isLive = false

    private val mediaRouteCallback = object : MediaRouter.Callback() {
        override fun onRouteSelected(router: MediaRouter?, route: MediaRouter.RouteInfo?) {
            updatePresentation()
        }

        override fun onRouteUnselected(router: MediaRouter?, route: MediaRouter.RouteInfo?, reason: Int) {
            updatePresentation()
        }

        override fun onRoutePresentationDisplayChanged(router: MediaRouter?, route: MediaRouter.RouteInfo?) {
            updatePresentation()
        }
    }

    final override fun initLayout() = R.layout.activity_play_base

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        notificationBuilder?.setContentTitle(title)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(finishBroadcastReceiver, IntentFilter().apply {
            addAction(ACTION_FINISH)
        })
    }

    override fun initView() {
        extBind = DataBindingUtil.inflate(layoutInflater, initExtLayout(), baseBind.flExt, true)
        findViews2()

        baseBind.bpvwv.onFullscreenClickListener = object : BiliPlayerViewWrapperView.OnFullscreenClickListener {
            override fun onClick(isFullscreen: Boolean) {
                this@BasePlayActivity.isFullscreen = isFullscreen
                if (this@BasePlayActivity.isFullscreen) {
                    baseBind.ml.transitionToEnd()
                    if (presentation == null) {
                        supportActionBar?.hide()
                    }
                    if (isInMultiWindowMode.not()) {
                        window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                    }
                } else {
                    baseBind.ml.transitionToStart()
                    supportActionBar?.show()
                    window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                            and (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY).inv())
                }
            }
        }
        baseBind.bpvwv.setControllerVisibilityListener { visibility ->
            if (visibility != View.VISIBLE) {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE
                if (isFullscreen && isInMultiWindowMode.not()) {
                    window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                }
                if (presentation == null) {
                    supportActionBar?.hide()
                }
            } else {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
                if (isFullscreen && isInMultiWindowMode.not()) {
                    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
                }
                supportActionBar?.show()
            }
        }
        baseBind.bpvwv.onPlayingStatusChangeListener = { playingStatus ->
            when (playingStatus) {
                BiliPlayerViewWrapperView.PlayingStatus.PLAYING -> {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    if (firstPlay) {
                        firstPlay = false
                        if (Settings.play.isAutoRecordingHistory) {
                            onStartAddToHistory()
                        }
                    }
                    val actions: MutableList<RemoteAction> = ArrayList()
                    actions.add(RemoteAction(
                            Icon.createWithResource(this, R.drawable.ic_pause),
                            getString(R.string.pause),
                            getString(R.string.pause),
                            PlayControlService.newPausePendingIntent(this, playId)))
                    pictureInPictureParamsBuilder.setActions(actions)
                    setPictureInPictureParams(pictureInPictureParamsBuilder.build())
                    notificationBuilder?.setSmallIcon(R.drawable.ic_play_arrow)
                    NotificationUtil.reshow(notificationId, notificationBuilder?.build())
                    NotificationUtil.setNotificationCleanable(notificationId, false)
                }
                BiliPlayerViewWrapperView.PlayingStatus.PAUSED -> {
                    val actions: MutableList<RemoteAction> = ArrayList()
                    actions.add(RemoteAction(
                            Icon.createWithResource(this, R.drawable.ic_play_arrow),
                            getString(R.string.resume),
                            getString(R.string.resume),
                            PlayControlService.newResumePendingIntent(this, playId)))
                    pictureInPictureParamsBuilder.setActions(actions)
                    setPictureInPictureParams(pictureInPictureParamsBuilder.build())
                    notificationBuilder?.setSmallIcon(R.drawable.ic_pause)
                    NotificationUtil.reshow(notificationId, notificationBuilder?.build())
                }
                BiliPlayerViewWrapperView.PlayingStatus.ENDED -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                else -> OtherUtils.doNothing() // TODO: 20-7-16
            }
        }
        baseBind.bpvwv.onPlayerErrorListener = { error ->
            error.printStackTrace()
            Snackbar.make(baseBind.clRoot, error.message!!, BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry) { baseBind.bpvwv.player.retry() }
                    .show()
        }
        baseBind.bpvwv.setOnDanmakuSendClickListener { onSendDanmaku() }
        baseBind.bpvwv.biliPlayerView.danmakuView.drawDebugInfo = Settings.danmaku.isDrawDebugInfo

        baseBind.dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        baseBind.dl.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerClosed(drawerView: View) {
                baseBind.dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                supportFragmentManager.beginTransaction().remove(showingFragment!!).commitAllowingStateLoss()
                showingFragment = null
            }

            override fun onDrawerOpened(drawerView: View) {
                baseBind.dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        })

        mediaRouteSelector = MediaRouteSelector.Builder()
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                .build()
        mediaRouter = MediaRouter.getInstance(this)

        playId = System.currentTimeMillis()
        PlayControlService.putId(playId, object : PlayControlService.ICallback {
            override fun onPause() {
                runOnUiThread { baseBind.bpvwv.pause() }
            }

            override fun onResume() {
                runOnUiThread { baseBind.bpvwv.resume() }
            }
        })

        notificationBuilder = NotificationCompat.Builder(this, NotificationUtil.CHANNEL_ID_VIDEO_PLAY_BACKGROUND)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
                .setSmallIcon(R.drawable.ic_pause)
                .setShowWhen(false)
                .addAction(R.drawable.ic_pause, getString(R.string.pause), PlayControlService.newPausePendingIntent(this, playId))
                .addAction(R.drawable.ic_play_arrow, getString(R.string.resume), PlayControlService.newResumePendingIntent(this, playId))

        if (Settings.danmaku.danmakuVisibility != 0) {
            baseBind.bpvwv.biliPlayerView.danmakuHide()
        }

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.statusBarColor = Color.TRANSPARENT
    }

    final override fun initRegisterCoordinatorLayout() = baseBind.clRoot

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.play_activity, menu)
        if (isLive) {
            menu?.let {
                it.findItem(R.id.download).isVisible = false
                it.findItem(R.id.sync_danmaku_progress).isVisible = false
            }
        }

        val mediaRouteItem = menu?.findItem(R.id.media_route)
        val mediaRouteActionProvider = MenuItemCompat.getActionProvider(mediaRouteItem) as MediaRouteActionProvider
        mediaRouteSelector?.also(mediaRouteActionProvider::setRouteSelector)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                ShareUtil.shareUrl(this, onGetShareUrl(), onGetShareTitle())
                true
            }
            R.id.open_in_browser -> {
                BrowserUtil.openCustomTab(this, onGetShareUrl())
                true
            }
            R.id.pip -> {
                val actions: MutableList<RemoteAction> = ArrayList()
                if (baseBind.bpvwv.isPlaying) {
                    actions.add(RemoteAction(
                            Icon.createWithResource(this, R.drawable.ic_pause),
                            getString(R.string.pause),
                            getString(R.string.pause),
                            PlayControlService.newPausePendingIntent(this, playId)))
                } else {
                    actions.add(RemoteAction(
                            Icon.createWithResource(this, R.drawable.ic_play_arrow),
                            getString(R.string.resume),
                            getString(R.string.resume),
                            PlayControlService.newResumePendingIntent(this, playId)))
                }
                pictureInPictureParamsBuilder.setActions(actions)
                enterPictureInPictureMode(pictureInPictureParamsBuilder.build())
                true
            }
            R.id.check_cover -> {
                onCheckCover()
                true
            }
            R.id.download -> {
                onDownload()
                true
            }
            R.id.add_to_history -> {
                onStartAddToHistory()
                true
            }
            R.id.retry -> {
                baseBind.bpvwv.player.retry()
                true
            }
            R.id.sync_danmaku_progress -> {
                baseBind.bpvwv.syncDanmakuProgress()
                true
            }
            R.id.play_settings -> {
                if (showingFragment == null) {
                    showingFragment = SettingsPlayFragment()
                    supportFragmentManager.beginTransaction().add(R.id.fl_end, showingFragment as Fragment).commitAllowingStateLoss()
                    baseBind.dl.openDrawer(GravityCompat.END)
                }
                true
            }
            R.id.danmaku_settings -> {
                if (showingFragment == null) {
                    showingFragment = SettingsDanmakuFragment()
                    supportFragmentManager.beginTransaction().add(R.id.fl_end, showingFragment as Fragment).commitAllowingStateLoss()
                    baseBind.dl.openDrawer(GravityCompat.END)
                }
                true
            }
            R.id.clean_danmaku_cache -> {
                baseBind.bpvwv.biliPlayerView.danmakuView.cleanCache()
                true
            }
            R.id.danmaku_reload -> {
                onReloadDanmaku()
                true
            }
            R.id.finish_background -> {
                sendBroadcast(Intent().apply {
                    action = ACTION_FINISH
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!Settings.play.isPlayBackground) {
            isPlayingBeforeActivityPause = baseBind.bpvwv.isPlaying
            baseBind.bpvwv.pause()
        } else {
            NotificationUtil.show(notificationId, notificationBuilder?.build()!!)
            if (baseBind.bpvwv.isPlaying) {
                NotificationUtil.setNotificationCleanable(notificationId, false)
            }
        }
        if (presentation != null) {
            presentation!!.dismiss()
            presentation!!.release()
            presentation = null
            remotePlayControlFragment?.let {
                supportFragmentManager.beginTransaction().remove(it as RemotePlayControlFragment).commitAllowingStateLoss()
                remotePlayControlFragment = null
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!Settings.play.isPlayBackground) {
            if (isPlayingBeforeActivityPause) {
                baseBind.bpvwv.resume()
            }
        } else {
            NotificationUtil.remove(notificationId)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFullscreen && isInMultiWindowMode.not()) {
            window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        mediaRouter.addCallback(mediaRouteSelector, mediaRouteCallback)
        updatePresentation()
    }

    override fun onPause() {
        super.onPause()
        mediaRouter.removeCallback(mediaRouteCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayControlService.removeId(playId)
        baseBind.bpvwv.release()
        NotificationUtil.remove(notificationId)
        unregisterReceiver(finishBroadcastReceiver)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        baseBind.ml.enableTransition(R.id.player_transition, isInPictureInPictureMode.not())
        if (isInPictureInPictureMode) {
            if (!isFullscreen) {
                baseBind.bpvwv.clickIbFullscreen()
            }
            baseBind.bpvwv.biliPlayerView.useController = false
        } else {
            baseBind.bpvwv.biliPlayerView.useController = true
        }
    }

    override fun onBackPressed() {
        when {
            showingFragment != null -> baseBind.dl.closeDrawer(GravityCompat.END)
            isFullscreen -> baseBind.bpvwv.clickIbFullscreen()
            else -> super.onBackPressed()
        }
    }

    fun updatePresentation() {
        val selectedRoute: MediaRouter.RouteInfo = mediaRouter.selectedRoute
        val selectedDisplay = selectedRoute.presentationDisplay
        if (presentation != null && presentation!!.display != selectedDisplay) {
            presentation!!.dismiss()
            presentation!!.release()
            presentation = null
            remotePlayControlFragment?.let {
                supportFragmentManager.beginTransaction().remove(it as RemotePlayControlFragment).commitAllowingStateLoss()
                remotePlayControlFragment = null
            }
        }

        if (presentation == null && selectedDisplay != null) {
            presentation = PlayPresentation(this, selectedDisplay, baseBind.bpvwv)
            remotePlayControlFragment = RemotePlayControlFragment(baseBind.bpvwv.player, selectedRoute.name) { baseBind.bpvwv.biliPlayerView.danmakuSwitch() }
            supportFragmentManager.beginTransaction().add(R.id.bpvwv, remotePlayControlFragment as RemotePlayControlFragment).commitAllowingStateLoss()
            presentation!!.setOnDismissListener {
                presentation?.release()
                presentation = null
                remotePlayControlFragment?.let {
                    supportFragmentManager.beginTransaction().remove(it as RemotePlayControlFragment).commitAllowingStateLoss()
                    remotePlayControlFragment = null
                }
            }
            try {
                presentation!!.show()
            } catch (e: WindowManager.InvalidDisplayException) {
                presentation!!.release()
                presentation = null
                remotePlayControlFragment?.let {
                    supportFragmentManager.beginTransaction().remove(it as RemotePlayControlFragment).commitAllowingStateLoss()
                    remotePlayControlFragment = null
                }
            }
        }
    }

    fun setCover(url: String?) {
        baseBind.bpvwv.setCover(url)
        if (url != null) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    notificationBuilder?.setLargeIcon(Glide.with(this@BasePlayActivity).asBitmap().load(url).submit().get())
                } catch (e: Exception) {
                    e.printStackTrace()
                    kRunOnUiThread { TipUtil.showTip(this@BasePlayActivity, e.message) }
                }
            }
        }
    }

    fun setWidthHeight(width: Int, height: Int) {
        val rational = Rational(width, height)
        if (rational.toDouble() > 0.418410 && rational.toDouble() < 2.390000) {
            pictureInPictureParamsBuilder.setAspectRatio(rational)
        }
    }

    fun setNotificationContentText(text: CharSequence?) {
        notificationBuilder?.setContentText(text)
    }

    fun seekTo(positionMs: Long) {
        baseBind.bpvwv.player.seekTo(positionMs)
    }

    fun loadDanmakuByAidCid(aid: Long, cid: Long, durationS: Int) {
        baseBind.bpvwv.loadDanmakuByAidCid(aid, cid, durationS)
    }

    @JvmOverloads
    fun loadDanmakuByBiliDanmakuParser(danmakuParser: DanmakuParser, onEnd: ((danmakus: Danmakus) -> Unit)? = null) {
        baseBind.bpvwv.loadDanmakuByBiliDanmakuParser(danmakuParser, onEnd)
    }

    fun setVideoMediaSourceAdapter(videoMediaSourceAdapter: BiliPlayerViewWrapperView.VideoMediaSourceAdapter?) {
        baseBind.bpvwv.videoMediaSourceAdapter = videoMediaSourceAdapter
    }

    fun setVideoMediaSource(mediaSource: MediaSource) {
        baseBind.bpvwv.player.prepare(mediaSource)
    }

    fun setLive(isLive: Boolean) {
        this.isLive = isLive
        baseBind.bpvwv.setLive(isLive)
    }

    abstract fun initExtLayout(): Int
    abstract fun onGetShareUrl(): String
    abstract fun onGetShareTitle(): String?
    abstract fun onCheckCover()
    abstract fun onDownload()
    abstract fun onStartAddToHistory()
    abstract fun onSendDanmaku()
    abstract fun onReloadDanmaku()

    protected open fun findViews2() {}

    inner class FinishBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_FINISH && this@BasePlayActivity.isStopped) {
                this@BasePlayActivity.finish()
            }
        }
    }
}
package com.duzhaokun123.bilibilihd.ui.play

import android.animation.ValueAnimator
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Message
import android.util.Rational
import android.view.*
import androidx.annotation.Nullable
import androidx.core.animation.doOnEnd
import androidx.core.app.NotificationCompat
import androidx.core.util.Pair
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.ActivityPlayBinding
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient
import com.duzhaokun123.bilibilihd.mybilibiliapi.history.HistoryAPI
import com.duzhaokun123.bilibilihd.mybilibiliapi.model.Base
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient
import com.duzhaokun123.bilibilihd.services.PlayControlService
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity
import com.duzhaokun123.bilibilihd.ui.settings.SettingsDanmakuFragment
import com.duzhaokun123.bilibilihd.ui.settings.SettingsPlayFragment
import com.duzhaokun123.bilibilihd.ui.widget.BiliPlayerViewPackageView
import com.duzhaokun123.bilibilihd.utils.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hiczp.bilibili.api.player.model.VideoPlayUrl
import com.hiczp.bilibili.api.app.model.View as BiliView

class PlayActivity : BaseActivity<ActivityPlayBinding>() {
    companion object {
        const val WHAT_LOAD_BILIVIEW = 0
        const val WHAT_BILIVIEW_LOAD_OVER = 1
        const val WHAT_INTRO_FRAGMENT_SEND_BACK = 2
        const val WHAT_ADD_HISTORY = 3
        const val WHAT_PAUSE = 4
        const val WHAT_RESUME = 5
    }

    private var introFragment: IntroFragment? = null
    private var commitFragment: CommitFragment? = null
    private var showingFragment: Fragment? = null
    private var pictureInPictureParamsBuilder: PictureInPictureParams.Builder = PictureInPictureParams.Builder()
    private var notificationBuilder: NotificationCompat.Builder? = null

    private var videoPlayUrl: VideoPlayUrl? = null
    private var biliView: BiliView? = null
    private var aid = 0L
    private var cid = 0L
    private var page = 0
    private var playId = 0L
    private var firstPlay = true
    private val notificationId = NotificationUtil.getNewId()
    private val bpvpvDefaultHeight = OtherUtils.dp2px(300f)

    private var isFullscreen = false
    private var isPlayingBeforeActivityPause = false
    private var allowFullscreenAnimation = true

    override fun initConfig() = NEED_HANDLER

    override fun initLayout() = R.layout.activity_play

    override fun initRegisterCoordinatorLayout() = baseBind.clRoot

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.play_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_in_browser -> {
                BrowserUtil.openCustomTab(this, MyBilibiliClientUtil.getB23Url(aid))
                true
            }
            R.id.pip -> {
                val actions: MutableList<RemoteAction> = ArrayList()
                if (baseBind.bpvpv.isPlaying) {
                    actions.add(RemoteAction(
                            Icon.createWithResource(this@PlayActivity, R.drawable.ic_pause),
                            getString(R.string.pause),
                            getString(R.string.pause),
                            PlayControlService.newPausePendingIntent(this, playId)))
                } else {
                    actions.add(RemoteAction(
                            Icon.createWithResource(this@PlayActivity, R.drawable.ic_play_arrow),
                            getString(R.string.resume),
                            getString(R.string.resume),
                            PlayControlService.newResumePendingIntent(this, playId)))
                }
                pictureInPictureParamsBuilder.setActions(actions)
                enterPictureInPictureMode(pictureInPictureParamsBuilder.build())
                true
            }
            R.id.check_cover -> {
                val pvIntent = Intent(this, PhotoViewActivity::class.java)
                pvIntent.putExtra("url", biliView?.data?.pic)
                startActivity(pvIntent)
                true
            }
            R.id.download -> {
                if (biliView != null && videoPlayUrl != null) {
                    VideoDownloadInfoDialog(this, biliView!!, videoPlayUrl!!, page, baseBind.bpvpv.qualityId).show()
                }
                true
            }
            R.id.add_to_history -> {
                handler?.sendEmptyMessage(WHAT_ADD_HISTORY)
                true
            }
            R.id.retry -> {
                baseBind.bpvpv.player.retry()
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun initView() {
        baseBind.tl.setupWithViewPager(baseBind.vp)
        baseBind.bpvpv.onFullscreenClickListener = BiliPlayerViewPackageView.OnFullscreenClickListener { isFullscreen ->
            this.isFullscreen = isFullscreen

            val newHeight: Int
            if (this.isFullscreen) {
                newHeight = baseBind.clRoot.height
                supportActionBar?.hide()
                window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            } else {
                newHeight = bpvpvDefaultHeight
                supportActionBar?.show()
                window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                        and (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY).inv())
            }

            val params = baseBind.bpvpv.layoutParams
            if (allowFullscreenAnimation) {
                val valueAnimator = ValueAnimator.ofInt(baseBind.bpvpv.height, newHeight)
                valueAnimator.addUpdateListener { animation ->
                    params.height = animation.animatedValue as Int
                    baseBind.bpvpv.layoutParams = params
                }
                if (newHeight == baseBind.clRoot.height) {
                    valueAnimator.doOnEnd {
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT
                        baseBind.bpvpv.layoutParams = params
                    }
                }
                valueAnimator.start()
            } else {
                params.height = newHeight
                if (params.height == baseBind.clRoot.height) {
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT
                }
                baseBind.bpvpv.layoutParams = params
            }
        }
        baseBind.bpvpv.setControllerVisibilityListener { visibility ->
            if (visibility != View.VISIBLE) {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE
                if (isFullscreen) {
                    window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                }
                supportActionBar?.hide()
            } else {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
                if (isFullscreen) {
                    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
                }
                supportActionBar?.show()
            }
        }
        baseBind.bpvpv.setOnPlayingStatusChangeListener { playingStatus ->
            when (playingStatus) {
                BiliPlayerViewPackageView.PlayingStatus.PLAYING -> {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    if (firstPlay) {
                        firstPlay = false
                        if (Settings.play.isAutoRecordingHistory) {
                            handler?.sendEmptyMessage(WHAT_ADD_HISTORY)
                        }
                    }
                    val actions: MutableList<RemoteAction> = ArrayList()
                    actions.add(RemoteAction(
                            Icon.createWithResource(this@PlayActivity, R.drawable.ic_pause),
                            getString(R.string.pause),
                            getString(R.string.pause),
                            PlayControlService.newPausePendingIntent(this, playId)))
                    pictureInPictureParamsBuilder.setActions(actions)
                    setPictureInPictureParams(pictureInPictureParamsBuilder.build())
                    notificationBuilder?.setSmallIcon(R.drawable.ic_play_arrow)
                    NotificationUtil.reshow(notificationId, notificationBuilder?.build())
                    NotificationUtil.setNotificationCleanable(notificationId, false)
                }
                BiliPlayerViewPackageView.PlayingStatus.PAUSED -> {
                    val actions: MutableList<RemoteAction> = ArrayList()
                    actions.add(RemoteAction(
                            Icon.createWithResource(this@PlayActivity, R.drawable.ic_play_arrow),
                            getString(R.string.resume),
                            getString(R.string.resume),
                            PlayControlService.newResumePendingIntent(this, playId)))
                    pictureInPictureParamsBuilder.setActions(actions)
                    setPictureInPictureParams(pictureInPictureParamsBuilder.build())
                    notificationBuilder?.setSmallIcon(R.drawable.ic_pause)
                    NotificationUtil.reshow(notificationId, notificationBuilder?.build())
                }
                BiliPlayerViewPackageView.PlayingStatus.ENDED -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                else -> OtherUtils.doNothing() // TODO: 20-7-16
            }
            // TODO: 20-7-12
        }
        baseBind.bpvpv.setOnPlayerErrorListener { error ->
            error.printStackTrace()
            Snackbar.make(baseBind.clRoot, error.message!!, BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry) { baseBind.bpvpv.player.retry() }
                    .show()
        }
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
    }

    override fun initData() {
        if (aid == 0L) {
            aid = teleportIntent?.getLongExtra("aid", 0)!!
            page = teleportIntent?.getIntExtra("page", 1)!!
            handler?.sendEmptyMessage(WHAT_LOAD_BILIVIEW)
        }
        title = ""
        playId = System.currentTimeMillis()

        PlayControlService.putId(playId, object : PlayControlService.ICallback {
            override fun onPause() {
                handler?.sendEmptyMessage(WHAT_PAUSE)
            }

            override fun onResume() {
                handler?.sendEmptyMessage(WHAT_RESUME)
            }
        })

        notificationBuilder = NotificationCompat.Builder(this, NotificationUtil.CHANNEL_CHANNEL_ID_VIDEO_PLAY_BACKGROUND)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
                .setSmallIcon(R.drawable.ic_pause)
                .setShowWhen(false)
                .addAction(R.drawable.ic_pause, getString(R.string.pause), PlayControlService.newPausePendingIntent(this, playId))
                .addAction(R.drawable.ic_play_arrow, getString(R.string.resume), PlayControlService.newResumePendingIntent(this, playId))
    }

    override fun onStop() {
        super.onStop()
        if (!Settings.play.isPlayBackground) {
            isPlayingBeforeActivityPause = baseBind.bpvpv.isPlaying
            baseBind.bpvpv.pause()
        } else {
            NotificationUtil.show(notificationId, notificationBuilder?.build()!!)
            if (baseBind.bpvpv.isPlaying) {
                NotificationUtil.setNotificationCleanable(notificationId, false)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!Settings.play.isPlayBackground) {
            if (isPlayingBeforeActivityPause) {
                baseBind.bpvpv.resume()
            }
        } else {
            NotificationUtil.remove(notificationId)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFullscreen) {
            window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayControlService.removeId(playId)
        baseBind.bpvpv.release()
        NotificationUtil.remove(notificationId)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        allowFullscreenAnimation = isInPictureInPictureMode.not()
        if (isInPictureInPictureMode) {
            if (!isFullscreen) {
                baseBind.bpvpv.clickIbFullscreen()
            }
            baseBind.bpvpv.biliPlayerView.useController = false
        } else {
            baseBind.bpvpv.biliPlayerView.useController = true
        }
    }

    override fun onBackPressed() {
        if (isFullscreen) {
            baseBind.bpvpv.clickIbFullscreen()
        } else {
            super.onBackPressed()
        }
    }

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_LOAD_BILIVIEW ->
                Thread {
                    try {
                        biliView = PBilibiliClient.getInstance().getPAppAPI().view(aid)
                        handler?.sendEmptyMessage(WHAT_BILIVIEW_LOAD_OVER)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread { TipUtil.showTip(this, e.message) }
                    }
                }.start()
            WHAT_BILIVIEW_LOAD_OVER -> {
                baseBind.bpvpv.setCover(biliView?.data?.pic)
                baseBind.vp.adapter = MyFragmentPagerAdapter(supportFragmentManager, 1)
                title = biliView?.data?.title
                notificationBuilder?.setContentTitle(biliView?.data?.title)
                Thread {
                    notificationBuilder?.setLargeIcon(Glide.with(this).asBitmap().load(biliView?.data?.pic).submit().get())
                }.start()
            }
            WHAT_INTRO_FRAGMENT_SEND_BACK -> {
                videoPlayUrl = GsonUtil.getGsonInstance().fromJson(msg.data.getString("videoPlayUrl"), VideoPlayUrl::class.java)
                page = msg.data.getInt("page")
                cid = biliView?.data?.pages?.get(page - 1)?.cid?.toLong()!!
                baseBind.bpvpv.loadDanmaku(aid, cid)
                baseBind.bpvpv.videoUrlAdapter = object : BiliPlayerViewPackageView.VideoUrlAdapter {
                    override fun getUrl(id: Int): Pair<String, String> {
                        var videoUrl: String? = null
                        val audioUrl: String? = videoPlayUrl?.data?.dash?.audio?.get(0)?.baseUrl
                        for (video in videoPlayUrl?.data?.dash?.video!!) {
                            if (video.id == id) {
                                videoUrl = video.baseUrl
                            }
                        }
                        return Pair(videoUrl, audioUrl)
                    }

                    override fun getDefaultIndex(): Int {
                        return if (Settings.play.defaultQualityType == 1) {
                            videoPlayUrl?.data?.acceptQuality?.indexOf(videoPlayUrl?.data?.quality)!!
                        } else {
                            videoPlayUrl?.data?.acceptQuality?.indexOf(videoPlayUrl?.data?.dash?.video?.get(0)?.id)!!
                        }
                    }

                    override fun getName(index: Int) = videoPlayUrl?.data?.acceptDescription?.get(index)!!

                    override fun onVideoIsNull() {
                        TipUtil.showTip(this@PlayActivity, R.string.not_vip)
                    }

                    override fun getCount() = videoPlayUrl?.data?.acceptQuality?.size!!

                    override fun getId(index: Int) = videoPlayUrl?.data?.acceptQuality?.get(index)!!
                }
                val rational = biliView?.data?.pages?.get(page - 1)?.dimension?.let {
                    Rational(it.width, it.height)
                }
                if (rational?.toDouble()!! > 0.418410 && rational.toDouble() < 2.390000) {
                    pictureInPictureParamsBuilder = PictureInPictureParams.Builder()
                            .setAspectRatio(rational)
                }
                biliView?.data?.pages?.let {
                    if (it.size > page) {
                        baseBind.bpvpv.biliPlayerView.setOnIbNextClickListener {
                            val message = Message()
                            message.what = IntroFragment.WHAT_LOAD_NEW_PAGE
                            message.arg1 = page + 1
                            introFragment?.handler?.sendMessage(message)
                        }
                    } else {
                        baseBind.bpvpv.biliPlayerView.setOnIbNextClickListener(null)
                    }
                }
                notificationBuilder?.setContentText(biliView?.data?.pages?.get(page - 1)?.part)
            }
            WHAT_ADD_HISTORY -> {
                val playedTime = baseBind.bpvpv.player.currentPosition
                Thread {
                    HistoryAPI.getInstance().setAidHistory(aid, biliView!!.data.cid.toLong(), playedTime / 1000, object : MyBilibiliClient.ICallback<Base?> {
                        override fun onException(e: java.lang.Exception) {
                            e.printStackTrace()
                            runOnUiThread { TipUtil.showToast(e.message) }
                        }

                        override fun onSuccess(t: Base?) {

                        }
                    })
                    Thread.sleep(15000)
                    if (Settings.play.isAutoRecordingHistory) {
                        handler?.sendEmptyMessage(WHAT_ADD_HISTORY)
                    }
                }.start()
            }
            WHAT_PAUSE -> baseBind.bpvpv.pause()
            WHAT_RESUME -> baseBind.bpvpv.resume()
        }
    }

    inner class MyFragmentPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
        override fun getItem(position: Int): Fragment {
            return if (position == 0) {
                if (introFragment == null) {
                    introFragment = IntroFragment.getInstance(biliView, aid, page)
                }
                introFragment!!
            } else {
                if (commitFragment == null) {
                    commitFragment = CommitFragment(aid)
                }
                commitFragment!!
            }
        }

        override fun getCount() = 2

        @Nullable
        override fun getPageTitle(position: Int): CharSequence {
            return if (position == 0) {
                getString(R.string.intro)
            } else {
                getString(R.string.comment_num, biliView?.data?.stat?.reply)
            }
        }
    }
}
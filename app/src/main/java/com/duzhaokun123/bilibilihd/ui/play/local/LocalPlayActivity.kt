package com.duzhaokun123.bilibilihd.ui.play.local

import android.graphics.Rect
import android.net.Uri
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.databinding.PlayExtLocalBinding
import com.duzhaokun123.bilibilihd.ui.play.base.BasePlayActivity
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil
import com.duzhaokun123.bilibilihd.utils.Settings
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser.XmlBiliDanmakuParser
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class LocalPlayActivity : BasePlayActivity<PlayExtLocalBinding>() {
    private lateinit var officialAppDownloadUri: Uri

    private val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(Application.getInstance(),
            Util.getUserAgent(Application.getInstance(), Application.getInstance().getString(R.string.app_name)))
    private val model: LocalVideoModel by viewModels()

    override fun initConfig() = 0

    override fun initExtLayout() = R.layout.play_ext_local

    override fun initView() {
        super.initView()
        baseBind.bpvwv.biliPlayerView.setOnIbNextClickListener(null)
        officialAppDownloadUri = Uri.parse(Settings.download.officialAppDownloadDir)
        extBind.rv.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect[0, 0, 0] = resources.getDimensionPixelOffset(R.dimen.divider_height)
            }
        })
        extBind.rv.layoutManager = LinearLayoutManager(this)
        extBind.rv.adapter = LocalAdapter(this, officialAppDownloadUri, model)
    }

    override fun initData() {
        model.title.observe(this, {
            this.title = "${it.first} ${it.second}"
            extBind.tvTitle.text = it.first
            extBind.tvPath.text = it.second
        })
        model.videoAudioUri.observe(this, {
            val sources = ArrayList<MediaSource>()
            if (it.first != null) {
                sources.add(ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(it.first))
            }
            if (it.second != null) {
                sources.add(ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(it.second))
            }
            baseBind.bpvwv.player.playWhenReady = false
            setVideoMediaSource(MergingMediaSource(*sources.toTypedArray()))
        })
        model.widthHeight.observe(this, {
            setWidthHeight(it.first, it.second)
        })
        model.danmakuUri.observe(this, {
            Thread {
                loadDanmakuByBiliDanmakuParser(XmlBiliDanmakuParser(it))
            }.start()
        })
    }

    class LocalVideoModel : ViewModel() {
        val title: MutableLiveData<Pair<String, String?>> by lazy {
            MutableLiveData()
        }
        val videoAudioUri: MutableLiveData<Pair<Uri?, Uri?>> by lazy {
            MutableLiveData()
        }
        val danmakuUri: MutableLiveData<Uri?> by lazy {
            MutableLiveData()
        }
        val cover: MutableLiveData<String> by lazy {
            MutableLiveData()
        }
        val aid: MutableLiveData<Long> by lazy {
            MutableLiveData()
        }
        val widthHeight: MutableLiveData<Pair<Int, Int>> by lazy {
            MutableLiveData()
        }
    }

    override fun onGetShareUrl() = MyBilibiliClientUtil.getB23Url(model.aid.value ?: 0) ?: ""

    override fun onGetShareTitle() = model.title.value.let { "${it?.first}" }

    override fun onCheckCover() {
        model.cover.value?.let { ImageViewUtil.viewImage(this, it) }
    }

    override fun onDownload() {
        //ignore
    }

    override fun onStartAddToHistory() {
        //ignore
    }
}
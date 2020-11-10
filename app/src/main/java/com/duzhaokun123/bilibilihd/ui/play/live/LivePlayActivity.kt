package com.duzhaokun123.bilibilihd.ui.play.live

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.databinding.PlayExtLiveBinding
import com.duzhaokun123.bilibilihd.ui.play.base.BasePlayActivity
import com.duzhaokun123.bilibilihd.ui.widget.BiliPlayerViewWrapperView
import com.duzhaokun123.bilibilihd.utils.BrowserUtil
import com.duzhaokun123.bilibilihd.utils.Settings
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.duzhaokun123.bilibilihd.utils.pBilibiliClient
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.hiczp.bilibili.api.weblive.model.PlayUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.toLongOrDefault

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

    override fun initConfig() = 0

    override fun initExtLayout() = R.layout.play_ext_live

    override fun initView() {
        super.initView()
        baseBind.bpvwv.biliPlayerView.setOnIbNextClickListener(null)
        extBind.btnPlay.setOnClickListener {
            loadLiveVideo(extBind.etCid.text.toString().toLongOrDefault(0))
        }
        baseBind.bpvwv.biliPlayerView.findViewById<DefaultTimeBar>(R.id.exo_progress).visibility = View.GONE
    }

    override fun initData() {
        extBind.etCid.setText(startIntent.getLongExtra(EXTRA_CID, 0).toString())
    }

    override fun onGetShareUrl() = "https://live.bilibili.com/${extBind.etCid.text}"

    override fun onGetShareTitle() = extBind.etCid.text.toString()

    override fun onCheckCover() {
//        TODO("Not yet implemented")
    }

    override fun onDownload() {
//        TODO("Not yet implemented")
    }

    override fun onStartAddToHistory() {
//        TODO("Not yet implemented")
    }

    private fun loadLiveVideo(cid: Long) {
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

                        override fun getMediaSource(id: Int): MediaSource? {
                            var mediaSource: MediaSource? = null
                            urls.forEach { url ->
                                if (url.data.currentQn == id) {
                                    mediaSource = HlsMediaSource.Factory(dataSourceFactory)
                                            .createMediaSource(Uri.parse(url.data.durl[0].url))
                                }
                            }
                            return mediaSource
                        }

                        override fun getCount(): Int {
                            return if (urls.size > 0)
                                urls[0].data.qualityDescription.size
                            else
                                0
                        }

                        override fun getName(index: Int) = urls[0].data.qualityDescription[index].desc

                        override fun getDefaultIndex() = 0

                        override fun getId(index: Int) = urls[0].data.qualityDescription[index].qn
                    })
                }
            }
        }
    }
}
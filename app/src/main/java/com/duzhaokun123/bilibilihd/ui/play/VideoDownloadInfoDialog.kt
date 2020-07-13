package com.duzhaokun123.bilibilihd.ui.play

import android.content.Context
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseDialog
import com.duzhaokun123.bilibilihd.databinding.DialogVideoDownloadInfoBinding
import com.hiczp.bilibili.api.player.model.VideoPlayUrl
import com.hiczp.bilibili.api.app.model.View as BiliView

class VideoDownloadInfoDialog(context: Context,
                              private val biliView: BiliView,
                              private val videoPlayUrl: VideoPlayUrl,
                              private val page: Int,
                              private val qualityId: Int)
    : BaseDialog<DialogVideoDownloadInfoBinding>(context) {
    override fun initConfig() = 0

    override fun initLayout() = R.layout.dialog_video_download_info

    override fun initView() {
    }

    override fun initData() {
        baseBind.tvPageNumber.text = page.toString()
        biliView.data.let { data ->
            baseBind.tvAid.text = data.aid.toString()
            baseBind.tvBvid.text = data.bvid
            baseBind.tvVideoTitle.text = data.title
            data.pages[page - 1].let { page ->
                baseBind.tvCid.text = page.cid.toString()
                baseBind.tvPageTitle.text = page.part
                baseBind.tvDanmakuUrl.text = page.dmlink
            }
        }
        videoPlayUrl.data.let { data ->
            baseBind.tvQuality.text = data.acceptDescription[data.acceptQuality.indexOf(qualityId)]
            data.dash.let { dash ->
                for (video in dash.video) {
                    if (video.id == qualityId) {
                        baseBind.tvVideoUrl.text = video.baseUrl
                        break
                    }
                }
                baseBind.tvAudioUrl.text = dash.audio?.get(0)?.baseUrl
            }
        }
    }
}
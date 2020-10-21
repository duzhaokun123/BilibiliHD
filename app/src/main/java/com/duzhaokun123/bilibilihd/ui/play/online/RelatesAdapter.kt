package com.duzhaokun123.bilibilihd.ui.play.online

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseSimpleAdapter
import com.duzhaokun123.bilibilihd.databinding.ItemRelateVideoCardBinding
import com.duzhaokun123.bilibilihd.utils.*
import com.hiczp.bilibili.api.app.model.View as BiliView

class RelatesAdapter(context: Context, private val biliView: BiliView) : BaseSimpleAdapter<ItemRelateVideoCardBinding>(context) {
    override fun getItemCount(): Int {
        return if (biliView.data.relates == null) {
            0
        } else {
            biliView.data.relates!!.size
        }
    }

    override fun initLayout() = R.layout.item_relate_video_card

    override fun initView(baseBind: ItemRelateVideoCardBinding, position: Int) {
        baseBind.cv.setOnClickListener {
            if (biliView.data.relates!![position].aid != 0) {
                val intent = Intent(context, OnlinePlayActivity::class.java)
                intent.putExtra("aid", biliView.data.relates!![position].aid.toLong())
                intent.putExtra(OnlinePlayActivity.EXTRA_FAST_LOAD_COVER_URL, biliView.data.relates!![position].pic)
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity!!, baseBind.ivCover, "cover").toBundle())
            } else {
                BrowserUtil.openCustomTab(context, biliView.data.relates!![position].uri)
            }
        }
        baseBind.cv.setOnLongClickListener {
            val popupMenu = PopupMenu(context, baseBind.cv)
            popupMenu.inflate(R.menu.video_card)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.check_cover -> {
                        ImageViewUtil.viewImage(context, biliView.data.relates!![position].pic, baseBind.ivCover)
                    }
                    R.id.add_to_watch_later ->
                        Thread {
                            try {
                                pBilibiliClient.pMainAPI.toView(aid = biliView.data.relates!![position].aid.toLong())
                                runOnUiThread { TipUtil.showTip(context, R.string.added) }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                runOnUiThread { TipUtil.showTip(context, e.message) }
                            }
                        }.start()
                }
                true
            }
            popupMenu.show()
            true
        }
    }

    override fun initData(baseBind: ItemRelateVideoCardBinding, position: Int) {
        biliView.data.relates!![position].let {
            baseBind.tvTitle.text = it.title
            baseBind.tvPlay.text = it.stat.view.toString()
            baseBind.tvDanmaku.text = it.stat.danmaku.toString()
            baseBind.tvUp.text = it.owner?.name
            baseBind.tvDuration.text = DateTimeFormatUtil.getStringForTime(it.duration.toLong() * 1000)
            GlideUtil.loadUrlInto(context, it.pic, baseBind.ivCover, false)
        }
    }
}
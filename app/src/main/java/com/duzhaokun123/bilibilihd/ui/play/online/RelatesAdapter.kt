package com.duzhaokun123.bilibilihd.ui.play.online

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import bilibili.app.view.v1.ViewV1
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseSimpleAdapter
import com.duzhaokun123.bilibilihd.databinding.ItemRelateVideoCardBinding
import com.duzhaokun123.bilibilihd.utils.*

class RelatesAdapter(context: Context, private val biliView: ViewV1.ViewReply) : BaseSimpleAdapter<ItemRelateVideoCardBinding>(context) {
    override fun getItemCount(): Int {
        return if (biliView.relatesList == null) {
            0
        } else {
            biliView.relatesList.size
        }
    }

    override fun initLayout() = R.layout.item_relate_video_card

    override fun initView(baseBind: ItemRelateVideoCardBinding, position: Int) {
        baseBind.cv.setOnClickListener {
            if (biliView.relatesList[position].aid != 0L) {
                val intent = Intent(context, OnlinePlayActivity::class.java)
                intent.putExtra("aid", biliView.relatesList[position].aid.toLong())
                intent.putExtra(OnlinePlayActivity.EXTRA_FAST_LOAD_COVER_URL, biliView.relatesList[position].pic)
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity!!, baseBind.ivCover, "cover").toBundle())
            } else {
                BrowserUtil.openCustomTab(context, biliView.relatesList[position].uri)
            }
        }
        baseBind.cv.setOnLongClickListener {
            val popupMenu = PopupMenu(context, baseBind.cv)
            popupMenu.inflate(R.menu.video_card)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.check_cover -> {
                        ImageViewUtil.viewImage(context, biliView.relatesList[position].pic, baseBind.ivCover)
                    }
                    R.id.add_to_watch_later ->
                        Thread {
                            try {
                                pBilibiliClient.pMainAPI.toView(aid = biliView.relatesList[position].aid.toLong())
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
        biliView.relatesList[position].let {
            baseBind.relate = it
            GlideUtil.loadUrlInto(context, it.pic, baseBind.ivCover, false)
        }
    }
}
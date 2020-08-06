package com.duzhaokun123.bilibilihd.ui.play

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseSimpleAdapter
import com.duzhaokun123.bilibilihd.databinding.ItemRelateVideoCardBinding
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient
import com.duzhaokun123.bilibilihd.mybilibiliapi.toview.ToViewAPI
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity
import com.duzhaokun123.bilibilihd.utils.BrowserUtil
import com.duzhaokun123.bilibilihd.utils.GlideUtil
import com.hiczp.bilibili.api.retrofit.CommonResponse
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
                val intent = Intent(context, PlayActivity::class.java)
                intent.putExtra("aid", biliView.data.relates!![position].aid.toLong())
                intent.putExtra(PlayActivity.EXTRA_FAST_LOAD_COVER_URL, biliView.data.relates!![position].pic)
                ContextCompat.startActivity(context, intent, ActivityOptions.makeSceneTransitionAnimation(activity!!, baseBind.ivCover, "cover").toBundle())
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
                        val intent = Intent(context, PhotoViewActivity::class.java)
                        intent.putExtra("url", biliView.data.relates!![position].pic)
                        ContextCompat.startActivity(context, intent, ActivityOptions.makeSceneTransitionAnimation(activity!!, baseBind.ivCover, "img").toBundle())
                    }
                    R.id.add_to_watch_later -> object : Thread() {
                        override fun run() {
                            ToViewAPI.getInstance().addAid(biliView.data.relates!![position].aid.toLong(), object : MyBilibiliClient.ICallback<CommonResponse> {
                                override fun onException(e: Exception) {
                                    e.printStackTrace()
                                }
                            })
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
            baseBind.tvPlay.text = it.stat.reply.toString()
            baseBind.tvDanmaku.text = it.stat.danmaku.toString()
            baseBind.tvUp.text = it.owner?.name
            GlideUtil.loadUrlInto(context, it.pic, baseBind.ivCover, false)
        }
    }
}
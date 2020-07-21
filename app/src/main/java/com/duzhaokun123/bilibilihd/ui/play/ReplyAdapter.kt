package com.duzhaokun123.bilibilihd.ui.play

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseSimpleAdapter
import com.duzhaokun123.bilibilihd.databinding.ItemReplyBinding
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity
import com.duzhaokun123.bilibilihd.utils.GlideUtil
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil
import com.duzhaokun123.bilibilihd.utils.DateTimeFormatUtil
import com.hiczp.bilibili.api.main.model.Reply

class ReplyAdapter(context: Context, private val reply: Reply) : BaseSimpleAdapter<ItemReplyBinding>(context) {
    private val sampleDateFormat = DateTimeFormatUtil.getFormat1()

    override fun getItemCount(): Int {
        return if (reply.data.replies == null) {
            0
        } else {
            reply.data.replies!!.size
        }
    }

    override fun initLayout() = R.layout.item_reply

    override fun initView(baseBind: ItemReplyBinding, position: Int) {
        reply.data.replies?.get(position)?.let { reply ->
            baseBind.civFace.setOnClickListener {
                val intent = Intent(context, UserSpaceActivity::class.java)
                intent.putExtra("uid", reply.mid)
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }

    override fun initData(baseBind: ItemReplyBinding, position: Int) {
        reply.data.replies?.get(position)?.let {
            baseBind.tvUsername.text = it.member.uname
            baseBind.tvDate.text = sampleDateFormat.format(it.ctime.toLong() * 1000)
            baseBind.tvContent.text = it.content.message
            baseBind.tvFloor.text = context.getString(R.string.d_floor, it.floor)
            GlideUtil.loadUrlInto(context, it.member.avatar, baseBind.civFace, false)
            ImageViewUtil.setLevelDrawable(baseBind.ivLevel, it.member.levelInfo.currentLevel)
        }
    }
}
package com.duzhaokun123.bilibilihd.ui.universal.reply

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseSimpleAdapter
import com.duzhaokun123.bilibilihd.databinding.ItemReplyBinding
import com.duzhaokun123.bilibilihd.mybilibiliapi.reply.ReplyAPI
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity
import com.duzhaokun123.bilibilihd.utils.*
import com.github.salomonbrys.kotson.get
import com.hiczp.bilibili.api.main.model.Reply

class RootReplyAdapter(context: Context, private val reply: Reply) : BaseSimpleAdapter<ItemReplyBinding>(context) {
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
        reply.data.replies?.get(position)?.let { reply ->
            baseBind.tvUsername.text = reply.member.uname
            baseBind.tvDate.text = sampleDateFormat.format(reply.ctime.toLong() * 1000)
            baseBind.tvFloor.text = context.getString(R.string.hashtagd, reply.floor)
            baseBind.tvLike.text = reply.like.toString()
            baseBind.tvReply.text = reply.rcount.toString()
            GlideUtil.loadUrlInto(context, reply.member.avatar, baseBind.civFace, false)
            ImageViewUtil.setLevelDrawable(baseBind.ivLevel, reply.member.levelInfo.currentLevel)
            when (reply.action) {
                1 -> baseBind.tvAction.setText(R.string.liked)
                2 -> baseBind.tvAction.setText(R.string.disliked)
            }

            baseBind.tvContent.text = reply.content.message
            Thread {
                val messageSSB = SpannableStringBuilder(reply.content.message)
                reply.content.emote?.let { emotes ->
                    for (key in emotes.keySet()) {
                        val emote = emotes.get(key)
                        val emoteText = emote["text"].asString
                        val emoteSize = OtherUtils.dp2px(20f) * emote["meta"]["size"].asInt
                        val emoteBitmap = Glide.with(context).asBitmap().load(emote["url"].asString).submit(emoteSize, emoteSize).get()
                        var index = -1
                        while (messageSSB.indexOf(emoteText, index + 1).also { index = it } != -1) {
                            messageSSB.setSpan(ImageSpan(context, emoteBitmap), index, index + emoteText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
                activity?.runOnUiThread {
                    baseBind.tvContent.text = messageSSB
                    LinkifyUtil.addAllLinks(baseBind.tvContent)
                }
            }.start()

            baseBind.btnAction.setOnClickListener {
                val popupMenu = PopupMenu(context, baseBind.btnAction)
                popupMenu.menuInflater.inflate(R.menu.reply_action, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.like -> {
                            Thread {
                                try {
                                    PBilibiliClient.getInstance().pMainAPI.likeReply(1, reply.oid, reply.rpid, reply.type)
                                    activity?.runOnUiThread {
                                        TipUtil.showTip(context, R.string.liked)
                                        baseBind.tvAction.setText(R.string.liked)
                                    }
                                    reply.action = 1
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                                }
                            }.start()
                        }
                        R.id.dislike -> {
                            Thread {
                                try {
                                    PBilibiliClient.getInstance().pMainAPI.dislikeReply(1, reply.oid, reply.rpid, reply.type)
                                    activity?.runOnUiThread {
                                        TipUtil.showTip(context, R.string.disliked)
                                        baseBind.tvAction.setText(R.string.disliked)
                                    }
                                    reply.action = 2
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                                }
                            }.start()
                        }
                        R.id.cancel_action -> {
                            Thread {
                                when (reply.action) {
                                    0 -> activity?.runOnUiThread { TipUtil.showTip(context, R.string.no_action) }
                                    1, 2 -> {
                                        try {
                                            if (reply.action == 1) {
                                                PBilibiliClient.getInstance().pMainAPI.likeReply(0, reply.oid, reply.rpid, reply.type)
                                            } else {
                                                PBilibiliClient.getInstance().pMainAPI.dislikeReply(0, reply.oid, reply.rpid, reply.type)
                                            }
                                            reply.action = 0
                                            activity?.runOnUiThread {
                                                TipUtil.showTip(context, R.string.canceled)
                                                baseBind.tvAction.text = null
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                                        }
                                    }
                                }
                            }.start()
                        }
                        R.id.reply -> {
                            val intent = Intent(context, ChildReplyActivity::class.java)
                            intent.putExtra(ChildReplyActivity.EXTRA_TYPE, reply.type)
                            intent.putExtra(ChildReplyActivity.EXTRA_OID, reply.oid)
                            intent.putExtra(ChildReplyActivity.EXTRA_ROOT, reply.rpid)
                            activity?.startActivity(intent)
                        }
                        R.id.delete -> {
                            Thread {
                                try {
                                    ReplyAPI.del(reply.type, reply.oid, reply.rpid)
                                    activity?.runOnUiThread {
                                        TipUtil.showTip(context, R.string.deleted)
                                        baseBind.tvAction.setText(R.string.deleted)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                                }
                            }.start()
                        }
                        R.id.report -> BrowserUtil.openWebViewActivity(context,
                                "https://www.bilibili.com/h5/comment/report?&pageType=${reply.type}&oid=${reply.oid}&rpid=${reply.rpid}", false, false)
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }
}
package com.duzhaokun123.bilibilihd.ui.universal.reply

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.widget.PopupMenu
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
    private var hasUpperTop = false

    override fun getItemCount(): Int {
        hasUpperTop = reply.data.top?.upper != null
        return if (reply.data.replies == null) {
            0
        } else {
            reply.data.replies!!.size
        } + if (hasUpperTop) 1 else 0
    }

    override fun initLayout() = R.layout.item_reply

    override fun initView(baseBind: ItemReplyBinding, position: Int) {
        baseBind.tvTop.visibility = View.INVISIBLE
        val aReply: Reply.Data.Reply = if (hasUpperTop && position == 0) {
            baseBind.tvTop.visibility = View.VISIBLE
            reply.data.top!!.upper!!.toCommonReply()
        } else if (hasUpperTop) {
            reply.data.replies?.get(position - 1)
        } else {
            reply.data.replies?.get(position)
        } ?: return

        baseBind.civFace.setOnClickListener {
            UserSpaceActivity.enter(activity, aReply.mid, baseBind.civFace, null)
        }
    }

    override fun initData(baseBind: ItemReplyBinding, position: Int) {
        val aReply: Reply.Data.Reply = if (hasUpperTop && position == 0) {
            reply.data.top!!.upper!!.toCommonReply()
        } else if (hasUpperTop) {
            reply.data.replies?.get(position - 1)
        } else {
            reply.data.replies?.get(position)
        } ?: return

        baseBind.tvUsername.text = aReply.member.uname
        baseBind.tvDate.text = sampleDateFormat.format(aReply.ctime.toLong() * 1000)
        baseBind.tvFloor.text = context.getString(R.string.hashtagd, aReply.floor)
        baseBind.tvLike.text = aReply.like.toString()
        baseBind.tvReply.text = aReply.rcount.toString()
        GlideUtil.loadUrlInto(context, aReply.member.avatar, baseBind.civFace, false)
        ImageViewUtil.setLevelDrawable(baseBind.ivLevel, aReply.member.levelInfo.currentLevel)
        when (aReply.action) {
            1 -> baseBind.tvAction.setText(R.string.liked)
            2 -> baseBind.tvAction.setText(R.string.disliked)
        }

        baseBind.tvContent.text = aReply.content.message
        Thread {
            val messageSSB = SpannableStringBuilder(aReply.content.message)
            aReply.content.emote?.let { emotes ->
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
                                PBilibiliClient.getInstance().pMainAPI.likeReply(1, aReply.oid, aReply.rpid, aReply.type)
                                activity?.runOnUiThread {
                                    TipUtil.showTip(context, R.string.liked)
                                    baseBind.tvAction.setText(R.string.liked)
                                }
                                aReply.action = 1
                            } catch (e: Exception) {
                                e.printStackTrace()
                                activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                            }
                        }.start()
                    }
                    R.id.dislike -> {
                        Thread {
                            try {
                                PBilibiliClient.getInstance().pMainAPI.dislikeReply(1, aReply.oid, aReply.rpid, aReply.type)
                                activity?.runOnUiThread {
                                    TipUtil.showTip(context, R.string.disliked)
                                    baseBind.tvAction.setText(R.string.disliked)
                                }
                                aReply.action = 2
                            } catch (e: Exception) {
                                e.printStackTrace()
                                activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                            }
                        }.start()
                    }
                    R.id.cancel_action -> {
                        Thread {
                            when (aReply.action) {
                                0 -> activity?.runOnUiThread { TipUtil.showTip(context, R.string.no_action) }
                                1, 2 -> {
                                    try {
                                        if (aReply.action == 1) {
                                            PBilibiliClient.getInstance().pMainAPI.likeReply(0, aReply.oid, aReply.rpid, aReply.type)
                                        } else {
                                            PBilibiliClient.getInstance().pMainAPI.dislikeReply(0, aReply.oid, aReply.rpid, aReply.type)
                                        }
                                        aReply.action = 0
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
                        intent.putExtra(ChildReplyActivity.EXTRA_TYPE, aReply.type)
                        intent.putExtra(ChildReplyActivity.EXTRA_OID, aReply.oid)
                        intent.putExtra(ChildReplyActivity.EXTRA_ROOT, aReply.rpid)
                        activity?.startActivity(intent)
                    }
                    R.id.delete -> {
                        Thread {
                            try {
                                ReplyAPI.del(aReply.type, aReply.oid, aReply.rpid)
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
                            "https://www.bilibili.com/h5/comment/report?&pageType=${aReply.type}&oid=${aReply.oid}&rpid=${aReply.rpid}", false, false)
                }
                true
            }
            popupMenu.show()
        }

    }
}
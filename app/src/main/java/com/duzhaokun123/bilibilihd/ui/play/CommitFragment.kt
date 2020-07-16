package com.duzhaokun123.bilibilihd.ui.play

import android.graphics.Rect
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseFragment
import com.duzhaokun123.bilibilihd.databinding.FragmentCommitBinding
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil
import com.hiczp.bilibili.api.main.model.Reply
import com.jcodecraeer.xrecyclerview.XRecyclerView

class CommitFragment(val aid: Long) : BaseFragment<FragmentCommitBinding>() {
    companion object {
        const val WHAT_REPLY_REFRESH = 0
        const val WHAT_REPLY_REFRESH_END = 1
        const val WHAT_REPLY_LOAD_MORE = 2
        const val WHAT_REPLY_LOAD_MORE_END = 3
    }

    private var reply: Reply? = null
    private var next: Long? = null

    override fun initConfig() = NEED_HANDLER

    override fun initLayout() = R.layout.fragment_commit

    override fun initView() {
        baseBind.xrv.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect[0, 0, 0] = resources.getDimensionPixelOffset(R.dimen.divider_height)
            }
        })
        baseBind.xrv.layoutManager = LinearLayoutManager(context)
        baseBind.xrv.adapter = Adapter()
        baseBind.xrv.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onLoadMore() {
                handler?.sendEmptyMessage(WHAT_REPLY_LOAD_MORE)
            }

            override fun onRefresh() {
                handler?.sendEmptyMessage(WHAT_REPLY_REFRESH)
            }
        })
    }

    override fun initData() {
        baseBind.xrv.refresh()
    }

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_REPLY_REFRESH -> Thread {
                var reply: Reply? = null
                try {
                    reply = PBilibiliClient.getInstance().getPMainAPI().reply(aid, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                }
                if (reply != null) {
                    next = reply.data.cursor.next
                    this.reply = reply
                    handler?.sendEmptyMessage(WHAT_REPLY_REFRESH_END)
                }
            }.start()
            WHAT_REPLY_REFRESH_END -> {
                baseBind.xrv.refreshComplete()
                reply!!.data.replies?.size?.let { XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, it - 1) }
            }
            WHAT_REPLY_LOAD_MORE -> Thread {
                var reply: Reply? = null
                try {
                    reply = PBilibiliClient.getInstance().getPMainAPI().reply(aid, next)
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                }
                if (reply != null) {
                    next = reply!!.data.cursor.next
                    reply!!.data.replies?.let { this.reply?.data?.replies?.plus(it) }
                    handler?.sendEmptyMessage(WHAT_REPLY_LOAD_MORE_END)
                }
            }.start()
            WHAT_REPLY_LOAD_MORE_END -> {
                baseBind.xrv.loadMoreComplete()
                XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, reply!!.data.replies!!.size - 1)
            }
        }
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.ReplyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ReplyHolder(LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false))

        override fun getItemCount(): Int {
            return if (reply == null || reply!!.data.replies == null) {
                0
            } else {
                reply!!.data.replies!!.size
            }
        }

        override fun onBindViewHolder(holder: ReplyHolder, position: Int) {
            reply?.data?.replies?.get(position)?.let {
                holder.tvUsername.text = it.member.uname
                holder.tvContext.text = it.content.message
            }
        }

        inner class ReplyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
            val tvContext: TextView = itemView.findViewById(R.id.tv_content)
        }
    }
}
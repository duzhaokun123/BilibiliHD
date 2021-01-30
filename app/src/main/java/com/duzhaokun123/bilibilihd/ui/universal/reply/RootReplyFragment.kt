package com.duzhaokun123.bilibilihd.ui.universal.reply

import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Message
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseFragment
import com.duzhaokun123.bilibilihd.databinding.FragmentRootReplyBinding
import com.duzhaokun123.bilibilihd.utils.ListUtil
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.hiczp.bilibili.api.main.model.Reply
import com.hiczp.bilibili.api.main.model.SendReplyResponse
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class RootReplyFragment(private val oid: Long, private val defMode: Int, private val type: Int) : BaseFragment<FragmentRootReplyBinding>() {
    companion object {
        const val WHAT_REPLY_REFRESH = 0
        const val WHAT_REPLY_REFRESH_END = 1
        const val WHAT_REPLY_LOAD_MORE = 2
        const val WHAT_REPLY_LOAD_MORE_END = 3
    }

    private var reply: Reply? = null
    private var next: Long? = null
    private var isEnd = false

    private var defLlMoreHeight = 0
    private var isLlMoreOpen = true

    private val viewModel: AllCountViewModel by activityViewModels()

    private lateinit var srl: SmartRefreshLayout
    private lateinit var rv: RecyclerView

    override fun initConfig() = NEED_HANDLER

    override fun initLayout() = R.layout.fragment_root_reply

    override fun findViews(parentView: View) {
        srl = parentView.findViewById(R.id.srl)
        rv = parentView.findViewById(R.id.rv)
    }

    override fun initView() {
        rv.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect[0, 0, 0] = resources.getDimensionPixelOffset(R.dimen.divider_height)
            }
        })
        rv.layoutManager = LinearLayoutManager(context)
        srl.setOnRefreshListener {
            isEnd = false
            handler?.sendEmptyMessage(WHAT_REPLY_REFRESH)
        }
        srl.setOnLoadMoreListener {
            if (isEnd.not()) {
                handler?.sendEmptyMessage(WHAT_REPLY_LOAD_MORE)
            } else {
                srl.finishLoadMoreWithNoMoreData()
            }
        }
        baseBind.btnSend.setOnClickListener {
            Thread {
                var sendReplyResponse: SendReplyResponse? = null
                try {
                    sendReplyResponse = Application.getPBilibiliClient().pMainAPI.sendReply(oid, baseBind.etText.text.toString(), type = type)
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity?.runOnUiThread { TipUtil.showToast(e.message) }
                }
                if (sendReplyResponse != null) {
                    activity?.runOnUiThread {
                        srl.autoRefresh()
                        baseBind.etText.text = null
                    }
                }
            }.start()
        }
        baseBind.etMode.setText(defMode.toString())
        baseBind.ibLlMoreSwitch.setOnClickListener {
            if (defLlMoreHeight == 0) {
                defLlMoreHeight = baseBind.llMore.height
            }
            val params = baseBind.llMore.layoutParams
            val valueAnimator: ValueAnimator
            if (isLlMoreOpen) {
                isLlMoreOpen = false
                valueAnimator = ValueAnimator.ofInt(defLlMoreHeight, 0)
                baseBind.ibLlMoreSwitch.setImageResource(R.drawable.ic_add_circle)
            } else {
                isLlMoreOpen = true
                valueAnimator = ValueAnimator.ofInt(0, defLlMoreHeight)
                valueAnimator.doOnEnd {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    baseBind.llMore.layoutParams = params
                }
                baseBind.ibLlMoreSwitch.setImageResource(R.drawable.ic_add_circle_accent)
            }
            valueAnimator.addUpdateListener {
                params.height = it.animatedValue as Int
                baseBind.llMore.layoutParams = params
            }
            valueAnimator.start()
        }
    }

    override fun initData() {
        srl.autoRefresh()
    }

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_REPLY_REFRESH -> Thread {
                var reply: Reply? = null
                try {
                    reply = Application.getPBilibiliClient().pMainAPI.reply(oid, null, baseBind.etMode.text.toString().toInt(), type)
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                }
                if (reply != null) {
                    next = reply.data.cursor.next
                    viewModel.allCount.postValue(reply.data.cursor.allCount)
                    this.reply = reply
                    isEnd = reply.data.cursor.isEnd
                    if (reply.data.top == null && reply.data.replies == null) {
                        isEnd = true
                    }
                }
                handler?.sendEmptyMessage(WHAT_REPLY_REFRESH_END)
            }.start()
            WHAT_REPLY_REFRESH_END -> {
               rv.adapter = RootReplyAdapter(requireContext(), reply!!)
                srl.finishRefresh()
                reply?.data?.replies?.size?.let { rv.adapter!!.notifyItemRangeChanged(0, it)}
            }
            WHAT_REPLY_LOAD_MORE -> Thread {
                var reply: Reply? = null
                try {
                    reply = Application.getPBilibiliClient().pMainAPI.reply(oid, next, baseBind.etMode.text.toString().toInt(), type)
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity?.runOnUiThread { TipUtil.showTip(context, e.message) }
                }
                if (reply != null) {
                    next = reply!!.data.cursor.next
                    viewModel.allCount.postValue(reply!!.data.cursor.allCount)
                    reply!!.data.replies?.let {
                        ListUtil.addAll(this.reply?.data?.replies, it)
                    }
                    isEnd = reply!!.data.cursor.isEnd
                    if (reply!!.data.top == null && reply!!.data.replies == null) {
                        isEnd = true
                    }
                }
                handler?.sendEmptyMessage(WHAT_REPLY_LOAD_MORE_END)
            }.start()
            WHAT_REPLY_LOAD_MORE_END -> {
                srl.finishLoadMore()
                rv.adapter!!.notifyItemRangeChanged(0, reply?.data?.replies?.size?.minus(1) ?: 0)
            }
        }
    }

    class AllCountViewModel : ViewModel() {
        val allCount: MutableLiveData<Int> by lazy {
            MutableLiveData<Int>()
        }
    }
}
package com.duzhaokun123.bilibilihd.ui.userspace

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Message
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity
import com.duzhaokun123.bilibilihd.ui.play.online.OnlinePlayActivity
import com.duzhaokun123.bilibilihd.utils.*
import com.duzhaokun123.bilibilihd.utils.ApiUtil.resources
import com.hiczp.bilibili.api.main.model.ResourceIds
import com.hiczp.bilibili.api.main.model.ResourceInfos
import com.jcodecraeer.xrecyclerview.XRecyclerView.LoadingListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.properties.Delegates

class FavoriteActivity : BaseActivity<LayoutXrecyclerviewOnlyBinding>() {
    private var ids: ResourceIds? = null
    private var infos: ResourceInfos? = null

    private var mediaId by Delegates.notNull<Long>()
    private var mid by Delegates.notNull<Long>()

    override fun initConfig() = NEED_HANDLER or FIX_LAYOUT

    public override fun initLayout() = R.layout.layout_xrecyclerview_only

    public override fun initView() {
        title = startIntent.getStringExtra("name")
        var spanCount = resources.getInteger(R.integer.column_medium)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && Settings.layout.column != 0) {
            spanCount = Settings.layout.column
        } else if (Settings.layout.columnLand != 0) {
            spanCount = Settings.layout.columnLand
        }
        baseBind.xrv.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        if (spanCount == 1) {
            baseBind!!.xrv.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect[0, 0, 0] = resources.getDimensionPixelOffset(R.dimen.divider_height)
                }
            })
        } else {
            baseBind.xrv.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect[0, 0, resources.getDimensionPixelOffset(R.dimen.divider_height)] = resources.getDimensionPixelOffset(R.dimen.divider_height)
                }
            })
        }
        baseBind.xrv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return if (viewType == 0) {
                    VideoCardHolder(LayoutInflater.from(this@FavoriteActivity).inflate(R.layout.layout_video_card_item, parent, false))
                } else {
                    VHolder(View(this@FavoriteActivity))
                }
            }

            override fun getItemViewType(position: Int): Int {
                return if (position == ids!!.data.size) {
                    1
                } else {
                    0
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (getItemViewType(position) == 1) {
                    val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fixButtonHeight)
                    holder.itemView.layoutParams = params
                } else {
                    if (infos == null) {
                        (holder as VideoCardHolder).mTvTitle.text = ids!!.data[position].bvid
                    } else {
                        infos!!.data[position].let {data ->
                            (holder as VideoCardHolder).mTvTitle.text = data.title
                            holder.mTvUp.text = "${data.upper.name}\n${data.intro}"
                            GlideUtil.loadUrlInto(this@FavoriteActivity, data.cover, holder.mIv, true)
                            Glide.with(this@FavoriteActivity).load(data.upper.face).into(holder.mCivFace)

                            holder.mCv.setOnClickListener {
                                val intent = Intent(this@FavoriteActivity, OnlinePlayActivity::class.java).apply {
                                    putExtra(OnlinePlayActivity.EXTRA_AID, data.id)
                                    putExtra(OnlinePlayActivity.EXTRA_FAST_LOAD_COVER_URL, data.cover)
                                }
                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@FavoriteActivity, holder.mIv, "cover").toBundle())
                            }
                            holder.mCv.setOnLongClickListener {
                                val popupMenu = PopupMenu(this@FavoriteActivity, holder.mCv)
                                popupMenu.menuInflater.inflate(R.menu.video_card, popupMenu.menu)
                                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                                    when (item.itemId) {
                                        R.id.check_cover -> {
                                            val intent = Intent(this@FavoriteActivity, PhotoViewActivity::class.java).apply {
                                                putExtra("url", data.cover)
                                            }
                                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@FavoriteActivity, holder.mIv, "img").toBundle())
                                        }
                                        R.id.add_to_watch_later -> ApiUtil.addToView(this@FavoriteActivity, bvid = data.bvid)
                                    }
                                    true
                                }
                                popupMenu.show()
                                true
                            }
                            holder.mCivFace.setOnClickListener {
                                UserSpaceActivity.enter(this@FavoriteActivity, data.upper.mid, holder.mCivFace, null)
                            }
                        }
                    }
                }
            }

            override fun getItemCount(): Int {
                return if (ids == null) {
                    0
                } else {
                    ids!!.data.size + 1
                }
            }

            inner class VideoCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val mCv: CardView = itemView.findViewById(R.id.cv)
                val mIv: ImageView = itemView.findViewById(R.id.iv)
                val mTvTitle: TextView = itemView.findViewById(R.id.tv_title)
                val mTvUp: TextView = itemView.findViewById(R.id.tv_up)
                val mCivFace: CircleImageView = itemView.findViewById(R.id.civ_face)
            }

            inner class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
        }
        baseBind.xrv.setLoadingMoreEnabled(false)
        baseBind.xrv.setLoadingListener(object : LoadingListener {
            override fun onRefresh() {
                Refresh().start()
            }

            override fun onLoadMore() {}
        })
    }

    public override fun initData() {
        mediaId = startIntent.getLongExtra("media_id", 0)
        mid = startIntent.getLongExtra("mid", 0)

        baseBind.xrv.refresh()
    }

    override fun initRegisterCoordinatorLayout() = baseBind.clRoot

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            1 -> {
                baseBind.xrv.refreshComplete()
                XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, ids!!.data.size)
            }
            0 -> XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, ids!!.data.size)
        }
    }

    internal inner class Refresh : Thread() {
        override fun run() {
            try {
                ids = pBilibiliClient.pMainAPI.resourceIds(mediaId, mid)
                handler?.sendEmptyMessage(0)
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { TipUtil.showTip(this@FavoriteActivity, e.message) }
            }
            if (ids != null) {
                ids!!.resources.forEach {
                    try {
                        val i = pBilibiliClient.pMainAPI.resourceInfos(mediaId, it, mid)
                        if (infos == null) {
                            infos = i
                        } else {
                            ListUtil.addAll(infos!!.data, i.data)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread { TipUtil.showTip(this@FavoriteActivity, e.message) }
                    }
                    handler?.sendEmptyMessage(1)
                }
            }
        }
    }
}
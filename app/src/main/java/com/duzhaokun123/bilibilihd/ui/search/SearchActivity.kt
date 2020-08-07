package com.duzhaokun123.bilibilihd.ui.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.ActivitySearchBinding
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient
import com.duzhaokun123.bilibilihd.utils.ListUtil
import com.duzhaokun123.bilibilihd.utils.Settings
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil
import com.hiczp.bilibili.api.app.model.SearchResult
import com.jcodecraeer.xrecyclerview.XRecyclerView

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    companion object {
        const val WHAT_REFRESH = 0
        const val WHAT_REFRESH_END = 1
        const val WHAT_LOAD_MORE = 2
        const val WHAT_LOAD_MORE_END = 3
    }

    private var page = 1

    private var searchResult: SearchResult? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_activity, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                onSearchRequested()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    override fun initConfig() = NEED_HANDLER or FIX_LAYOUT


    override fun initLayout() = R.layout.activity_search

    override fun initView() {
        var spanCount = resources.getInteger(R.integer.column_medium)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && Settings.layout.column != 0) {
            spanCount = Settings.layout.column
        } else if (Settings.layout.columnLand != 0) {
            spanCount = Settings.layout.columnLand
        }
        baseBind.xrv.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        if (spanCount == 1) {
            baseBind.xrv.addItemDecoration(object : ItemDecoration() {
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
        baseBind.xrv.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onLoadMore() {
                handler?.sendEmptyMessage(WHAT_LOAD_MORE)
            }

            override fun onRefresh() {
                handler?.sendEmptyMessage(WHAT_REFRESH)
            }

        })
    }

    override fun initData() {
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            handler?.sendEmptyMessage(WHAT_REFRESH)
        }
    }

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_REFRESH -> Thread {
                try {
                    page = 1
                    searchResult = PBilibiliClient.pAppAPI.search(intent.getStringExtra(SearchManager.QUERY)!!, page)
                    handler?.sendEmptyMessage(WHAT_REFRESH_END)
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread { TipUtil.showToast(e.message) }
                }
            }.start()
            WHAT_REFRESH_END -> {
                baseBind.xrv.refreshComplete()
                baseBind.xrv.adapter = SearchAdapter(this, searchResult!!)
                XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, searchResult!!.data.item.size - 1)
            }
            WHAT_LOAD_MORE -> Thread {
                try {
                    page++
                    val searchResult = PBilibiliClient.pAppAPI.search(intent.getStringExtra(SearchManager.QUERY)!!, page)
                    ListUtil.addAll(this.searchResult!!.data.item, searchResult.data.item)
                    handler?.sendEmptyMessage(WHAT_LOAD_MORE_END)
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread { TipUtil.showToast(e.message) }
                }
            }.start()
            WHAT_LOAD_MORE_END -> {
                baseBind.xrv.loadMoreComplete()
                XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, searchResult!!.data.item.size - 1)
            }
        }
    }
}
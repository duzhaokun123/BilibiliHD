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
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.databinding.ActivitySearchBinding
import com.duzhaokun123.bilibilihd.utils.ListUtil
import com.duzhaokun123.bilibilihd.utils.Settings
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.duzhaokun123.bilibilihd.utils.systemBars
import com.hiczp.bilibili.api.app.model.SearchResult
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class SearchActivity : BaseActivity2<ActivitySearchBinding>() {
    companion object {
        const val WHAT_REFRESH = 0
        const val WHAT_REFRESH_END = 1
        const val WHAT_LOAD_MORE = 2
        const val WHAT_LOAD_MORE_END = 3
    }

    private var page = 1

    private var searchResult: SearchResult? = null

    private lateinit var srl: SmartRefreshLayout
    private lateinit var rv: RecyclerView
    private lateinit var mh: MaterialHeader
    private lateinit var cf: ClassicsFooter

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

    override fun initConfig() = setOf(Config.NEED_HANDLER)
    override fun initLayout() = R.layout.activity_search

    override fun findViews() {
        srl = findViewById(R.id.srl)
        rv = findViewById(R.id.rv)
        mh = findViewById(R.id.mh)
        cf = findViewById(R.id.cf)
    }

    override fun initView() {
        var spanCount = resources.getInteger(R.integer.column_medium)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && Settings.layout.column != 0) {
            spanCount = Settings.layout.column
        } else if (Settings.layout.columnLand != 0) {
            spanCount = Settings.layout.columnLand
        }
        rv.addOnScrollListener(RVAutoSetActionBarUpListener())
        rv.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        if (spanCount == 1) {
            rv.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect[0, 0, 0] = resources.getDimensionPixelOffset(R.dimen.divider_height)
                }
            })
        } else {
            rv.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect[0, 0, resources.getDimensionPixelOffset(R.dimen.divider_height)] = resources.getDimensionPixelOffset(R.dimen.divider_height)
                }
            })
        }
        srl.setOnLoadMoreListener {
            handler?.sendEmptyMessage(WHAT_LOAD_MORE)
        }
        srl.setOnRefreshListener {
            handler?.sendEmptyMessage(WHAT_REFRESH)
        }
    }

    override fun initData() {
        handleIntent(intent)
    }

    override fun onApplyWindowInsets(windowInsetsCompat: WindowInsetsCompat) {
        windowInsetsCompat.systemBars.let {
            srl.updatePadding(top = it.top, bottom = it.bottom)
            mh.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = it.top
            }
            cf.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = -1 * it.bottom
            }
        }
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
                    searchResult = Application.getPBilibiliClient().pAppAPI.search(intent.getStringExtra(SearchManager.QUERY)!!, page)
                    handler?.sendEmptyMessage(WHAT_REFRESH_END)

                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread { TipUtil.showToast(e.message) }
                }
            }.start()
            WHAT_REFRESH_END -> {
                rv.adapter = SearchAdapter(this, searchResult!!)
                rv.adapter!!.notifyItemRangeChanged(0, searchResult!!.data.item.size)
                srl.finishRefresh()
            }
            WHAT_LOAD_MORE -> Thread {
                try {
                    page++
                    val searchResult = Application.getPBilibiliClient().pAppAPI.search(intent.getStringExtra(SearchManager.QUERY)!!, page)
                    ListUtil.addAll(this.searchResult!!.data.item, searchResult.data.item)
                    handler?.sendEmptyMessage(WHAT_LOAD_MORE_END)
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread { TipUtil.showToast(e.message) }
                }
            }.start()
            WHAT_LOAD_MORE_END -> {
                srl.finishLoadMore()
                rv.adapter!!.notifyItemRangeChanged(0, searchResult!!.data.item.size)
            }
        }
    }
}
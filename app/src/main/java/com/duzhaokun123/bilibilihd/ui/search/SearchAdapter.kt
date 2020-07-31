package com.duzhaokun123.bilibilihd.ui.search

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseSimpleAdapter
import com.duzhaokun123.bilibilihd.databinding.ItemSearchCardBinding
import com.duzhaokun123.bilibilihd.ui.UrlOpenActivity
import com.duzhaokun123.bilibilihd.utils.GlideUtil
import com.hiczp.bilibili.api.app.model.SearchResult

class SearchAdapter(context: Context, private val searchResult: SearchResult) : BaseSimpleAdapter<ItemSearchCardBinding>(context) {
    override fun getItemCount() = searchResult.data.item.size

    override fun initLayout() = R.layout.item_search_card

    override fun initView(baseBind: ItemSearchCardBinding, position: Int) {
        searchResult.data.item[position].let { item ->
            baseBind.cv.setOnClickListener {
                val intent = Intent(context, UrlOpenActivity::class.java)
                intent.data = Uri.parse(item.uri)
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }

    override fun initData(baseBind: ItemSearchCardBinding, position: Int) {
        searchResult.data.item[position].let { item ->
            baseBind.htvTitle.setHtml(item.title.replace("<em class=\"keyword\">", "<strong><font color=\"#FB7299\">").replace("</em>", "</font></strong>"))
            baseBind.tvUp.text = item.author
            baseBind.tvLinkType.text = item.linktype
            GlideUtil.loadUrlInto(context, item.cover, baseBind.ivCover, false)
        }
    }
}
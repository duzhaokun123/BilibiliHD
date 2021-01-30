package com.duzhaokun123.bilibilihd.ui.article

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import com.duzhaokun123.bilibilihd.Params
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.databinding.LayoutWebViewBinding
import com.duzhaokun123.bilibilihd.ui.UrlOpenActivity
import com.duzhaokun123.bilibilihd.utils.BrowserUtil
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil
import com.duzhaokun123.bilibilihd.utils.ShareUtil.shareText

class ArticleActivity : BaseActivity2<LayoutWebViewBinding>() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.article_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_in_browser -> {
                BrowserUtil.openCustomTab(this, MyBilibiliClientUtil.getCvUrl(startIntent.getLongExtra("id", 0)))
                true
            }
            R.id.share -> {
                shareText(this, MyBilibiliClientUtil.getCvUrl(startIntent.getLongExtra("id", 0)))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun initConfig() = setOf(Config.FIX_LAYOUT)
    override fun initLayout() = R.layout.layout_web_view

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        baseBind.wv.settings.javaScriptEnabled = true
        baseBind.wv.settings.blockNetworkImage = false
        baseBind.wv.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        baseBind.wv.settings.userAgentString = Params.DESKTOP_USER_AGENT
        baseBind.wv.settings.domStorageEnabled = true
        baseBind.wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val intent = Intent(this@ArticleActivity, UrlOpenActivity::class.java)
                intent.data = request?.url
                startActivity(intent)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                title = url
                baseBind.pb.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                baseBind.pb.visibility = View.INVISIBLE
            }
        }
        baseBind.wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                baseBind.pb.setProgress(newProgress, true)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                setTitle(title)
            }
        }
    }

    override fun initData() {
        baseBind.wv.loadUrl("https://www.bilibili.com/read/cv${startIntent.getLongExtra("id", 0)}")
    }
}
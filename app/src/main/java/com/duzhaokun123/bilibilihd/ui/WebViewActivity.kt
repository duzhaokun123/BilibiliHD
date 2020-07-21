package com.duzhaokun123.bilibilihd.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.duzhaokun123.bilibilihd.Params
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.LayoutWebViewBinding
import com.duzhaokun123.bilibilihd.utils.BrowserUtil

class WebViewActivity : BaseActivity<LayoutWebViewBinding>() {
    companion object {
        const val EXTRA_DESKTOP_UA = "desktop_ua"
        const val EXTRA_INTERCEPT_ALL = "intercept_all"
    }

    override fun initConfig(): Int {
        return FIX_LAYOUT
    }

    override fun initLayout(): Int {
        return R.layout.layout_web_view
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.web_view_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_in_browser -> {
                BrowserUtil.openCustomTab(this, baseBind.wv.url)
                true
            }
            R.id.reload -> {
                baseBind.wv.reload()
                true
            }
            R.id.stop -> {
                baseBind.wv.stopLoading()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.let {
            it.setHomeAsUpIndicator(R.drawable.ic_clear)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        baseBind.wv.settings.javaScriptEnabled = true
        baseBind.wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return if ("bilibili" == request?.url?.scheme || teleportIntent?.extras?.getBoolean(EXTRA_INTERCEPT_ALL)!!) {
                    val intent = Intent(this@WebViewActivity, UrlOpenActivity::class.java)
                    intent.data = request?.url
                    startActivity(intent)
                    true
                } else {
                    false
                }

            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                title = url
                baseBind.pb.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                baseBind.pb.visibility = View.INVISIBLE
            }
        }
        baseBind.wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                baseBind.pb.setProgress(newProgress, true)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                setTitle(title)
            }
        }
        if (teleportIntent?.extras?.getBoolean(EXTRA_DESKTOP_UA, true)!!) {
            baseBind.wv.settings.userAgentString = Params.DESKTOP_USER_AGENT
        }
        baseBind.wv.settings.domStorageEnabled = true
    }

    override fun initData() {
        teleportIntent?.dataString?.let { baseBind.wv.loadUrl(it) }
    }

    override fun onBackPressed() {
        if (baseBind.wv.canGoBack()) {
            baseBind.wv.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

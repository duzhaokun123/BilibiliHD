package com.duzhaokun123.bilibilihd.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.LayoutWebViewBinding
import com.duzhaokun123.bilibilihd.utils.BrowserUtil

class WebViewActivity : BaseActivity<LayoutWebViewBinding>() {
    override fun initConfig(): Int {
        return FIX_LAYOUT
    }

    override fun initLayout(): Int {
        return R.layout.layout_web_view
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.web_view_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_in_browser -> {
                BrowserUtil.openCustomTab(this, teleportIntent?.extras?.getString("url", "")!!)
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
                return if ("bilibili" == request?.url!!.scheme) {
                    val intent = Intent()
                    intent.data = request.url
                    startActivity(intent)
                    false
                } else {
                    baseBind.wv.loadUrl(request.url.toString())
                    true
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
        baseBind.wv.settings.userAgentString = teleportIntent?.extras?.getString("ua")
        baseBind.wv.settings.domStorageEnabled = true
    }

    override fun initData() {
        teleportIntent?.extras?.getString("url", "")?.let { baseBind.wv.loadUrl(it) }
    }

    override fun onBackPressed() {
        if (baseBind.wv.canGoBack()) {
            baseBind.wv.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

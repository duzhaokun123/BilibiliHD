package com.duzhaokun123.bilibilihd.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.ActivityWebViewBinding
import com.duzhaokun123.bilibilihd.utils.BrowserUtil

class WebViewActivity : BaseActivity<ActivityWebViewBinding>() {
    override fun initConfig(): Int {
        return FIX_LAYOUT
    }

    override fun initLayout(): Int {
        return R.layout.activity_web_view
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.web_view_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.open_in_browser -> BrowserUtil.openCustomTab(this, teleportIntent?.extras?.getString("url", "")!!)
            R.id.reload -> baseBind.wv.reload()
            R.id.stop -> baseBind.wv.stopLoading()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        baseBind.wv.settings.javaScriptEnabled = true
        baseBind.wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                baseBind.wv.loadUrl(request.toString())
                return true
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
                baseBind.pb.progress = newProgress
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                setTitle(title)
            }
        }
    }

    override fun initData() {
        baseBind.wv.loadUrl(teleportIntent?.extras?.getString("url", ""))
    }

    override fun onBackPressed() {
        if (baseBind.wv.canGoBack()) {
            baseBind.wv.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

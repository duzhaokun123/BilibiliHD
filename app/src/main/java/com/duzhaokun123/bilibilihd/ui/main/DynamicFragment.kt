package com.duzhaokun123.bilibilihd.ui.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.duzhaokun123.bilibilihd.Params
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseFragment
import com.duzhaokun123.bilibilihd.databinding.LayoutWebViewBinding
import com.duzhaokun123.bilibilihd.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DynamicFragment : BaseFragment<LayoutWebViewBinding>(), Refreshable {
    private var loadFinished = false

    override fun initConfig() = 0
    override fun initLayout() = R.layout.layout_web_view

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        baseBind.wv.apply {
            settings.apply {
                javaScriptEnabled = true
                blockNetworkImage = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                userAgentString = Params.DESKTOP_USER_AGENT
                domStorageEnabled = true
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    BrowserUtil.openCustomTab(requireContext(), request.url.toString())
                    return true
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    baseBind.pb.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView, url: String) {
                    loadFinished = true
                    baseBind.pb.visibility = View.INVISIBLE
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    baseBind.pb.setProgress(newProgress, true)
                }
            }
            addJavascriptInterface(object {
                @JavascriptInterface
                fun viewImage(src: String) {
                    kRunOnUiThread { ImageViewUtil.viewImage(requireContext(), src.replaceAfterInclude('@', "")) }
                }
            }, "app")
        }

        requireBaseActivity2().registerOnApplyWindowInsets(3){windowInsetsCompat ->
            windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()).let {
                baseBind.rl.updatePadding(top = it.top, bottom = it.bottom)
            }
        }
    }

    override fun initData() {
        if (isFirstCreate) {
            baseBind.wv.loadUrl("https://t.bilibili.com")
        }
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch(Dispatchers.Main) {
            while (isStopped.not()) {
                if (loadFinished) { // FIXME: 21-2-2 这没有阻止网页加载图片, 造成更多流量消耗
                    val javaScript = """javascript:
                        document.getElementsByClassName("bp-icon-font icon-ss-dynamic bp-v-middle").forEach(function(e) {
                          var bigP = e.parentNode
                          bigP.onclick = function() {
                            var bic = bigP.parentNode.parentNode.getElementsByClassName("boost-img-container")[0].firstChild
                            app.viewImage(bic.src)
                            document.getElementsByClassName("close-button")[0].click()
                          }
                        })
                    """.trimIndent()
                    baseBind.wv.evaluateJavascript(javaScript, null)
                }
                delay(2000)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        baseBind.wv.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        baseBind.wv.restoreState(savedInstanceState)
    }

    override fun onRefresh() {
        loadFinished = false
        baseBind.wv.reload()
    }
}
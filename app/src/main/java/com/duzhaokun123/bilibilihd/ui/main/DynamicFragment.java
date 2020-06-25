package com.duzhaokun123.bilibilihd.ui.main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.duzhaokun123.bilibilihd.Params;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutWebViewBinding;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;

/**
 * 这个类用 Kotlin 写会崩溃
 */
public class DynamicFragment extends BaseFragment<LayoutWebViewBinding> {

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.layout_web_view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
        baseBind.wv.getSettings().setJavaScriptEnabled(true);
        baseBind.wv.getSettings().setBlockNetworkImage(false);
        baseBind.wv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        baseBind.wv.getSettings().setUserAgentString(Params.DESKTOP_USER_AGENT);
        baseBind.wv.getSettings().setDomStorageEnabled(true);
        baseBind.wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                BrowserUtil.openCustomTab(requireContext(), request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                baseBind.pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                baseBind.pb.setVisibility(View.INVISIBLE);
            }
        });
        baseBind.wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                baseBind.pb.setProgress(newProgress, true);
            }
        });
    }

    @Override
    protected void initData() {
        baseBind.wv.loadUrl("https://t.bilibili.com");
    }
}

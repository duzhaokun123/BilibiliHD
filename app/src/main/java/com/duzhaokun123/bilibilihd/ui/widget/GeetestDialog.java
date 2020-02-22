package com.duzhaokun123.bilibilihd.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.duzhaokun123.bilibilihd.R;

public class GeetestDialog extends Dialog {

    private String url;
    private WebView mWvGeetest;

    public GeetestDialog(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebView getWebView() {
        return mWvGeetest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_geetest_dialog);
        mWvGeetest = findViewById(R.id.wv_geetest);

        mWvGeetest.getSettings().setJavaScriptEnabled(true);
        mWvGeetest.loadUrl(url);

    }
}

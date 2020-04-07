package com.duzhaokun123.bilibilihd.ui.article;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityArticleBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.CustomTabUtil;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;

public class ArticleActivity extends BaseActivity<ActivityArticleBinding> {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.article_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.open_in_browser) {
            if (teleportIntent != null) {
                CustomTabUtil.openUrl(this, MyBilibiliClientUtil.getCvUrl(teleportIntent.getLongExtra("id", 0)));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_article;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
        baseBind.wv.getSettings().setJavaScriptEnabled(true);
        baseBind.wv.getSettings().setBlockNetworkImage(false);
        baseBind.wv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        baseBind.wv.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 9; Mi MIX 2S Build/PQ3A.190801.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045114 Mobile Safari/537.36 BiliApp/5540300");
    }


    @Override
    protected void initData() {
        if (teleportIntent != null) {
            baseBind.wv.loadUrl("https://www.bilibili.com/read/mobile/" + teleportIntent.getLongExtra("id", 0));
        }
    }
}

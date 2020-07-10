package com.duzhaokun123.bilibilihd.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivitySearchBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.hiczp.bilibili.api.app.model.SearchResult;

import java.util.Objects;

public class SearchActivity extends BaseActivity<ActivitySearchBinding> {
    private SearchResult searchResult;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.search_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search) {
            onSearchRequested();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int initConfig() {
        return NEED_HANDLER | FIX_LAYOUT;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        searchResult = PBilibiliClient.Companion.getInstance().getPAppAPI().search(Objects.requireNonNull(intent.getStringExtra(SearchManager.QUERY)));
                        if (handler != null) {
                            handler.sendEmptyMessage(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> TipUtil.showToast(e.getMessage()));
                    }
                }
            }.start();
        }
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        // TODO: 20-4-14
        baseBind.tv.setText(searchResult.toString());
    }
}

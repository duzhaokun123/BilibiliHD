package com.duzhaokun123.bilibilihd.ui.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.duzhaokun123.bilibilihd.utils.Handler;

public abstract class BaseActivity<layout extends ViewDataBinding> extends AppCompatActivity implements Handler.IHandlerMessageCallback {

    protected static final int FULLSCREEN = 0b001;
    protected static final int NEED_HANDLER = 0b010;

    private int config;

    protected layout baseBind;
    public final String CLASS_NAME = this.getClass().getSimpleName();
    @Nullable
    public Handler handler;
    @Nullable
    protected Intent teleportIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        teleportIntent = getIntent();

        config = initConfig();
        if ((config & FULLSCREEN) == FULLSCREEN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if ((config & NEED_HANDLER) == NEED_HANDLER) {
            handler = new Handler(this);
        }

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        baseBind = DataBindingUtil.setContentView(this, initLayout());

        findViews();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((config & FULLSCREEN) == FULLSCREEN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.destroy();
            handler = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    protected abstract int initConfig();
    protected void restoreInstanceState(@NonNull Bundle savedInstanceState) { }
    protected abstract int initLayout();
    protected void findViews() { }
    protected abstract void initView();
    protected abstract void initData();
}

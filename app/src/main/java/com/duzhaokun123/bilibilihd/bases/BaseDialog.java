package com.duzhaokun123.bilibilihd.bases;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.duzhaokun123.bilibilihd.utils.Handler;

public abstract class BaseDialog<layout extends ViewDataBinding> extends AlertDialog implements Handler.IHandlerMessageCallback {
    protected BaseDialog(@NonNull Context context) {
        super(context);
    }

    protected static final int NEED_HANDLER = 0b010;

    private int config;

    protected layout baseBind;
    @Nullable
    protected Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = initConfig();
        if ((config & NEED_HANDLER) == NEED_HANDLER) {
            handler = new Handler(this);
        }

        if (savedInstanceState != null) {
            onRestoreInstanceStateSynchronously(savedInstanceState);
        }

        baseBind = DataBindingUtil.inflate(getLayoutInflater(), initLayout(), null, false);
        View rootView = baseBind.getRoot();
        findViews(rootView);
        initView();
        initData();
        setContentView(rootView);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.destroy();
            handler = null;
        }
    }

    protected abstract int initConfig();

    protected abstract int initLayout();

    protected void onRestoreInstanceStateSynchronously(@NonNull Bundle savedInstanceState) {
    }

    protected void findViews(View rootView) {
    }

    protected abstract void initView();

    protected abstract void initData();
}

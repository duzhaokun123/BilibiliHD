package com.duzhaokun123.bilibilihd.ui.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.duzhaokun123.bilibilihd.utils.Handler;

public abstract class BaseFragment<layout extends ViewDataBinding> extends Fragment implements Handler.IHandlerMessageCallback {

    protected static final String CLASS_NAME = BaseActivity.class.getSimpleName();
    protected static final int NEED_HANDLER = 0b010;

    private int config;

    protected layout baseBind;
    @Nullable
    public Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = initConfig();
        if ((config & NEED_HANDLER) == NEED_HANDLER) {
            handler = new Handler(this);
        }

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseBind = DataBindingUtil.inflate(inflater, initLayout(), container, false);
        View parentView = baseBind.getRoot();
        findViews(parentView);
        initView();
        initData();
        return parentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.destroy();
            handler = null;
        }
    }

    @Nullable
    public BaseActivity getBaseActivity() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            return (BaseActivity) activity;
        } else {
            return null;
        }
    }

    protected abstract int initConfig();
    protected void restoreInstanceState(@NonNull Bundle savedInstanceState) { }
    protected abstract int initLayout();
    protected void findViews(View parentView) { }
    protected abstract void initView();
    protected abstract void initData();

}

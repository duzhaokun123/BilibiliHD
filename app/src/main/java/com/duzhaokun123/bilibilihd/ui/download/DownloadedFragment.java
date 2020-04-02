package com.duzhaokun123.bilibilihd.ui.download;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseFragment;

public class DownloadedFragment extends BaseFragment<LayoutXrecyclerviewOnlyBinding> {
    // TODO: 20-4-2 临时的
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText("TODO");
        return textView;
    }

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    protected int initLayout() {
        return R.layout.layout_xrecyclerview_only;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}

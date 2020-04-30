package com.duzhaokun123.bilibilihd.ui.download;

import android.Manifest;
import android.os.Build;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;

import java.util.Objects;

public class DownloadedFragment extends BaseFragment<LayoutXrecyclerviewOnlyBinding> {
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Objects.requireNonNull(getBaseActivity()).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, null);
        }
    }
}

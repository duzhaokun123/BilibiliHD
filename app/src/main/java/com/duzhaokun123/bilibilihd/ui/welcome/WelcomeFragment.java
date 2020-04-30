package com.duzhaokun123.bilibilihd.ui.welcome;

import com.duzhaokun123.bilibilihd.BuildConfig;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.bases.BaseFragment;
import com.duzhaokun123.bilibilihd.databinding.FragmentWelcomeBinding;

public class WelcomeFragment extends BaseFragment<FragmentWelcomeBinding> {
    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_welcome;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        baseBind.tvVersion.setText(BuildConfig.VERSION_NAME);
    }
}

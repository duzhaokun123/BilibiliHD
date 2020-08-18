package com.duzhaokun123.bilibilihd.ui

import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.ActivityTestBinding

class TestActivity : BaseActivity<ActivityTestBinding>() {

    override fun initConfig() = FIX_LAYOUT
    override fun initLayout() = R.layout.activity_test

    override fun initView() {}

    override fun initData() {}

}
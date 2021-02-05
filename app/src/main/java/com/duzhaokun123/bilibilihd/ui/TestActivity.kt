package com.duzhaokun123.bilibilihd.ui

import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.databinding.ActivityTestBinding

class TestActivity : BaseActivity2<ActivityTestBinding>() {

    override fun initConfig() = setOf(Config.FIX_LAYOUT)
    override fun initLayout() = R.layout.activity_test

    override fun initView() {}

    override fun initData() {}
}
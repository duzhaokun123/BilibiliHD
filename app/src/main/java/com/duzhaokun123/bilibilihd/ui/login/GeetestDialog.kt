package com.duzhaokun123.bilibilihd.ui.login

import android.content.Context
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseDialog
import com.duzhaokun123.bilibilihd.databinding.DialogGeetestBinding

class GeetestDialog(context: Context) : BaseDialog<DialogGeetestBinding>(context) {
    override fun initConfig() = NEED_HANDLER
    override fun initLayout() = R.layout.dialog_geetest
    override fun initView() {}
    override fun initData() {}
}
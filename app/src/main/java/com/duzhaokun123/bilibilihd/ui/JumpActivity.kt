package com.duzhaokun123.bilibilihd.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.core.view.updatePadding
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.databinding.ActivityJumpBinding
import com.duzhaokun123.bilibilihd.utils.TipUtil

class JumpActivity : BaseActivity2<ActivityJumpBinding>() {
    override fun initConfig() = setOf<Config>()
    override fun initLayout() = R.layout.activity_jump

    override fun initView() {
        baseBind.nsv.setOnScrollChangeListener(AutoSetActionBarUpListener())
        val intent = Intent()
        baseBind.btnAdd.setOnClickListener {
            try {
                when (baseBind.rgChoose.checkedRadioButtonId) {
                    R.id.rb_int -> intent.putExtra(baseBind.etKey.text.toString(), baseBind.etValue.text.toString().toInt())
                    R.id.rb_long -> intent.putExtra(baseBind.etKey.text.toString(), baseBind.etValue.text.toString().toLong())
                    R.id.rb_double -> intent.putExtra(baseBind.etKey.text.toString(), baseBind.etValue.text.toString().toDouble())
                    R.id.rb_float -> intent.putExtra(baseBind.etKey.text.toString(), baseBind.etValue.text.toString().toFloat())
                    R.id.rb_boolean -> intent.putExtra(baseBind.etKey.text.toString(), baseBind.etValue.text.toString().toBoolean())
                    R.id.rb_string -> intent.putExtra(baseBind.etKey.text.toString(), baseBind.etValue.text.toString())
                }
                Log.d(className, "${baseBind.etKey.text}: ${baseBind.etValue.text}")
            } catch (e: Exception) {
                e.printStackTrace()
                TipUtil.showToast(e.message)
            }
        }
        baseBind.btnStart.setOnClickListener {
            Log.d(className, "start: ${baseBind.etComponent.text}")
            try {
                val clazz = Class.forName(baseBind.etComponent.text.toString())
                if (Activity::class.java.isAssignableFrom(clazz)) {
                    intent.component = ComponentName(this@JumpActivity, clazz)
                    startActivity(intent)
                } else {
                    TipUtil.showToast("${clazz.name} is not a Activity")
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                TipUtil.showToast("class not found ${e.message}")
            }
        }
    }

    override fun initData() {}

    override fun onWindowFocusChanged(hasFocus: kotlin.Boolean) {
        super.onWindowFocusChanged(hasFocus)
        baseBind.nsv.updatePadding(top = fixTopHeight, bottom = fixBottomHeight)
    }
}
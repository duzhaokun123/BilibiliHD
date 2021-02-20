package com.duzhaokun123.bilibilihd.utils

import android.os.Build
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import com.duzhaokun123.bilibilihd.Application

val WindowInsetsCompat.systemBars: Insets
    get() {
        val re = this.getInsets(WindowInsetsCompat.Type.systemBars())
        return if (Build.VERSION.SDK_INT != Build.VERSION_CODES.R) {
            re
        } else {
            var s = 0
            val app = Application.getInstance()
            val resourceId = app.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                s = app.resources.getDimensionPixelSize(resourceId)
            }
            Insets.of(re.left, re.top + s, re.right, re.bottom)
        }
    }
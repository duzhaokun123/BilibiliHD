package com.duzhaokun123.bilibilihd.utils

import androidx.core.view.WindowInsetsCompat

val WindowInsetsCompat.systemBars
    get() = this.getInsets(WindowInsetsCompat.Type.systemBars())
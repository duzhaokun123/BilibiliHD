package com.duzhaokun123.bilibilihd.utils

import java.util.regex.Pattern

object PatternUtil {
    val avPattern: Pattern by lazy { Pattern.compile("[aA][vV][0-9]+") }
    val bvPattern: Pattern by lazy { Pattern.compile("BV[fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF]+") }
    val cvPattern: Pattern by lazy { Pattern.compile("[cC][vV][0-9]+") }
    val smPattern: Pattern by lazy { Pattern.compile("[sS][mM][0-9]+") }
    val acPattern: Pattern by lazy { Pattern.compile("[aA][cC][0-9]+") }
    val auPattern: Pattern by lazy { Pattern.compile("[aA][uU][0-9]+") }
}
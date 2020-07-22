package com.duzhaokun123.bilibilihd.ui.play

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.ViewGroup
import android.widget.FrameLayout
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.ui.widget.BiliPlayerView

class PlayPresentation(context: Context, display: Display, private val biliPlayerView: BiliPlayerView) : Presentation(context, display) {

    private var frameLayout: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.presentation_play)
        frameLayout = findViewById(R.id.fl)
        biliPlayerView.parent?.let { (it as ViewGroup).removeView(biliPlayerView) }
        frameLayout!!.addView(biliPlayerView)
    }

    fun release() {
        biliPlayerView.parent?.let { (it as ViewGroup).removeView(biliPlayerView) }
    }
}
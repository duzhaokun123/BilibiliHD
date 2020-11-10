package com.duzhaokun123.bilibilihd.ui.play.base

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.ViewGroup
import android.widget.FrameLayout
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.ui.widget.BiliPlayerViewWrapperView

class PlayPresentation(context: Context, display: Display, biliPlayerViewWrapperView: BiliPlayerViewWrapperView) : Presentation(context, display) {

    private var frameLayout: FrameLayout? = null
    private var released = false
    private val biliPlayerView = biliPlayerViewWrapperView.biliPlayerView
    private val oldParent = biliPlayerView.parent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.presentation_play)
        frameLayout = findViewById(R.id.fl)
        (oldParent as ViewGroup).removeView(biliPlayerView)
        frameLayout!!.addView(biliPlayerView)
    }

    fun release() {
        if (released.not()) {
            released = true
            biliPlayerView.parent?.let { (it as ViewGroup).removeView(biliPlayerView) }
            (oldParent as ViewGroup).addView(biliPlayerView)
        }
    }
}
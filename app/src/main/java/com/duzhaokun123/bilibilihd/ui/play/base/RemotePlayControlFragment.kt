package com.duzhaokun123.bilibilihd.ui.play.base

import android.os.Message
import android.view.View
import android.widget.SeekBar
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseFragment
import com.duzhaokun123.bilibilihd.databinding.FragmentRemotePlayControlBinding
import com.google.android.exoplayer2.Player

class RemotePlayControlFragment(private val player: Player, private val displayName: String, private val onDanmakuSwitchClick: (v: View) -> Unit) : BaseFragment<FragmentRemotePlayControlBinding>() {
    companion object {
        const val WHAT_REFRESH_STATUS = 0
    }

    private var duration = 0L
    private var position = 0L
    private var buffered = 0L

    override fun initConfig() = NEED_HANDLER

    override fun initLayout() = R.layout.fragment_remote_play_control

    override fun initView() {
        baseBind.tvDisplayName.text = context?.getString(R.string.connection_to_s, displayName)
        baseBind.sbProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress.toLong() * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        baseBind.ibResume.setOnClickListener { player.playWhenReady = true }
        baseBind.ibPause.setOnClickListener { player.playWhenReady = false }
        baseBind.btnDanmakuSwitch.setOnClickListener(onDanmakuSwitchClick)
    }

    override fun initData() {
        handler?.sendEmptyMessage(WHAT_REFRESH_STATUS)
    }

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            WHAT_REFRESH_STATUS -> {
                duration = player.contentDuration
                position = player.contentPosition
                buffered = player.contentBufferedPosition

                baseBind.sbProgress.max = (duration / 1000).toInt()
                baseBind.sbProgress.progress = (position / 1000).toInt()
                baseBind.sbProgress.secondaryProgress = (buffered / 1000).toInt()

                if (player.playWhenReady) {
                    baseBind.ibPause.visibility = View.VISIBLE
                    baseBind.ibResume.visibility = View.GONE
                } else {
                    baseBind.ibPause.visibility = View.GONE
                    baseBind.ibResume.visibility = View.VISIBLE
                }
                handler?.sendEmptyMessageDelayed(WHAT_REFRESH_STATUS, 500)
            }
        }
    }
}
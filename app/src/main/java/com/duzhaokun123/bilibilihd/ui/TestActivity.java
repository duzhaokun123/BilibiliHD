package com.duzhaokun123.bilibilihd.ui;

import android.net.Uri;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityTestBinding;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class TestActivity extends BaseActivity<ActivityTestBinding> {
    private SimpleExoPlayer player;

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        player = new SimpleExoPlayer.Builder(this).build();
        baseBind.pv.setPlayer(player);
        baseBind.btnPlay.setOnClickListener(v -> {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));
            MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(baseBind.etVideo.getText().toString()));
            if (baseBind.cbVideoOnly.isChecked()) {
                player.prepare(videoSource);
            } else {
                MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(baseBind.etAudio.getText().toString()));
                MediaSource mediaSource = new MergingMediaSource(videoSource, audioSource);
                player.prepare(mediaSource);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}

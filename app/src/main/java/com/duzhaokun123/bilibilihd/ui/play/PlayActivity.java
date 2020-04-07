package com.duzhaokun123.bilibilihd.ui.play;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Rational;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityPlayBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.DownloadUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hiczp.bilibili.api.app.model.View;
import com.hiczp.bilibili.api.player.model.VideoPlayUrl;

import java.util.Objects;

public class PlayActivity extends BaseActivity<ActivityPlayBinding> {

    private TextView mTv;
    private ImageView mIv;
    private Button mBtnPip;

    private SimpleExoPlayer player;

    private PBilibiliClient pBilibiliClient;
    private VideoPlayUrl videoPlayUrl;
    private View mView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            videoPlayUrl = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("videoPlayUrl"), VideoPlayUrl.class);
            mView = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("mView"), View.class);
        }

        mTv = findViewById(R.id.tv);
        mBtnPip = findViewById(R.id.btn_pip);

        mBtnPip.setOnClickListener(v -> {
            Rational rational = new Rational(mView.getData().getPages().get(0).getDimension().getWidth(), mView.getData().getPages().get(0).getDimension().getHeight());
            if (rational.doubleValue() > 0.418410 && rational.doubleValue() < 2.390000) {
                PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder()
                        .setAspectRatio(rational)
                        .build();
                enterPictureInPictureMode(pictureInPictureParams);
            } else {
                ToastUtil.sendMsg(PlayActivity.this, R.string.inappropriate);
            }
        });

        mIv = new ImageView(PlayActivity.this);
        mIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Log.d("PlayActivity", String.valueOf(getIntent().getExtras().getLong("aid", 0)));
        setTitle(String.valueOf(getIntent().getExtras().getLong("aid", 0)));
        pBilibiliClient = PBilibiliClient.Companion.getInstance();

        new Thread() {
            @Override
            public void run() {
                if (videoPlayUrl == null) {
                    try {
                        mView = pBilibiliClient.getPAppAPI().view(getIntent().getExtras().getLong("aid", 0));
                        videoPlayUrl = pBilibiliClient.getPPlayerAPI().videoPlayUrl(getIntent().getExtras().getLong("aid", 0), mView.getData().getCid());
                        if (handler != null) {
                            handler.sendEmptyMessage(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> ToastUtil.sendMsg(PlayActivity.this, e.getMessage()));
                    }
                }
            }
        }.start();
        baseBind.btnStart.setOnClickListener(v -> player.setPlayWhenReady(true));
        baseBind.btnPause.setOnClickListener(v -> player.setPlayWhenReady(false));
    }

    @Override
    public int initLayout() {
        return R.layout.activity_play;
    }

    @Override
    public void initView() {
        player = new SimpleExoPlayer.Builder(this).build();
        baseBind.pv.setPlayer(player);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        ViewGroup.LayoutParams params = baseBind.pv.getLayoutParams();
        if (isInPictureInPictureMode) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(getSupportActionBar()).hide();
        } else {
            params.height = OtherUtils.dp2px(this, 250);
            Objects.requireNonNull(getSupportActionBar()).show();
        }
        baseBind.pv.setLayoutParams(params);
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));
                MediaSource mediaSource = null;
                if (videoPlayUrl.getData().getDash() != null) {
//                        mGsyVideo.setUp(, "");
                    MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl()));
                    MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl()));
                    mediaSource = new MergingMediaSource(videoSource, audioSource);
                    Log.d(CLASS_NAME, "video " + videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl());
                    Log.d(CLASS_NAME, "audio " + videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl());
                    baseBind.tvVideo.setText(videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl());
                    baseBind.tvAudio.setText(videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl());
                    baseBind.btnDownload.setOnClickListener(v -> DownloadUtil.downloadVideo(PlayActivity.this,
                            videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl(),
                            videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl(),
                            mView.getData().getPages().get(0).getPart(),
                            mView.getData().getTitle(),
                            mView.getData().getBvid(),
                            false,
                            1,
                            mView.getData().getPages().get(0).getDmlink()));
                }
                if (videoPlayUrl.getData().getDurl() != null) {
                    mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(videoPlayUrl.getData().getDurl().get(0).getUrl()));
                    baseBind.tvVideo.setText(videoPlayUrl.getData().getDurl().get(0).getUrl());
                    baseBind.btnDownload.setOnClickListener(v -> DownloadUtil.downloadVideo(PlayActivity.this,
                            videoPlayUrl.getData().getDurl().get(0).getUrl(),
                            null,
                            mView.getData().getPages().get(0).getPart(),
                            mView.getData().getTitle(),
                            mView.getData().getBvid(),
                            true,
                            1,
                            mView.getData().getPages().get(0).getDmlink()));
                }
                player.prepare(mediaSource);
                Glide.with(PlayActivity.this).load(mView.getData().getPic()).into(mIv);
                mTv.setText(videoPlayUrl.toString());
                baseBind.tvDanmaku.setText(mView.getData().getPages().get(0).getDmlink());
                setTitle(mView.getData().getTitle());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("videoPlayUrl", GsonUtil.getGsonInstance().toJson(videoPlayUrl));
        outState.putString("mView", GsonUtil.getGsonInstance().toJson(mView));
    }
}

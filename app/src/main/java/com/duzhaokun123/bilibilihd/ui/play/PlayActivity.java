package com.duzhaokun123.bilibilihd.ui.play;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.Rational;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityPlayBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.hiczp.bilibili.api.app.model.View;
import com.hiczp.bilibili.api.player.model.VideoPlayUrl;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class PlayActivity extends BaseActivity<ActivityPlayBinding> {

    private TextView mTv;
    private StandardGSYVideoPlayer mGsyVideo;
    private ImageView mIv;
    private Button mBtnPip;

    private PBilibiliClient pBilibiliClient;
    private VideoPlayUrl videoPlayUrl;
    private Handler handler;
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
        mGsyVideo = findViewById(R.id.gsy);
        mBtnPip = findViewById(R.id.btn_pip);

        handler = new Handler();

        mBtnPip.setOnClickListener(v -> {
            mGsyVideo.startWindowFullscreen(PlayActivity.this, false, true);
            Rational rational = new Rational(mGsyVideo.getCurrentVideoWidth(), mGsyVideo.getCurrentVideoHeight());
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
        mGsyVideo.setThumbImageView(mIv);
        mGsyVideo.getTitleTextView().setVisibility(android.view.View.VISIBLE);
        mGsyVideo.getBackButton().setVisibility(android.view.View.VISIBLE);
        mGsyVideo.setIsTouchWiget(true);
        mGsyVideo.getBackButton().setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                onBackPressed();
            }
        });
        mGsyVideo.getBackButton().setImageResource(R.drawable.ic_arrow_back);
        mGsyVideo.startPlayLogic();
        mGsyVideo.getFullscreenButton().setOnClickListener(v -> mGsyVideo.startWindowFullscreen(PlayActivity.this, false, true));


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
                        handler.sendEmptyMessage(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> ToastUtil.sendMsg(PlayActivity.this, e.getMessage()));
                    }
                }
            }
        }.start();

        baseBind.btnStart.setOnClickListener(v -> baseBind.gsy.onVideoResume(false));
        baseBind.btnPause.setOnClickListener(v -> baseBind.gsy.onVideoPause());
    }

    @Override
    public int initLayout() {
        return R.layout.activity_play;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    class Handler extends android.os.Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    mGsyVideo.setUp(videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl(),
                            true, mView.getData().getTitle());
                    Glide.with(PlayActivity.this).load(mView.getData().getPic()).into(mIv);
                    mTv.setText(videoPlayUrl.toString());

                    Log.d(CLASS_NAME, "video " + videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl());
                    Log.d(CLASS_NAME, "audio " + videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl());

                    baseBind.tvVideo.setText(videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl());
                    baseBind.tvAudio.setText(videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl());
                    baseBind.tvDanmaku.setText(mView.getData().getPages().get(0).getDmlink());

                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGsyVideo.onVideoPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGsyVideo.onVideoResume(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mGsyVideo.setVideoAllCallBack(null);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("videoPlayUrl", GsonUtil.getGsonInstance().toJson(videoPlayUrl));
        outState.putString("mView", GsonUtil.getGsonInstance().toJson(mView));
    }
}

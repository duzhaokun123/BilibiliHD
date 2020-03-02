package com.duzhaokun123.bilibilihd.ui.play;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.pBilibiliApi.api.PBilibiliClient;
import com.hiczp.bilibili.api.app.model.View;
import com.hiczp.bilibili.api.player.model.VideoPlayUrl;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class PlayActivity extends AppCompatActivity {

    private TextView mTv;
    private StandardGSYVideoPlayer mGsy;
    private ImageView mIv;

    private PBilibiliClient pBilibiliClient;
    private VideoPlayUrl videoPlayUrl;
    private Handler handler;
    private View mView;
    private OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mTv = findViewById(R.id.tv);
        mGsy = findViewById(R.id.gsy);

        handler = new Handler();

        mIv = new ImageView(PlayActivity.this);
        mIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mGsy.setThumbImageView(mIv);
        mGsy.getTitleTextView().setVisibility(android.view.View.VISIBLE);
        mGsy.getBackButton().setVisibility(android.view.View.VISIBLE);
        orientationUtils = new OrientationUtils(this, mGsy);
        mGsy.getFullscreenButton().setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                orientationUtils.resolveByClick();
            }
        });
        mGsy.setIsTouchWiget(true);
        mGsy.getBackButton().setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                onBackPressed();
            }
        });
        mGsy.startPlayLogic();

        setTitle("av" + getIntent().getExtras().getString("aid"));
        pBilibiliClient = PBilibiliClient.Companion.getPBilibiliClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mView = pBilibiliClient.getPAppAPI().view(Long.parseLong(getIntent().getExtras().getString("aid")));
                videoPlayUrl = pBilibiliClient.getPPlayerAPI().videoPlayUrl(Long.parseLong(getIntent().getExtras().getString("aid")), mView.getData().getCid());
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    class Handler extends android.os.Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mTv.setText(videoPlayUrl.toString());
            mGsy.setUp(videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl(), true, mView.getData().getTitle());
            Glide.with(PlayActivity.this).load(mView.getData().getPic()).into(mIv);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGsy.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGsy.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mGsy.getFullscreenButton().performClick();
            return;
        }
        mGsy.setVideoAllCallBack(null);
        super.onBackPressed();
    }
}

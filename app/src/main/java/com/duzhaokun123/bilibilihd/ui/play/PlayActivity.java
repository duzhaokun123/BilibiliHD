package com.duzhaokun123.bilibilihd.ui.play;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Rational;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityPlayBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.CustomTabUtil;
import com.duzhaokun123.bilibilihd.utils.DownloadUtil;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.SimpleDateFormatUtil;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hiczp.bilibili.api.player.model.VideoPlayUrl;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayActivity extends BaseActivity<ActivityPlayBinding> {
    private ImageButton mIbFullscreen;
    private Button mBtnDanmakuSwitch, mBtnDanmaku;
    private TextView mTvUpName, mTvUpFans;
    private CircleImageView mCivFace;

    private SimpleExoPlayer player;

    private VideoPlayUrl videoPlayUrl;
    private com.hiczp.bilibili.api.app.model.View biliView;
    private boolean fullscreen = false;
    private long aid;

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_play;
    }

    @Override
    protected void restoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.restoreInstanceState(savedInstanceState);
        videoPlayUrl = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("videoPlayUrl"), VideoPlayUrl.class);
        biliView = GsonUtil.getGsonInstance().fromJson(savedInstanceState.getString("biliView"), com.hiczp.bilibili.api.app.model.View.class);
        fullscreen = savedInstanceState.getBoolean("fullscreen");
        aid = savedInstanceState.getLong("aid");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.play_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_in_browser:
                if (teleportIntent != null) {
                    CustomTabUtil.openUrl(this, MyBilibiliClientUtil.getB23Url(teleportIntent.getLongExtra("aid", 0)));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fullscreen) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                    | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void findViews() {
        mIbFullscreen = baseBind.pv.findViewById(R.id.ib_fullscreen);
        mBtnDanmakuSwitch = baseBind.pv.findViewById(R.id.btn_danmaku_switch);
        mBtnDanmaku = baseBind.pv.findViewById(R.id.btn_danmaku);
        mTvUpName = findViewById(R.id.tv_name);
        mTvUpFans = findViewById(R.id.tv_content);
        mCivFace = findViewById(R.id.civ_face);
    }

    @Override
    public void initView() {
        baseBind.btnPip.setOnClickListener(v -> {
            Rational rational = new Rational(biliView.getData().getPages().get(0).getDimension().getWidth(), biliView.getData().getPages().get(0).getDimension().getHeight());
            if (rational.doubleValue() > 0.418410 && rational.doubleValue() < 2.390000) {
                PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder()
                        .setAspectRatio(rational)
                        .build();
                enterPictureInPictureMode(pictureInPictureParams);
            } else {
                ToastUtil.sendMsg(PlayActivity.this, R.string.inappropriate);
            }
        });
        player = new SimpleExoPlayer.Builder(this).build();
        baseBind.pv.setPlayer(player);
        baseBind.pv.setControllerVisibilityListener(visibility -> {
            if (visibility != android.view.View.VISIBLE) {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LOW_PROFILE);
                if (fullscreen) {
                    getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_FULLSCREEN);
                }
                Objects.requireNonNull(getSupportActionBar()).hide();
            } else {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LOW_PROFILE);
                if (fullscreen) {
                    getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_FULLSCREEN);
                }
                Objects.requireNonNull(getSupportActionBar()).show();
            }
        });
        baseBind.pv.setOnClickListener(new android.view.View.OnClickListener() {
            long lastClick = 0;

            @Override
            public void onClick(android.view.View v) {
                if (System.currentTimeMillis() - lastClick < 200) {
                    player.setPlayWhenReady(!player.getPlayWhenReady());
                } else {
                    lastClick = System.currentTimeMillis();
                }
            }
        });
        mIbFullscreen.setOnClickListener(v -> {
            fullscreen = !fullscreen;
            ViewGroup.LayoutParams params = baseBind.pv.getLayoutParams();
            if (fullscreen) {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                Objects.requireNonNull(getSupportActionBar()).hide();
                mIbFullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                params.height = OtherUtils.dp2px(this, 300);
                Objects.requireNonNull(getSupportActionBar()).show();
                mIbFullscreen.setImageResource(R.drawable.ic_fullscreen);
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                        & ~(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY));
            }
            baseBind.pv.setLayoutParams(params);
        });
        mBtnDanmakuSwitch.setOnClickListener(v -> {
            if (mBtnDanmaku.getVisibility() == View.VISIBLE) {
                mBtnDanmaku.setVisibility(View.INVISIBLE);
            } else {
                mBtnDanmaku.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void initData() {
        if (aid == 0) {
            aid = Objects.requireNonNull(getIntent().getExtras()).getLong("aid", 0);
            new LoadBiliView().start();
        }
        setTitle("");
        baseBind.tvId.setText(MyBilibiliClientUtil.av2bv(aid));
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        ViewGroup.LayoutParams params = baseBind.pv.getLayoutParams();
        if (isInPictureInPictureMode) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(getSupportActionBar()).hide();
            baseBind.pv.setUseController(false);
        } else {
            if (fullscreen) {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                Objects.requireNonNull(getSupportActionBar()).hide();
            } else {
                params.height = OtherUtils.dp2px(this, 300);
                Objects.requireNonNull(getSupportActionBar()).show();
            }
            baseBind.pv.setUseController(true);
        }
        baseBind.pv.setLayoutParams(params);
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                new LoadVideoPlayUrl(biliView.getData().getCid()).start();
                GlideUtil.loadUrlInto(this, biliView.getData().getOwner().getFace(), mCivFace, false);
                mCivFace.setOnClickListener(v -> {
                    Intent intent = new Intent(this, UserSpaceActivity.class);
                    intent.putExtra("uid", biliView.getData().getOwner().getMid());
                    startActivity(intent);
                });
                mTvUpName.setText(biliView.getData().getOwner().getName());
                mTvUpName.setOnClickListener(v -> mCivFace.callOnClick());
                mTvUpFans.setText(getString(R.string.num_fans, biliView.getData().getOwnerExt().getFans()));
                baseBind.tvDesc.setText(biliView.getData().getDesc());
                baseBind.tvUptime.setText(SimpleDateFormatUtil.getFormat1().format(biliView.getData().getPubdate() * 1000L));
                baseBind.tvDanmakuHas.setText(String.valueOf(biliView.getData().getStat().getDanmaku()));
                baseBind.tvWatched.setText(String.valueOf(biliView.getData().getStat().getView()));
                break;
            case 1:
                if (videoPlayUrl.getData().getDash() != null && videoPlayUrl.getData().getDash().getAudio() != null) {
                    setPlayerUrl(videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl(),
                            videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl());
                    baseBind.btnDownload.setOnClickListener(v -> DownloadUtil.downloadVideo(PlayActivity.this,
                            videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl(),
                            videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl(),
                            biliView.getData().getPages().get(0).getPart(),
                            biliView.getData().getTitle(),
                            biliView.getData().getBvid(),
                            false,
                            1,
                            biliView.getData().getPages().get(0).getDmlink()));
                } else if (videoPlayUrl.getData().getDash() != null) {
                    setPlayerUrl(videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl(), null);
                    baseBind.btnDownload.setOnClickListener(v -> DownloadUtil.downloadVideo(PlayActivity.this,
                            videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl(),
                            null,
                            biliView.getData().getPages().get(0).getPart(),
                            biliView.getData().getTitle(),
                            biliView.getData().getBvid(),
                            true,
                            1,
                            biliView.getData().getPages().get(0).getDmlink()));
                }
                if (videoPlayUrl.getData().getDurl() != null) {
                    setPlayerUrl(videoPlayUrl.getData().getDurl().get(0).getUrl(), null);
                    baseBind.btnDownload.setOnClickListener(v -> DownloadUtil.downloadVideo(PlayActivity.this,
                            videoPlayUrl.getData().getDurl().get(0).getUrl(),
                            null,
                            biliView.getData().getPages().get(0).getPart(),
                            biliView.getData().getTitle(),
                            biliView.getData().getBvid(),
                            true,
                            1,
                            biliView.getData().getPages().get(0).getDmlink()));
                }
                baseBind.tvTitle.setText(biliView.getData().getTitle());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    @Override
    public void onBackPressed() {
        if (fullscreen) {
            mIbFullscreen.callOnClick();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("videoPlayUrl", GsonUtil.getGsonInstance().toJson(videoPlayUrl));
        outState.putString("biliView", GsonUtil.getGsonInstance().toJson(biliView));
        outState.putBoolean("fullscreen", fullscreen);
        outState.putLong("aid", aid);
    }

    private void setPlayerUrl(@NonNull String video, @Nullable String audio) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));
        MediaSource mediaSource;
        if (audio != null) {
            MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(video));
            MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(audio));
            mediaSource = new MergingMediaSource(videoSource, audioSource);
        } else {
            mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(video));
        }
        player.prepare(mediaSource);
    }

    class LoadBiliView extends Thread {
        @Override
        public void run() {
            try {
                biliView = PBilibiliClient.Companion.getInstance().getPAppAPI().view(aid);
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> ToastUtil.sendMsg(PlayActivity.this, e.getMessage()));
            }
        }
    }

    class LoadVideoPlayUrl extends Thread {
        long cid;

        LoadVideoPlayUrl(long cid) {
            this.cid = cid;
        }

        @Override
        public void run() {
            try {
                videoPlayUrl = PBilibiliClient.Companion.getInstance().getPPlayerAPI().videoPlayUrl(aid, cid);
                if (handler != null) {
                    handler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> ToastUtil.sendMsg(PlayActivity.this, e.getMessage()));
            }
        }
    }
}

package com.duzhaokun123.bilibilihd.ui.play;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityPlayBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.BiliDanmakuParser;
import com.duzhaokun123.bilibilihd.utils.CustomTabUtil;
import com.duzhaokun123.bilibilihd.utils.DownloadUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hiczp.bilibili.api.danmaku.Danmaku;
import com.hiczp.bilibili.api.danmaku.DanmakuParser;
import com.hiczp.bilibili.api.player.model.VideoPlayUrl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import kotlin.Pair;
import kotlin.sequences.Sequence;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import okhttp3.ResponseBody;

public class PlayActivity extends BaseActivity<ActivityPlayBinding> {
    private ImageButton mIbFullscreen;
    private Button mBtnDanmakuSwitch, mBtnDanmaku;

    private SimpleExoPlayer player;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser mParser;

    private VideoPlayUrl videoPlayUrl;
    private com.hiczp.bilibili.api.app.model.View biliView;
    private boolean fullscreen = false;
    private long aid;
    private int page;
    private boolean playingBeforeActivityPause = false;
    private VideoDownloadInfo videoDownloadInfo;

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
        page = savedInstanceState.getInt("page");
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
                    CustomTabUtil.openUrl(this, MyBilibiliClientUtil.getB23Url(aid));
                }
                return true;
            case R.id.download:
                if (videoDownloadInfo != null) {
                    videoDownloadInfo.startDownload(this);
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
        baseBind.pv.onResume();
        if (playingBeforeActivityPause) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void findViews() {
        mIbFullscreen = baseBind.pv.findViewById(R.id.ib_fullscreen);
        mBtnDanmakuSwitch = baseBind.pv.findViewById(R.id.btn_danmaku_switch);
        mBtnDanmaku = baseBind.pv.findViewById(R.id.btn_danmaku);
    }

    @Override
    public void initView() {
//        baseBind.btnPip.setOnClickListener(v -> {
//            Rational rational = new Rational(biliView.getData().getPages().get(page - 1).getDimension().getWidth(), biliView.getData().getPages().get(page - 1).getDimension().getHeight());
//            if (rational.doubleValue() > 0.418410 && rational.doubleValue() < 2.390000) {
//                PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder()
//                        .setAspectRatio(rational)
//                        .build();
//                enterPictureInPictureMode(pictureInPictureParams);
//            } else {
//                ToastUtil.sendMsg(PlayActivity.this, R.string.inappropriate);
//            }
//        });
        player = new SimpleExoPlayer.Builder(this).build();
        player.addListener(new DebugTextViewHelper(player, baseBind.tvPlayerLog));
        player.addListener(new Player.EventListener() {

            @Override
            public void onSeekProcessed() {
                baseBind.dv.seekTo(player.getContentPosition());
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                // TODO: 20-4-10
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    baseBind.dv.resume();
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    baseBind.dv.pause();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                ToastUtil.sendMsg(PlayActivity.this, error.getMessage());
            }
        });
        baseBind.pv.setPlayer(player);
        baseBind.pv.setControllerVisibilityListener(visibility -> {
            if (visibility != android.view.View.VISIBLE) {
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LOW_PROFILE);
                if (fullscreen) {
                    getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
            ViewGroup.LayoutParams params = baseBind.rl.getLayoutParams();
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
            baseBind.rl.setLayoutParams(params);
        });
        mBtnDanmakuSwitch.setOnClickListener(v -> {
            if (mBtnDanmaku.getVisibility() == View.VISIBLE) {
                mBtnDanmaku.setVisibility(View.INVISIBLE);
                baseBind.dv.hide();
            } else {
                mBtnDanmaku.setVisibility(View.VISIBLE);
                baseBind.dv.show();
            }
        });
        baseBind.tl.setupWithViewPager(baseBind.vp);

//        // 设置最大显示行数
//        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
//        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
//        // 设置是否禁止重叠
//        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
//        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
//        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
//        danmakuContext = DanmakuContext.create();
//        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
////                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
////        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
//                .setMaximumLines(maxLinesPair)
//                .preventOverlapping(overlappingEnablePair).setDanmakuMargin(40);
//        baseBind.dv.showFPS(true);
//        baseBind.dv.enableDanmakuDrawingCache(true);
    }

    @Override
    public void initData() {
        if (aid == 0) {
            if (teleportIntent != null) {
                aid = teleportIntent.getLongExtra("aid", 0);
                page = teleportIntent.getIntExtra("page", 1);
            }
            new LoadBiliView().start();
        }
        setTitle("");
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        ViewGroup.LayoutParams params = baseBind.rl.getLayoutParams();
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
        baseBind.rl.setLayoutParams(params);
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                baseBind.vp.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), 1));
                break;
            case 1:
                videoPlayUrl = GsonUtil.getGsonInstance().fromJson(msg.getData().getString("videoPlayUrl"), VideoPlayUrl.class);
                page = msg.getData().getInt("page");
                if (videoDownloadInfo == null) {
                    videoDownloadInfo = new VideoDownloadInfo();
                }
                if (videoPlayUrl.getData().getDash() != null && videoPlayUrl.getData().getDash().getAudio() != null) {
                    setPlayerUrl(videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl(),
                            videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl());
                    videoDownloadInfo.videoUrl = videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl();
                    videoDownloadInfo.audioUrl = videoPlayUrl.getData().getDash().getAudio().get(0).getBaseUrl();
                    videoDownloadInfo.videoOnly = false;
                } else if (videoPlayUrl.getData().getDash() != null) {
                    setPlayerUrl(videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl(), null);
                    videoDownloadInfo.videoUrl = videoPlayUrl.getData().getDash().getVideo().get(0).getBaseUrl();
                    videoDownloadInfo.audioUrl = null;
                    videoDownloadInfo.videoOnly = true;
                }
                if (videoPlayUrl.getData().getDurl() != null) {
                    setPlayerUrl(videoPlayUrl.getData().getDurl().get(0).getUrl(), null);
                    videoDownloadInfo.videoUrl = videoPlayUrl.getData().getDurl().get(0).getUrl();
                    videoDownloadInfo.audioUrl = null;
                    videoDownloadInfo.videoOnly = true;
                }
                videoDownloadInfo.videoTitle = biliView.getData().getPages().get(page - 1).getPart();
                videoDownloadInfo.mainTitle = biliView.getData().getTitle();
                videoDownloadInfo.bvid = biliView.getData().getBvid();
                videoDownloadInfo.cid = biliView.getData().getPages().get(page - 1).getCid();
                videoDownloadInfo.page = page;
                setTitle(biliView.getData().getPages().get(page - 1).getPart());

                new Thread() {
                    @Override
                    public void run() {
                        ResponseBody responseBody = null;
                        try {
                             responseBody = PBilibiliClient.Companion.getInstance().getPDanmakuAPI().list(aid, biliView.getData().getPages().get(page - 1).getCid());
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> ToastUtil.sendMsg(PlayActivity.this, e.getMessage()));
                        }
                        if (responseBody != null) {
                            Pair<Map<Long, Integer>, Sequence<Danmaku>> mapSequencePair = DanmakuParser.INSTANCE.parse(responseBody.byteStream());
//                            mParser = createParser(responseBody.byteStream());
//                            baseBind.dv.prepare(mParser, danmakuContext);
                        }

                    }
                }.start();
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        baseBind.pv.onPause();
        if (playingBeforeActivityPause = player.isPlaying()) {
            player.setPlayWhenReady(false);
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
        outState.putInt("page", page);
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

    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            if (loader != null) {
                loader.load(stream);
            }
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmakuParser();
        IDataSource<?> dataSource = null;
        if (loader != null) {
            dataSource = loader.getDataSource();
        }
        parser.load(dataSource);
        return parser;

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

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        MyFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return IntroFragment.getInstance(biliView, aid, page);
            } else {
                return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.intro);
            } else {
                return getString(R.string.comment_num, biliView.getData().getStat().getReply());
            }
        }
    }

    static class VideoDownloadInfo {
        String videoUrl, audioUrl;
        String mainTitle, videoTitle;
        String bvid;
        long cid;
        int page;
        boolean videoOnly;

        void startDownload(Context context) {
            DownloadUtil.downloadVideo(context, videoUrl, audioUrl, videoTitle, mainTitle, bvid, videoOnly, page, cid);
        }
    }
}

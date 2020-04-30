package com.duzhaokun123.bilibilihd.ui.play;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.Rational;
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
import com.duzhaokun123.bilibilihd.ui.PhotoViewActivity;
import com.duzhaokun123.bilibilihd.bases.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.BiliDanmakuParser;
import com.duzhaokun123.bilibilihd.utils.BrowserUtil;
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil;
import com.duzhaokun123.bilibilihd.utils.DownloadUtil;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
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
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hiczp.bilibili.api.player.model.VideoPlayUrl;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import kotlin.Pair;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Danmaku;
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import okhttp3.ResponseBody;

public class PlayActivity extends BaseActivity<ActivityPlayBinding> {
    private ImageButton mIbFullscreen, mIbNext;
    private Button mBtnDanmakuSwitch, mBtnDanmaku, mBtnQuality;

    private SimpleExoPlayer player;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser mParser;
    private IntroFragment introFragment;
    private DanmakuSendFragment danmakuSendFragment;

    private VideoPlayUrl videoPlayUrl;
    private com.hiczp.bilibili.api.app.model.View biliView;
    private boolean fullscreen = false;
    private long aid;
    private int page;
    private boolean playingBeforeActivityPause = false;
    private boolean playingBeforeTryToSendDanmaku;
    private VideoDownloadInfo videoDownloadInfo;
    private Map<Integer, String> videoQualityMap;

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_play;
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
                    BrowserUtil.openDefault(this, MyBilibiliClientUtil.getB23Url(aid));
                }
                return true;
            case R.id.pip:
                Rational rational = new Rational(biliView.getData().getPages().get(page - 1).getDimension().getWidth(), biliView.getData().getPages().get(page - 1).getDimension().getHeight());
                PictureInPictureParams pictureInPictureParams;
                if (rational.doubleValue() > 0.418410 && rational.doubleValue() < 2.390000) {
                    pictureInPictureParams = new PictureInPictureParams.Builder()
                            .setAspectRatio(rational)
                            .build();
                } else {
                    pictureInPictureParams = new PictureInPictureParams.Builder()
                            .build();
                }
                enterPictureInPictureMode(pictureInPictureParams);
                return true;
            case R.id.check_cover:
                if (biliView != null) {
                    Intent intent = new Intent(this, PhotoViewActivity.class);
                    intent.putExtra("url", biliView.getData().getPic());
                    startActivity(intent);
                }
                return true;
            case R.id.download:
                if (videoDownloadInfo != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        videoDownloadInfo.startDownload(this);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, grantResults -> {
                            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                videoDownloadInfo.startDownload(this);
                            }
                        });
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playingBeforeActivityPause) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fullscreen) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        baseBind.pv.onResume();
    }

    @Override
    protected void findViews() {
        mIbFullscreen = baseBind.pv.findViewById(R.id.ib_fullscreen);
        mBtnDanmakuSwitch = baseBind.pv.findViewById(R.id.btn_danmaku_switch);
        mBtnDanmaku = baseBind.pv.findViewById(R.id.btn_danmaku);
        mBtnQuality = baseBind.pv.findViewById(R.id.btn_quality);
        mIbNext = baseBind.pv.findViewById(R.id.ib_next);
    }

    @Override
    public void initView() {
        player = new SimpleExoPlayer.Builder(this).build();
        player.addListener(new Player.EventListener() {
            @Override
            public void onSeekProcessed() {
                baseBind.dv.seekTo(player.getContentPosition());
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    baseBind.dv.resume();
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    baseBind.pbLoading.setVisibility(View.INVISIBLE);
                } else {
                    baseBind.dv.pause();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    long contentPosition = player.getContentPosition();
                    long contentDuration = player.getContentDuration();
                    long contentBufferedPosition = player.getContentBufferedPosition();
                    if (contentBufferedPosition - contentPosition <= 1000 && contentDuration - contentPosition > 100) {
                        baseBind.pbLoading.setVisibility(View.VISIBLE);
                    } else {
                        baseBind.pv.showController();
                    }
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
                mIbNext.setVisibility(View.VISIBLE);
            } else {
                params.height = OtherUtils.dp2px(this, 300);
                Objects.requireNonNull(getSupportActionBar()).show();
                mIbFullscreen.setImageResource(R.drawable.ic_fullscreen);
                getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                        & ~(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY));
                mIbNext.setVisibility(View.GONE);
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

        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 10); // 滚动弹幕最大显示10行

        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, false); // 允许从右至左的弹幕重合
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);// 不允许从顶部弹幕重合
        danmakuContext = DanmakuContext.create();
        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(true)
                .setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
//                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
//                .setMaximumLines(maxLinesPair) //设置最大行数
                .preventOverlapping(overlappingEnablePair)
                .setDanmakuMargin(getResources().getInteger(R.integer.danmaku_margin))
                .setMaximumVisibleSizeInScreen(0)
                .setScaleTextSize(getResources().getInteger(R.integer.danmaku_scale_text_size));
        baseBind.dv.showFPS(true);
        baseBind.dv.enableDanmakuDrawingCache(true);

        mBtnDanmaku.setOnClickListener(v -> {
//            playingBeforeTryToSendDanmaku = player.isPlaying();
//            player.setPlayWhenReady(false);
//            danmakuSendFragment = new DanmakuSendFragment();
//            getSupportFragmentManager().beginTransaction().add(R.id.fl_pv_cover, danmakuSendFragment).commitAllowingStateLoss();
            ToastUtil.sendMsg(this, "没有实现");
        });
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
            baseBind.dv.hide();
        } else {
            if (fullscreen) {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                Objects.requireNonNull(getSupportActionBar()).hide();
            } else {
                params.height = OtherUtils.dp2px(this, 300);
                Objects.requireNonNull(getSupportActionBar()).show();
            }
            baseBind.pv.setUseController(true);
            if (mBtnDanmaku.getVisibility() == View.VISIBLE) {
                baseBind.dv.show();
            }
        }
        baseBind.rl.setLayoutParams(params);
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                baseBind.vp.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), 1));
                GlideUtil.loadUrlInto(this, biliView.getData().getPic(), baseBind.ivCover, false);
                baseBind.ivCover.setOnClickListener(v -> {
                    baseBind.ivCover.setImageDrawable(null);
                    baseBind.ivCover.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                });
                break;
            case 1:
                baseBind.pbLoading.setVisibility(View.VISIBLE);
                baseBind.dv.release();
                new Thread() {
                    @Override
                    public void run() {
                        final boolean[] playWhenReady = new boolean[1];
                        runOnUiThread(() -> playWhenReady[0] = player.getPlayWhenReady());
                        runOnUiThread(() -> player.setPlayWhenReady(false));
                        ResponseBody responseBody = null;
                        try {
                            responseBody = PBilibiliClient.Companion.getInstance().getPDanmakuAPI().list(aid, biliView.getData().getPages().get(page - 1).getCid());
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> ToastUtil.sendMsg(PlayActivity.this, "无法加载弹幕\n" + e.getMessage()));
                        }
                        if (responseBody != null) {
                            Pair<Map<Long, Integer>, BufferedInputStream> pair = DanmakuUtil.INSTANCE.toInputStream(responseBody.byteStream());
                            mParser = createParser(pair.getSecond());
                            runOnUiThread(() -> baseBind.dv.prepare(mParser, danmakuContext));
                        }
                        runOnUiThread(() -> {
                            player.setPlayWhenReady(playWhenReady[0]);
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            baseBind.pbLoading.setVisibility(View.INVISIBLE);
                        });
                    }
                }.start();
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
                } else if (videoPlayUrl.getData().getDurl() != null) {
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

                if (videoPlayUrl.getData().getDash() != null) {
                    mBtnQuality.setVisibility(View.VISIBLE);
                    videoQualityMap = new HashMap<>();
                    for (VideoPlayUrl.Data.Dash.Video video : videoPlayUrl.getData().getDash().getVideo()) {
                        videoQualityMap.put(video.getId(), video.getBaseUrl());
                    }
                    mBtnQuality.setOnClickListener(v -> {
                        PopupMenu popupMenu = new PopupMenu(this, mBtnQuality);
                        Menu menu = popupMenu.getMenu();
                        for (int i = 0; i < videoPlayUrl.getData().getAcceptDescription().size(); i++) {
                            menu.add(0, videoPlayUrl.getData().getAcceptQuality().get(i), i, videoPlayUrl.getData().getAcceptDescription().get(i));
                        }
                        popupMenu.setOnMenuItemClickListener(item -> {
                            String video = videoQualityMap.get(item.getItemId());
                            if (!videoDownloadInfo.videoUrl.equals(video) && video != null) {
                                long position = player.getCurrentPosition();
                                videoDownloadInfo.videoUrl = video;
                                setPlayerUrl(video, videoDownloadInfo.audioUrl);
                                mBtnQuality.setText(videoPlayUrl.getData().getAcceptDescription().get(item.getOrder()));
                                player.seekTo(position);
                            } else if (video == null) {
                                runOnUiThread(() -> ToastUtil.sendMsg(PlayActivity.this, R.string.not_vip));
                            }
                            return true;
                        });
                        popupMenu.show();

                    });
                    mBtnQuality.setText(videoPlayUrl.getData().getAcceptDescription().get(0));
                } else {
                    videoQualityMap = null;
                    mBtnQuality.setOnClickListener(null);
                    mBtnQuality.setVisibility(View.GONE);
                }
                if (page < biliView.getData().getPages().size()) {
                    mIbNext.setImageResource(R.drawable.ic_skip_next_write);
                    mIbNext.setOnClickListener(v -> {
                        if (introFragment != null && introFragment.getHandler() != null) {
                            Message message = new Message();
                            message.what = 2;
                            message.arg1 = page + 1;
                            introFragment.getHandler().sendMessage(message);
                        }
                    });
                } else {
                    mIbNext.setImageResource(R.drawable.ic_skip_next_gray);
                    mIbNext.setOnClickListener(null);
                }
                break;
            case 2:
                long timeOffset = player.getContentPosition();
                Danmaku danmaku = new Danmaku(Objects.requireNonNull(msg.getData().getString("message")));
                danmaku.textColor = 16777215;
                danmaku.isLive = false;
                danmaku.timeOffset = timeOffset;
                danmaku.duration = new Duration(5000);
                baseBind.dv.addDanmaku(danmaku);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            PBilibiliClient.Companion.getInstance().getPMainAPI().sendDanmaku(aid,
                                    biliView.getData().getPages().get(page - 1).getCid(),
                                    timeOffset,
                                    Objects.requireNonNull(msg.getData().getString("message")),
                                    1, 16777215);
                            runOnUiThread(() -> ToastUtil.sendMsg(PlayActivity.this, R.string.sended));
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> ToastUtil.sendMsg(PlayActivity.this, e.getMessage()));
                        }
                    }
                }.start();
            case 3:
                getSupportFragmentManager().beginTransaction().remove(danmakuSendFragment).commitAllowingStateLoss();
                player.setPlayWhenReady(playingBeforeTryToSendDanmaku);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        baseBind.pv.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (playingBeforeActivityPause = player.isPlaying()) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
        baseBind.dv.release();
    }

    @Override
    public void onBackPressed() {
        if (fullscreen) {
            mIbFullscreen.callOnClick();
        } else {
            super.onBackPressed();
        }
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
                if (introFragment == null) {
                    introFragment = IntroFragment.getInstance(biliView, aid, page);
                }
                return introFragment;
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

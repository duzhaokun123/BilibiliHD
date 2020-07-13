package com.duzhaokun123.bilibilihd.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutPlayerOverlayBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.utils.BiliDanmakuParser;
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil;
import com.duzhaokun123.bilibilihd.utils.Handler;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;

import kotlin.Pair;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import okhttp3.ResponseBody;

/**
 * TODO: 实现双击暂停
 */
public class BiliPlayerView extends PlayerView implements Handler.IHandlerMessageCallback {
    private static final int WHAT_DANMAKU_LOAD_EXCEPTION = 0;
    private static final int WHAT_DANMAKU_LOAD_SUCCESSFUL = 1;

    private final String TAG = this.getClass().getSimpleName();

    private LayoutPlayerOverlayBinding overlayBaseBind;
    private FrameLayout overlay;
    private ProgressBar pbExoBuffering;
    private ImageButton ibFullscreen, ibNext;
    private Button btnDanmakuSwitch, btnDanmaku, btnQuality;

    private DanmakuLoadListener danmakuLoadListener;
    private BiliPlayerViewPackageView.OnFullscreenClickListener onFullscreenClickListener;

    private Handler handler;
    private DanmakuContext danmakuContext;

    private boolean isFullscreen = false;

    public BiliPlayerView(Context context) {
        this(context, null);
    }

    public BiliPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BiliPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            return;
        }
        overlay = getOverlayFrameLayout();
        overlayBaseBind = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_player_overlay, overlay, true);
        handler = new Handler(this);
        danmakuContext = DanmakuContext.create();
        DanmakuUtil.INSTANCE.syncDanmakuSettings(danmakuContext, context);

        pbExoBuffering = findViewById(R.id.exo_buffering);
        ibFullscreen = findViewById(R.id.ib_fullscreen);
        ibNext = findViewById(R.id.ib_next);
        btnDanmakuSwitch = findViewById(R.id.btn_danmaku_switch);
        btnDanmaku = findViewById(R.id.btn_danmaku);
        btnQuality = findViewById(R.id.btn_quality);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) {
            return;
        }
        ibFullscreen.setOnClickListener(view -> {
            if (!isFullscreen) {
                isFullscreen = true;
                ibFullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
                ibNext.setVisibility(VISIBLE);
            } else {
                isFullscreen = false;
                ibFullscreen.setImageResource(R.drawable.ic_fullscreen);
                ibNext.setVisibility(GONE);
            }
            if (onFullscreenClickListener != null) {
                onFullscreenClickListener.onClick(isFullscreen);
            }
        });
        btnDanmakuSwitch.setOnClickListener(view -> {
            if (btnDanmaku.getVisibility() == VISIBLE) {
                btnDanmaku.setVisibility(INVISIBLE);
                overlayBaseBind.dv.hide();
            } else {
                btnDanmaku.setVisibility(VISIBLE);
                overlayBaseBind.dv.show();
            }
        });
        btnDanmaku.setOnClickListener(view -> TipUtil.showTip(getContext(), "没有实现"));
        overlayBaseBind.dv.enableDanmakuDrawingCache(true);
    }

    public void release() {
        handler.destroy();
        overlayBaseBind.dv.release();
    }

    public DanmakuLoadListener getDanmakuLoadListener() {
        return danmakuLoadListener;
    }

    public void setDanmakuLoadListener(DanmakuLoadListener danmakuLoadListener) {
        this.danmakuLoadListener = danmakuLoadListener;
    }

    public void loadDanmaku(long aid, long cid) {
        pbExoBuffering.setVisibility(VISIBLE);
        overlayBaseBind.dv.release();
        new Thread() {
            @Override
            public void run() {
                ResponseBody responseBody = null;
                try {
                    responseBody = PBilibiliClient.Companion.getInstance().getPDanmakuAPI().list(aid, cid);
                } catch (Exception e) {
                    e.printStackTrace();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("exception", e);
                    Message message = new Message();
                    message.what = WHAT_DANMAKU_LOAD_EXCEPTION;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                if (responseBody != null) {
                    Pair<Map<Long, Integer>, BufferedInputStream> pair = DanmakuUtil.INSTANCE.toInputStream(responseBody.byteStream());
                    overlayBaseBind.dv.prepare(createParser(pair.getSecond()), danmakuContext);
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(WHAT_DANMAKU_LOAD_SUCCESSFUL);
                }
            }
        }.start();
    }

    public void setPbExoBufferingVisibility(int visibility) {
        pbExoBuffering.setVisibility(visibility);
    }

    public void danmakuPause() {
        overlayBaseBind.dv.pause();
    }

    public void danmakuResume() {
        overlayBaseBind.dv.resume();
    }

    public void danmakuSeekTo(long ms) {
        overlayBaseBind.dv.seekTo(ms);
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case WHAT_DANMAKU_LOAD_EXCEPTION:
                Exception exception = (Exception) msg.getData().getSerializable("exception");
                if (danmakuLoadListener != null) {
                    danmakuLoadListener.onDanmakuLoadEnd(exception);
                }
                pbExoBuffering.setVisibility(INVISIBLE);
                break;
            case WHAT_DANMAKU_LOAD_SUCCESSFUL:
                if (danmakuLoadListener != null) {
                    danmakuLoadListener.onDanmakuLoadEnd(null);
                }
                if (getPlayer() != null) {
                    overlayBaseBind.dv.seekTo(getPlayer().getContentPosition());
                    if (!getPlayer().isPlaying()) {
                        overlayBaseBind.dv.pause();
                    }
                }
                pbExoBuffering.setVisibility(INVISIBLE);
                break;
        }
    }

    public static BaseDanmakuParser createParser(InputStream stream) {

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

    public BiliPlayerViewPackageView.OnFullscreenClickListener getOnFullscreenClickListener() {
        return onFullscreenClickListener;
    }

    public void setOnFullscreenClickListener(BiliPlayerViewPackageView.OnFullscreenClickListener onFullscreenClickListener) {
        this.onFullscreenClickListener = onFullscreenClickListener;
    }

    public void setOnIbNextClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            ibNext.setImageResource(R.drawable.ic_skip_next_write);
        } else {
            ibNext.setImageResource(R.drawable.ic_skip_next_gray);
        }
        ibNext.setOnClickListener(onClickListener);
    }

    public Button getBtnQuality() {
        return btnQuality;
    }

    public void clickIbFullscreen() {
        ibFullscreen.callOnClick();
    }

    public interface DanmakuLoadListener {
        void onDanmakuLoadEnd(@Nullable Exception e);
    }
}

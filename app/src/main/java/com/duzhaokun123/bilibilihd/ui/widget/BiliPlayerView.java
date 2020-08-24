package com.duzhaokun123.bilibilihd.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutPlayerOverlayBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.MyBilibiliClient;
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.DanmakuAPI;
import com.duzhaokun123.bilibilihd.mybilibiliapi.shot.ShotAPi;
import com.duzhaokun123.bilibilihd.mybilibiliapi.shot.model.VideoShot;
import com.duzhaokun123.bilibilihd.proto.BiliDanmaku;
import com.duzhaokun123.bilibilihd.utils.DanmakuUtil;
import com.duzhaokun123.bilibilihd.utils.DateTimeFormatUtil;
import com.duzhaokun123.bilibilihd.utils.Handler;
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.ProtobufBiliDanmakuParser;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;

import java.util.Objects;

import master.flame.danmaku.ui.widget.DanmakuView;

public class BiliPlayerView extends PlayerView implements Handler.IHandlerMessageCallback {
    private static final int WHAT_DANMAKU_LOAD_EXCEPTION = 0;
    private static final int WHAT_DANMAKU_LOAD_SUCCESSFUL = 1;
    private static final int WHAT_LOAD_SHOT = 2;

    private final String TAG = this.getClass().getSimpleName();
    private int defaultIbNextWidth;

    private LayoutPlayerOverlayBinding overlayBaseBind;
    private FrameLayout overlay;
    private ProgressBar pbExoBuffering;
    private ImageButton ibFullscreen, ibNext;
    private Button btnDanmakuSwitch, btnDanmaku, btnQuality;
    private LinearLayout llTime;
    private TimeBar exoTimeBar;

    private DanmakuLoadListener danmakuLoadListener;
    private BiliPlayerViewPackageView.OnFullscreenClickListener onFullscreenClickListener;

    private Handler handler;

    @Nullable
    private VideoShot videoShot;

    private long aid;
    private long cid;
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
        defaultIbNextWidth = OtherUtils.dp2px(50);
        overlay = getOverlayFrameLayout();
        overlayBaseBind = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_player_overlay, overlay, true);
        handler = new Handler(this);

        pbExoBuffering = findViewById(R.id.exo_buffering);
        ibFullscreen = findViewById(R.id.ib_fullscreen);
        ibNext = findViewById(R.id.ib_next);
        btnDanmakuSwitch = findViewById(R.id.btn_danmaku_switch);
        btnDanmaku = findViewById(R.id.btn_danmaku);
        btnQuality = findViewById(R.id.btn_quality);
        llTime = findViewById(R.id.ll_time);
        exoTimeBar = findViewById(R.id.exo_progress);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) {
            return;
        }
        ibFullscreen.setOnClickListener(view -> {
            ViewGroup.LayoutParams ibNextParams = ibNext.getLayoutParams();
            int ibNextNewWidth;
            if (!isFullscreen) {
                isFullscreen = true;
                ibFullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
                ibNext.setVisibility(VISIBLE);
                ibNextNewWidth = defaultIbNextWidth;
            } else {
                isFullscreen = false;
                ibFullscreen.setImageResource(R.drawable.ic_fullscreen);
                ibNextNewWidth = 0;
            }
            ValueAnimator valueAnimator = ValueAnimator.ofInt(ibNextParams.width, ibNextNewWidth);
            valueAnimator.addUpdateListener(animation -> {
                ibNextParams.width = (int) animation.getAnimatedValue();
                ibNext.setLayoutParams(ibNextParams);
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (ibNextParams.width == 0) {
                        ibNext.setVisibility(GONE);
                    }
                }
            });
            valueAnimator.start();
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
        llTime.setOnClickListener(v -> {
            boolean isPlayingBefore = Objects.requireNonNull(getPlayer()).getPlayWhenReady();
            getPlayer().setPlayWhenReady(false);
            EditText editText = new EditText(getContext());
            editText.setText(DateTimeFormatUtil.getStringForTime(getPlayer().getCurrentPosition()));
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.jump)
                    .setPositiveButton(R.string.jump, (dialog, which) -> {
                                try {
                                    long timeMs = DateTimeFormatUtil.getTimeSForString(editText.getText().toString()) * 1000;
                                    getPlayer().seekTo(timeMs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    TipUtil.showToast(e.getMessage());
                                }
                            }
                    )
                    .setNegativeButton(android.R.string.cancel, null)
                    .setOnDismissListener(dialog -> {
                        if (isPlayingBefore) {
                            getPlayer().setPlayWhenReady(true);
                        }
                    })
                    .setView(editText)
                    .create().show();
        });
        exoTimeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(@NonNull TimeBar timeBar, long position) {
                overlayBaseBind.ivPreview.setVisibility(VISIBLE);
            }

            @Override
            public void onScrubMove(@NonNull TimeBar timeBar, long position) {
                if (videoShot == null) {
                    return;
                }
                int timeS = (int) (position / 1000);
                int index = -1;
                int gap = -1;
                for (int i = 0; i < videoShot.getData().getIndex().size(); i++) {
                    int newGap = Math.abs(videoShot.getData().getIndex().get(i) - timeS);
                    if (gap == -1 || gap >= newGap) {
                        gap = newGap;
                    } else if (i != 1) {
                        index = i - 1;
                        break;
                    }
                    if (i == videoShot.getData().getIndex().size()) {
                        index = i;
                    }
                }
                ImageViewUtil.INSTANCE.setPreview(overlayBaseBind.ivPreview, videoShot, index);
            }

            @Override
            public void onScrubStop(@NonNull TimeBar timeBar, long position, boolean canceled) {
                overlayBaseBind.ivPreview.setVisibility(GONE);
            }
        });
    }

    public void release() {
        handler.destroy();
        overlayBaseBind.dv.release();
    }

    public void setDanmakuLoadListener(DanmakuLoadListener danmakuLoadListener) {
        this.danmakuLoadListener = danmakuLoadListener;
    }

    public void loadDanmakuByAidCid(long aid, long cid, int durationS) {
        pbExoBuffering.setVisibility(VISIBLE);
        overlayBaseBind.dv.release();
        new Thread() {
            @Override
            public void run() {
                BiliDanmaku.DmSegMobileReply[] dmSegMobileReplies = new BiliDanmaku.DmSegMobileReply[durationS / 360 + 1];
                try {
                    for (int i = 0; i < dmSegMobileReplies.length; i++) {
                        dmSegMobileReplies[i] = DanmakuAPI.INSTANCE.getBiliDanmaku(aid, cid, 1, i + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("exception", e);
                    Message message = new Message();
                    message.what = WHAT_DANMAKU_LOAD_EXCEPTION;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                overlayBaseBind.dv.prepare(new ProtobufBiliDanmakuParser(dmSegMobileReplies), DanmakuUtil.INSTANCE.getDanmakuContext());
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(WHAT_DANMAKU_LOAD_SUCCESSFUL);
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

    public void danmakuSwitch() {
        btnDanmakuSwitch.callOnClick();
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
                    if (!getPlayer().getPlayWhenReady()) {
                        overlayBaseBind.dv.pause();
                    }
                }
                pbExoBuffering.setVisibility(INVISIBLE);
                break;
            case WHAT_LOAD_SHOT:
                new Thread(() -> ShotAPi.INSTANCE.getShot(aid, cid, new MyBilibiliClient.ICallback<VideoShot>() {
                    @Override
                    public void onException(@NonNull Exception e) {
                        e.printStackTrace();
                        Application.runOnUiThread(() -> TipUtil.showToast(e.getMessage()));
                    }

                    @Override
                    public void onSuccess(@NonNull VideoShot videoShot) {
                        BiliPlayerView.this.videoShot = videoShot;
                    }
                })).start();
                break;
        }
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

    public void loadShot(long aid, long cid) {
        videoShot = null;
        this.aid = aid;
        this.cid = cid;
        handler.sendEmptyMessage(WHAT_LOAD_SHOT);
    }

    public void clickIbFullscreen() {
        ibFullscreen.callOnClick();
    }

    public DanmakuView getDanmakuView() {
        return overlayBaseBind.dv;
    }

    public interface DanmakuLoadListener {
        void onDanmakuLoadEnd(@Nullable Exception e);
    }
}

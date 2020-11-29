package com.duzhaokun123.bilibilihd.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutPlayerOverlayBinding;
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.DanmakuAPI;
import com.duzhaokun123.bilibilihd.mybilibiliapi.danamku.parser.ProtobufBiliDanmakuParser;
import com.duzhaokun123.bilibilihd.proto.BiliDanmaku;
import com.duzhaokun123.bilibilihd.utils.DateTimeFormatUtil;
import com.duzhaokun123.bilibilihd.utils.Handler;
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil;
import com.duzhaokun123.bilibilihd.utils.OtherUtils;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.duzhaokun123.danmakuview.ui.DanmakuView;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.flexbox.FlexboxLayout;
import com.hiczp.bilibili.api.web.model.VideoShot;

import java.util.Objects;

public class BiliPlayerView extends PlayerView implements Handler.IHandlerMessageCallback {
    private static final int WHAT_DANMAKU_LOAD_EXCEPTION = 0;
    private static final int WHAT_DANMAKU_LOAD_SUCCESSFUL = 1;
    private static final int WHAT_LOAD_SHOT = 2;

    private int defaultIbNextWidth;
    private boolean isLive = false;

    private LayoutPlayerOverlayBinding overlayBaseBind;
    private FrameLayout overlay;
    private ProgressBar pbExoBuffering;
    private ImageButton ibFullscreen, ibNext;
    private Button btnDanmakuSwitch, btnDanmaku, btnQuality, btnLine, btnSpeed;
    private LinearLayout llTime;
    private DefaultTimeBar exoProgress;
    private TextView tvLive;
    private FlexboxLayout fl;

    private DanmakuLoadListener danmakuLoadListener;
    private BiliPlayerViewWrapperView.OnFullscreenClickListener onFullscreenClickListener;

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

        pbExoBuffering = overlayBaseBind.pb;
        ibFullscreen = findViewById(R.id.ib_fullscreen);
        ibNext = findViewById(R.id.ib_next);
        btnDanmakuSwitch = findViewById(R.id.btn_danmaku_switch);
        btnDanmaku = findViewById(R.id.btn_danmaku);
        btnQuality = findViewById(R.id.btn_quality);
        btnLine = findViewById(R.id.btn_line);
        btnSpeed = findViewById(R.id.btn_speed);
        llTime = findViewById(R.id.ll_time);
        exoProgress = findViewById(R.id.exo_progress);
        tvLive = findViewById(R.id.tv_live);
        fl = findViewById(R.id.fl);
    }

    @SuppressLint("SetTextI18n")
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
            if (!isLive) {
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
            }
            if (onFullscreenClickListener != null) {
                onFullscreenClickListener.onClick(isFullscreen);
            }
        });
        btnDanmakuSwitch.setOnClickListener(view -> {
            if (btnDanmaku.getVisibility() == VISIBLE) {
                btnDanmaku.setVisibility(INVISIBLE);
                overlayBaseBind.dv.setVisibility(INVISIBLE);
            } else {
                btnDanmaku.setVisibility(VISIBLE);
                overlayBaseBind.dv.setVisibility(VISIBLE);
            }
        });
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
        exoProgress.addListener(new TimeBar.OnScrubListener() {
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
        fl.setOnClickListener(v -> {
        });
        btnSpeed.setOnClickListener(v -> {
            final float[] speed = {Objects.requireNonNull(getPlayer()).getPlaybackParameters().speed};
            PopupWindow popupWindow = new PopupWindow(OtherUtils.dp2px(300), OtherUtils.dp2px(50));
            @SuppressLint("InflateParams") View popSpeed = LayoutInflater.from(getContext()).inflate(R.layout.pop_speed, null, false);
            popupWindow.setContentView(popSpeed);
            popupWindow.setOutsideTouchable(true);
            SeekBar sbSpeed = popSpeed.findViewById(R.id.sb_speed);
            TextView tvSpeed = popSpeed.findViewById(R.id.tv_speed);
            Button btnP01 = popSpeed.findViewById(R.id.btn_p01);
            Button btnM01 = popSpeed.findViewById(R.id.btn_m01);
            sbSpeed.setProgress((int) (100 * speed[0]));
            tvSpeed.setText("" + speed[0] + "x");
            sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    speed[0] = progress / 100F;
                    tvSpeed.setText("" + speed[0] + "x");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            btnP01.setOnClickListener(v1 -> sbSpeed.setProgress(sbSpeed.getProgress() + 10, true));
            btnM01.setOnClickListener(v1 -> sbSpeed.setProgress(sbSpeed.getProgress() - 10, true));
            popupWindow.setOnDismissListener(() -> {
                if (speed[0] == 0F) {
                    TipUtil.showToast("should not be 0.0");
                    return;
                }
                btnSpeed.setText("" + speed[0] + "x");
                PlaybackParameters param = new PlaybackParameters(speed[0]);
                Objects.requireNonNull(getPlayer()).setPlaybackParameters(param);
                overlayBaseBind.dv.setSpeed(speed[0]);
            });
            popupWindow.showAsDropDown(btnSpeed);

        });
    }

    public void release() {
        handler.destroy();
        overlayBaseBind.dv.destroy();
    }

    public void setDanmakuLoadListener(DanmakuLoadListener danmakuLoadListener) {
        this.danmakuLoadListener = danmakuLoadListener;
    }

    public void loadDanmakuByAidCid(long aid, long cid, int durationS) {
        pbExoBuffering.setVisibility(VISIBLE);
        new Thread(() -> {
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
            overlayBaseBind.dv.parse(new ProtobufBiliDanmakuParser(dmSegMobileReplies),
                    danmakus -> {
                        handler.sendEmptyMessageDelayed(WHAT_DANMAKU_LOAD_SUCCESSFUL, 17);
                        return null;
                    }
            );

        }).start();
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
                    if (getPlayer().isPlaying()) {
                        overlayBaseBind.dv.resume();
                    }
                }
                pbExoBuffering.setVisibility(INVISIBLE);
                break;
            case WHAT_LOAD_SHOT:
                new Thread(() -> {
                    try {
                        this.videoShot = Application.getPBilibiliClient().getPWebAPI().videoshot(aid, cid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Application.runOnUiThread(() -> TipUtil.showTip(getContext(), e.getMessage()));
                    }
                }).start();
                break;
        }
    }

    public BiliPlayerViewWrapperView.OnFullscreenClickListener getOnFullscreenClickListener() {
        return onFullscreenClickListener;
    }

    public void setOnFullscreenClickListener(BiliPlayerViewWrapperView.OnFullscreenClickListener onFullscreenClickListener) {
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

    public Button getBtnLine() {
        return btnLine;
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

    public void danmakuHide() {
        btnDanmaku.setVisibility(INVISIBLE);
        overlayBaseBind.dv.setVisibility(INVISIBLE);
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
        if (isLive) {
            llTime.setVisibility(GONE);
            exoProgress.setVisibility(GONE);
            ibNext.setVisibility(GONE);
            tvLive.setVisibility(VISIBLE);
            btnSpeed.setVisibility(GONE);
        } else {
            llTime.setVisibility(VISIBLE);
            exoProgress.setVisibility(VISIBLE);
            tvLive.setVisibility(GONE);
            btnSpeed.setVisibility(VISIBLE);
        }
    }

    public void setDanmakuSendClickListener(OnClickListener listener) {
        btnDanmaku.setOnClickListener(listener);
    }

    public interface DanmakuLoadListener {
        void onDanmakuLoadEnd(@Nullable Exception e);
    }
}

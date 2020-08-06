package com.duzhaokun123.bilibilihd.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;

import com.duzhaokun123.bilibilihd.Application;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutBiliPlayerViewPackageViewBinding;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class BiliPlayerViewPackageView extends FrameLayout {
    private LayoutBiliPlayerViewPackageViewBinding baseBind;

    private OnPlayingStatusChangeListener onPlayingStatusChangeListener;
    private OnPlayerErrorListener onPlayerErrorListener;
    private VideoUrlAdapter videoUrlAdapter;

    private SimpleExoPlayer player;

    private int qualityId = 0;

    public BiliPlayerViewPackageView(@NonNull Context context) {
        this(context, null);
    }

    public BiliPlayerViewPackageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BiliPlayerViewPackageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            LayoutInflater.from(context).inflate(R.layout.layout_bili_player_view_package_view, this);
            return;
        }

        baseBind = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_bili_player_view_package_view, this, true);
        player = new SimpleExoPlayer.Builder(context).build();
        baseBind.bpv.setPlayer(player);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) {
            return;
        }
        player.addListener(new Player.EventListener() {
            @Override
            public void onSeekProcessed() {
                baseBind.bpv.danmakuSeekTo(player.getContentPosition());
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    baseBind.bpv.danmakuResume();
                    baseBind.bpv.setPbExoBufferingVisibility(View.INVISIBLE);
                    if (onPlayingStatusChangeListener != null) {
                        onPlayingStatusChangeListener.onPlayingStatusChange(PlayingStatus.PLAYING);
                    }
                } else {
                    baseBind.bpv.danmakuPause();
                    if (onPlayingStatusChangeListener != null) {
                        onPlayingStatusChangeListener.onPlayingStatusChange(PlayingStatus.PAUSED);
                    }
                    long contentPosition = player.getContentPosition();
                    long contentDuration = player.getContentDuration();
                    long contentBufferedPosition = player.getContentBufferedPosition();
                    if (contentBufferedPosition - contentPosition <= 1000 && contentDuration - contentPosition > 100) {
                        baseBind.bpv.setPbExoBufferingVisibility(View.VISIBLE);
                        if (onPlayingStatusChangeListener != null) {
                            onPlayingStatusChangeListener.onPlayingStatusChange(PlayingStatus.LOADING);
                        }
                    } else {
                        baseBind.bpv.showController();
                        if (onPlayingStatusChangeListener != null) {
                            onPlayingStatusChangeListener.onPlayingStatusChange(PlayingStatus.ENDED);
                        }
                    }
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (onPlayingStatusChangeListener != null) {
                    onPlayingStatusChangeListener.onPlayingStatusChange(PlayingStatus.ERROR);
                }
                if (onPlayerErrorListener != null) {
                    onPlayerErrorListener.onPlayerError(error);
                }
            }
        });
        baseBind.ivCover.setOnClickListener(view -> {
            setCover(null);
            resume();
        });
        baseBind.bpv.setDanmakuLoadListener(e -> {
            if (e != null) {
                TipUtil.showTip(getContext(), e.getMessage());
            }
        });
        baseBind.bpv.getBtnQuality().setOnClickListener(view -> {
            if (videoUrlAdapter == null) {
                return;
            }

            PopupMenu popupMenu = new PopupMenu(getContext(), baseBind.bpv.getBtnQuality());
            Menu menu = popupMenu.getMenu();
            for (int i = 0; i < videoUrlAdapter.getCount(); i++) {
                menu.add(0, i, i, videoUrlAdapter.getName(i));
            }
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                changeQuality(menuItem.getItemId());
                return true;
            });
            popupMenu.show();
        });
        baseBind.bpv.setOnClickListener(new OnClickListener() {
            private long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                long thisClickTime = System.currentTimeMillis();
                if (thisClickTime - lastClickTime < 400) {
                    if (player.getPlayWhenReady()) {
                        pause();
                    } else {
                        resume();
                    }
                }
                lastClickTime = thisClickTime;
            }
        });
    }

    public void release() {
        player.release();
        baseBind.bpv.release();
    }

    public void setCover(String url) {
        if (url != null) {
            baseBind.ivCover.setVisibility(VISIBLE);
            GlideUtil.loadUrlInto(getContext(), url, baseBind.ivCover, false);
        } else {
            baseBind.ivCover.setVisibility(GONE);
            baseBind.ivCover.setImageDrawable(null);
        }
    }

    public void pause() {
        player.setPlayWhenReady(false);
        baseBind.bpv.danmakuPause();
    }

    public void resume() {
        player.setPlayWhenReady(true);
        baseBind.bpv.danmakuResume();
    }

    public OnFullscreenClickListener getOnFullscreenClickListener() {
        return baseBind.bpv.getOnFullscreenClickListener();
    }

    public void setOnFullscreenClickListener(OnFullscreenClickListener onFullscreenClickListener) {
        baseBind.bpv.setOnFullscreenClickListener(onFullscreenClickListener);
    }

    public OnPlayingStatusChangeListener getOnPlayingStatusChangeListener() {
        return onPlayingStatusChangeListener;
    }

    public void setOnPlayingStatusChangeListener(OnPlayingStatusChangeListener onPlayingStatusChangeListener) {
        this.onPlayingStatusChangeListener = onPlayingStatusChangeListener;
    }

    public void setControllerVisibilityListener(PlayerControlView.VisibilityListener visibilityListener) {
        baseBind.bpv.setControllerVisibilityListener(visibilityListener);
    }

    public BiliPlayerView getBiliPlayerView() {
        return baseBind.bpv;
    }

    public void loadDanmaku(long aid, long cid, int durationS) {
        baseBind.bpv.loadDanmaku(aid, cid, durationS);
    }

    public void setOnPlayerErrorListener(OnPlayerErrorListener onPlayerErrorListener) {
        this.onPlayerErrorListener = onPlayerErrorListener;
    }

    public VideoUrlAdapter getVideoUrlAdapter() {
        return videoUrlAdapter;
    }

    public void setVideoUrlAdapter(VideoUrlAdapter videoUrlAdapter) {
        this.videoUrlAdapter = videoUrlAdapter;
        player.seekTo(0);
        changeQuality(videoUrlAdapter.getDefaultIndex());
    }

    private void changeQuality(int index) {
        if (videoUrlAdapter == null) {
            baseBind.bpv.getBtnQuality().setVisibility(GONE);
        } else {
            int qualityIdTemp = videoUrlAdapter.getId(index);
            baseBind.bpv.getBtnQuality().setVisibility(VISIBLE);
            Pair<String, String> url = videoUrlAdapter.getUrl(qualityIdTemp);
            String video = url.first;
            String audio = url.second;
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(Application.getInstance(), Util.getUserAgent(Application.getInstance(), Application.getInstance().getString(R.string.app_name)));
            MediaSource mediaSource = null;
            if (video == null) {
                videoUrlAdapter.onVideoIsNull();
            } else if (audio != null) {
                MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(video));
                MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(audio));
                mediaSource = new MergingMediaSource(videoSource, audioSource);
            } else {
                mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(video));
            }
            if (mediaSource != null) {
                long playedTime = player.getCurrentPosition();
                player.prepare(mediaSource);
                player.seekTo(playedTime);
                baseBind.bpv.getBtnQuality().setText(videoUrlAdapter.getName(index));
                qualityId = qualityIdTemp;
            }
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public boolean isPlaying() {
        return player.getPlayWhenReady();
    }

    public void clickIbFullscreen() {
        baseBind.bpv.clickIbFullscreen();
    }

    public int getQualityId() {
        return qualityId;
    }

    public void setQualityId(int qualityId) {
        this.qualityId = qualityId;
    }

    public interface OnFullscreenClickListener {
        void onClick(boolean isFullscreen);
    }

    public interface OnPlayerErrorListener {
        void onPlayerError(ExoPlaybackException error);
    }

    public interface OnPlayingStatusChangeListener {
        void onPlayingStatusChange(@NonNull PlayingStatus playingStatus);
    }

    public interface VideoUrlAdapter {
        Pair<String, String> getUrl(int id);

        int getCount();

        String getName(int index);

        int getDefaultIndex();

        void onVideoIsNull();

        int getId(int index);
    }

    public enum PlayingStatus {
        PLAYING,
        PAUSED,
        LOADING,
        ENDED,
        ERROR
    }
}

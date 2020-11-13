package com.duzhaokun123.bilibilihd.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutBiliPlayerViewWapperViewBinding;
import com.duzhaokun123.bilibilihd.utils.GlideUtil;
import com.duzhaokun123.bilibilihd.utils.TipUtil;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;

import java.util.List;

import kotlin.Pair;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

public class BiliPlayerViewWrapperView extends FrameLayout {
    private LayoutBiliPlayerViewWapperViewBinding baseBind;

    private OnPlayingStatusChangeListener onPlayingStatusChangeListener;
    private OnPlayerErrorListener onPlayerErrorListener;
    private VideoMediaSourceAdapter videoMediaSourceAdapter;

    private SimpleExoPlayer player;

    private int qualityId = 0;

    public BiliPlayerViewWrapperView(@NonNull Context context) {
        this(context, null);
    }

    public BiliPlayerViewWrapperView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BiliPlayerViewWrapperView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            LayoutInflater.from(context).inflate(R.layout.layout_bili_player_view_wapper_view, this);
            return;
        }

        baseBind = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_bili_player_view_wapper_view, this, true);
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
            public void onPlayerError(@NonNull ExoPlaybackException error) {
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
            if (videoMediaSourceAdapter == null) {
                return;
            }

            PopupMenu popupMenu = new PopupMenu(getContext(), baseBind.bpv.getBtnQuality());
            Menu menu = popupMenu.getMenu();
            for (int i = 0; i < videoMediaSourceAdapter.getCount(); i++) {
                String name = videoMediaSourceAdapter.getName(i);
                menu.add(0, i, i, name != null ? name : String.valueOf(i));
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

    public void loadDanmakuByAidCid(long aid, long cid, int durationS) {
        baseBind.bpv.loadDanmakuByAidCid(aid, cid, durationS);
    }

    public void loadDanmakuByBiliDanmakuParser(BaseDanmakuParser danmakuParser) {
        baseBind.bpv.loadDanmakuByBiliDanmakuParser(danmakuParser);
    }

    public void setOnPlayerErrorListener(OnPlayerErrorListener onPlayerErrorListener) {
        this.onPlayerErrorListener = onPlayerErrorListener;
    }

    public VideoMediaSourceAdapter getVideoMediaSourceAdapter() {
        return videoMediaSourceAdapter;
    }

    public void setVideoMediaSourceAdapter(VideoMediaSourceAdapter videoMediaSourceAdapter) {
        this.videoMediaSourceAdapter = videoMediaSourceAdapter;
        player.seekTo(0);
        changeQuality(videoMediaSourceAdapter.getDefaultIndex());
    }

    private void changeQuality(int index) {
        if (videoMediaSourceAdapter == null) {
            baseBind.bpv.getBtnQuality().setVisibility(GONE);
        } else {
            int count = videoMediaSourceAdapter.getCount();
            if (count > index) {
                int qualityIdTemp = videoMediaSourceAdapter.getId(index);
                baseBind.bpv.getBtnQuality().setVisibility(VISIBLE);
                List<Pair<String, MediaSource>> mediaSources = videoMediaSourceAdapter.getMediaSources(qualityIdTemp);
                if (mediaSources != null) {
                    updateMediaSource(mediaSources.get(0).getSecond());
                    baseBind.bpv.getBtnQuality().setText(videoMediaSourceAdapter.getName(index));
                    baseBind.bpv.getBtnLine().setText(mediaSources.get(0).getFirst());
                    qualityId = qualityIdTemp;
                    baseBind.bpv.getBtnLine().setOnClickListener(
                            new OnClickListener() {
                                private int order = 0;

                                @Override
                                public void onClick(View v) {
                                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                                    for (int i = 0; i < mediaSources.size(); i++) {
                                        popupMenu.getMenu().add(0, i, i, mediaSources.get(i).getFirst());
                                    }
                                    popupMenu.setOnMenuItemClickListener(item -> {
                                                if (order != item.getOrder()) {
                                                    order = item.getOrder();
                                                    updateMediaSource(mediaSources.get(order).getSecond());
                                                    baseBind.bpv.getBtnLine().setText(mediaSources.get(order).getFirst());

                                                }
                                                return true;
                                            }
                                    );
                                    popupMenu.show();
                                }


                            }
                    );
                }
            }
        }
    }


    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public boolean isPlaying() {
        return player.isPlaying();
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

    public void syncDanmakuProgress() {
        baseBind.bpv.getDanmakuView().seekTo(player.getContentPosition());
    }

    public void setLive(boolean isLive) {
        baseBind.bpv.setLive(isLive);
    }

    public void setOnDanmakuSendClickListener(OnClickListener listener) {
        baseBind.bpv.setDanmakuSendClickListener(listener);
    }

    private void updateMediaSource(MediaSource mediaSource) {
        long playedTime = player.getCurrentPosition();
        player.prepare(mediaSource);
        player.seekTo(playedTime);
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

    public interface VideoMediaSourceAdapter {
        @Nullable
        List<Pair<String, MediaSource>> getMediaSources(int id);

        int getCount();

        @Nullable
        String getName(int index);

        int getDefaultIndex();

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

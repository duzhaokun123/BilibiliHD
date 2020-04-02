package com.duzhaokun123.bilibilihd.ui.download;

import android.annotation.SuppressLint;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding;
import com.duzhaokun123.bilibilihd.services.VideoDownloadService;
import com.duzhaokun123.bilibilihd.ui.widget.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;

import java.util.Map;

public class DownloadingFragment extends BaseFragment<LayoutXrecyclerviewOnlyBinding> {
    private Map<Integer, VideoDownloadService.VideoTaskHolder> videoTaskHolderMap;
    private Integer[] videoTaskHolderIds;

    @Override
    protected int initConfig() {
        return NEED_HANDLER;
    }

    @Override
    protected int initLayout() {
        return R.layout.layout_xrecyclerview_only;
    }

    @Override
    protected void initView() {
        baseBind.xrv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        baseBind.xrv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VideoDownloadHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_video_download, parent, false));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                VideoDownloadService.VideoTaskHolder videoTaskHolder = videoTaskHolderMap.get(videoTaskHolderIds[position]);
                if (videoTaskHolder == null) {
                    return;
                }
                ((VideoDownloadHolder) holder).mTvId.setText("id:" + videoTaskHolder.id);
                ((VideoDownloadHolder) holder).mtvTitle.setText(videoTaskHolder.title);
                DownloadTask[] downloadTasks = videoTaskHolder.downloadContext.getTasks();
                ((VideoDownloadHolder) holder).mPbTotal.setMax((int) videoTaskHolder.getTotalLength());
                ((VideoDownloadHolder) holder).mPbTotal.setProgress((int) videoTaskHolder.getTotalCurrentOffset(), true);
                if (((VideoDownloadHolder) holder).mPbTotal.getMax() != 0) {
                    ((VideoDownloadHolder) holder).mPbTotal.setIndeterminate(false);
                }
                ((VideoDownloadHolder) holder).mTvTotalInfo.setText(videoTaskHolder.status.name());
                for (DownloadTask downloadTask : downloadTasks) {
                    BreakpointInfo info = downloadTask.getInfo();
                    if (info == null) {
                        break;
                    }
                    if ("video".equals(downloadTask.getTag())) {
                        ((VideoDownloadHolder) holder).mPbVideo.setMax((int) info.getTotalLength());
                        ((VideoDownloadHolder) holder).mPbVideo.setProgress((int) info.getTotalOffset(), true);
                        if (((VideoDownloadHolder) holder).mPbVideo.getMax() != 0) {
                            ((VideoDownloadHolder) holder).mPbVideo.setIndeterminate(false);
                        }
                        if (videoTaskHolder.videoEndCause != null) {
                            ((VideoDownloadHolder) holder).mTvVideoSpeed.setText(videoTaskHolder.videoEndCause.name());
                        }
                    } else if ("audio".equals(downloadTask.getTag())) {
                        ((VideoDownloadHolder) holder).mPbAudio.setMax((int) info.getTotalLength());
                        ((VideoDownloadHolder) holder).mPbAudio.setProgress((int) info.getTotalOffset(), true);
                        if (((VideoDownloadHolder) holder).mPbAudio.getMax() != 0) {
                            ((VideoDownloadHolder) holder).mPbAudio.setIndeterminate(false);
                        }
                        if (videoTaskHolder.audioEndCause != null) {
                            ((VideoDownloadHolder) holder).mTvAudioSpeed.setText(videoTaskHolder.audioEndCause.name());
                        }
                    } else if ("danmaku".equals(downloadTask.getTag())) {
                        ((VideoDownloadHolder) holder).mPbDanmaku.setMax((int) info.getTotalLength());
                        ((VideoDownloadHolder) holder).mPbDanmaku.setProgress((int) info.getTotalOffset(), true);
                        if (((VideoDownloadHolder) holder).mPbDanmaku.getMax() != 0) {
                            ((VideoDownloadHolder) holder).mPbDanmaku.setIndeterminate(false);
                        }
                        if (videoTaskHolder.danmakuEndCause != null) {
                            ((VideoDownloadHolder) holder).mTvDanmakuSpeed.setText(videoTaskHolder.danmakuEndCause.name());
                        }
                    }
                }
            }

            @Override
            public int getItemCount() {
                if (videoTaskHolderIds == null) {
                    return 0;
                } else {
                    return videoTaskHolderIds.length;
                }
            }

            class VideoDownloadHolder extends RecyclerView.ViewHolder {
                private ProgressBar mPbTotal, mPbVideo, mPbAudio, mPbDanmaku;
                private TextView mtvTitle, mTvTotalInfo, mTvVideoSpeed, mTvAudioSpeed, mTvDanmakuSpeed, mTvId;

                VideoDownloadHolder(@NonNull View itemView) {
                    super(itemView);
                    mPbTotal = itemView.findViewById(R.id.pb_total);
                    mPbVideo = itemView.findViewById(R.id.pb_video);
                    mPbAudio = itemView.findViewById(R.id.pb_audio);
                    mPbDanmaku = itemView.findViewById(R.id.pb_danmaku);
                    mtvTitle = itemView.findViewById(R.id.tv_title);
                    mTvTotalInfo = itemView.findViewById(R.id.tv_total_info);
                    mTvVideoSpeed = itemView.findViewById(R.id.tv_video_speed);
                    mTvAudioSpeed = itemView.findViewById(R.id.tv_audio_speed);
                    mTvDanmakuSpeed = itemView.findViewById(R.id.tv_danmaku_speed);
                    mTvId = itemView.findViewById(R.id.tv_id);
                }
            }
        });
        baseBind.xrv.setLoadingMoreEnabled(false);
        baseBind.xrv.setPullRefreshEnabled(false);
    }

    @Override
    protected void initData() {

        new Thread() {
            @Override
            public void run() {
                do {
                    if (handler != null) {
                        handler.sendEmptyMessage(0);
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (handler != null);
            }
        }.start();
    }

    @Override
    public void handlerCallback(@NonNull Message msg) {
        videoTaskHolderMap = VideoDownloadService.getVideoTaskHolderMap();
        if (videoTaskHolderMap != null) {
            videoTaskHolderIds = videoTaskHolderMap.keySet().toArray(new Integer[0]);
            XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, 0, videoTaskHolderIds.length);
        } else {
            videoTaskHolderIds = null;
        }
    }
}

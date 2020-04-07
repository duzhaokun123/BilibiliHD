package com.duzhaokun123.bilibilihd.ui.download;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.LayoutXrecyclerviewOnlyBinding;
import com.duzhaokun123.bilibilihd.services.VideoDownloadService;
import com.duzhaokun123.bilibilihd.ui.widget.BaseFragment;
import com.duzhaokun123.bilibilihd.utils.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;

import java.util.List;
import java.util.Map;

public class DownloadingFragment extends BaseFragment<LayoutXrecyclerviewOnlyBinding> {
    private Map<Integer, VideoDownloadService.VideoTaskHolder> videoTaskHolderMap;
    private Integer[] videoTaskHolderIds;

    @Override
    protected int initConfig() {
        return 0;
    }

    @Override
    protected int initLayout() {
        return R.layout.layout_xrecyclerview_only;
    }

    @Override
    protected void initView() {
        baseBind.xrv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.divider_height));
            }
        });
        baseBind.xrv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        baseBind.xrv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VideoDownloadHolder(LayoutInflater.from(getContext()).inflate(R.layout.layout_video_download_card_item, parent, false));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                VideoDownloadService.VideoTaskHolder videoTaskHolder = videoTaskHolderMap.get(videoTaskHolderIds[position]);
                if (videoTaskHolder == null) {
                    return;
                }
                ((VideoDownloadHolder) holder).mPbTotal.setMax((int) videoTaskHolder.getTotalLength());
                ((VideoDownloadHolder) holder).mTvTitle.setText(videoTaskHolder.getVideoTitle());
                ((VideoDownloadHolder) holder).mTvId.setText("id:" + videoTaskHolder.getId());
                ((VideoDownloadHolder) holder).mPbVideo.setMax((int) videoTaskHolder.getVideoLength());
                if (((VideoDownloadHolder) holder).mPbTotal.getMax() != 0) {
                    ((VideoDownloadHolder) holder).mPbTotal.setIndeterminate(false);
                    ((VideoDownloadHolder) holder).mPbTotal.setProgress((int) videoTaskHolder.getTotalCurrentOffset());
                }
                if (((VideoDownloadHolder) holder).mPbVideo.getMax() != 0) {
                    ((VideoDownloadHolder) holder).mPbVideo.setIndeterminate(false);
                    ((VideoDownloadHolder) holder).mPbVideo.setProgress((int) videoTaskHolder.getVideoCurrentOffset());
                }
                ((VideoDownloadHolder) holder).mPbAudio.setMax((int) videoTaskHolder.getAudioLength());
                if (((VideoDownloadHolder) holder).mPbAudio.getMax() != 0) {
                    ((VideoDownloadHolder) holder).mPbAudio.setIndeterminate(false);
                    ((VideoDownloadHolder) holder).mPbAudio.setProgress((int) videoTaskHolder.getAudioCurrentOffset());
                }
                if (videoTaskHolder.getVideoEndCause() != null) {
                    ((VideoDownloadHolder) holder).mTvVideoSpeed.setText(videoTaskHolder.getVideoEndCause().name());
                }
                if (videoTaskHolder.getAudioEndCause() != null) {
                    ((VideoDownloadHolder) holder).mTvAudioSpeed.setText(videoTaskHolder.getAudioEndCause().name());
                }
                ((VideoDownloadHolder) holder).mTvTotalInfo.setText(videoTaskHolder.getStatus().name());
                if (videoTaskHolder.isVideoOnly()) {
                    ((VideoDownloadHolder) holder).mPbAudio.setIndeterminate(false);
                    ((VideoDownloadHolder) holder).mTvAudioSpeed.setText(getString(R.string.no_need));
                }
                videoTaskHolder.getDoubleDownloadListener().setDownloadListener2(new DownloadListener4WithSpeed() {
                    @Override
                    public void taskStart(@NonNull DownloadTask task) {

                    }

                    @Override
                    public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

                    }

                    @Override
                    public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

                    }

                    @Override
                    public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
                        if ("video".equals(task.getTag())) {
                            ((VideoDownloadHolder) holder).mPbVideo.setMax((int) info.getTotalLength());
                            ((VideoDownloadHolder) holder).mPbVideo.setIndeterminate(false);
                        } else if ("audio".equals(task.getTag())) {
                            ((VideoDownloadHolder) holder).mPbAudio.setMax((int) info.getTotalLength());
                            ((VideoDownloadHolder) holder).mPbAudio.setIndeterminate(false);
                        }
                        ((VideoDownloadHolder) holder).mPbTotal.setMax((int) videoTaskHolder.getTotalLength());
                        ((VideoDownloadHolder) holder).mPbTotal.setIndeterminate(false);
                    }

                    @Override
                    public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {

                    }

                    @Override
                    public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
                        if ("video".equals(task.getTag())) {
                            ((VideoDownloadHolder) holder).mPbVideo.setProgress((int) currentOffset);
                            ((VideoDownloadHolder) holder).mTvVideoSpeed.setText(taskSpeed.getSpeedWithSIAndFlush());
                        } else if ("audio".equals(task.getTag())) {
                            ((VideoDownloadHolder) holder).mPbAudio.setProgress((int) currentOffset);
                            ((VideoDownloadHolder) holder).mTvAudioSpeed.setText(taskSpeed.getSpeedWithSIAndFlush());
                        }
                        ((VideoDownloadHolder) holder).mPbTotal.setProgress((int) videoTaskHolder.getTotalCurrentOffset());
                    }

                    @Override
                    public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {

                    }

                    @Override
                    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                        if ("video".equals(task.getTag())) {
                            ((VideoDownloadHolder) holder).mTvVideoSpeed.setText(cause.name());
                        } else if ("audio".equals(task.getTag())) {
                            ((VideoDownloadHolder) holder).mTvAudioSpeed.setText(cause.name());
                        }
                    }
                });
                ((VideoDownloadHolder) holder).mCv.setOnClickListener(v -> {
                    if (videoTaskHolder.getStatus() == VideoDownloadService.VideoTaskHolder.Status.PAUSING) {
                        VideoDownloadService.resumeTask(getContext(), videoTaskHolder.getId());
                    } else if (videoTaskHolder.getStatus() == VideoDownloadService.VideoTaskHolder.Status.DOWNLOADING) {
                        VideoDownloadService.pauseTask(getContext(), videoTaskHolder.getId());
                    }
                    XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, videoTaskHolderIds.length);
                });
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
                private ProgressBar mPbTotal, mPbVideo, mPbAudio;
                private TextView mTvTitle, mTvTotalInfo, mTvVideoSpeed, mTvAudioSpeed, mTvId;
                private CardView mCv;

                VideoDownloadHolder(@NonNull View itemView) {
                    super(itemView);
                    mPbTotal = itemView.findViewById(R.id.pb_total);
                    mPbVideo = itemView.findViewById(R.id.pb_video);
                    mPbAudio = itemView.findViewById(R.id.pb_audio);
                    mTvTitle = itemView.findViewById(R.id.tv_title);
                    mTvTotalInfo = itemView.findViewById(R.id.tv_total_info);
                    mTvVideoSpeed = itemView.findViewById(R.id.tv_video_speed);
                    mTvAudioSpeed = itemView.findViewById(R.id.tv_audio_speed);
                    mTvId = itemView.findViewById(R.id.tv_id);
                    mCv = itemView.findViewById(R.id.cv);
                }
            }
        });
        baseBind.xrv.setLoadingMoreEnabled(false);
        baseBind.xrv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (videoTaskHolderIds != null) {
                    videoTaskHolderIds = videoTaskHolderMap.keySet().toArray(new Integer[0]);
                    XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, videoTaskHolderIds.length);
                }
                baseBind.xrv.refreshComplete();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = new Intent(getContext(), VideoDownloadService.class);
        if (getContext() != null) {
            getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null) {
            getContext().unbindService(serviceConnection);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof VideoDownloadService.MyBinder) {
                videoTaskHolderMap = ((VideoDownloadService.MyBinder) service).getVideoTaskHolderMap();
                videoTaskHolderIds = videoTaskHolderMap.keySet().toArray(new Integer[0]);
                XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, videoTaskHolderIds.length);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}

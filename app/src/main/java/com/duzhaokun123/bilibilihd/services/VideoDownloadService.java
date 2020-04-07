package com.duzhaokun123.bilibilihd.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.TaskStackBuilder;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.model.MainSavedVideoInfo;
import com.duzhaokun123.bilibilihd.model.PageSavedVideoInfo;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.download.DownloadActivity;
import com.duzhaokun123.bilibilihd.utils.DoubleDownloadListener;
import com.duzhaokun123.bilibilihd.utils.FileUtil;
import com.duzhaokun123.bilibilihd.utils.GsonUtil;
import com.duzhaokun123.bilibilihd.utils.NotificationUtil;
import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.DownloadContextListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoDownloadService extends IntentService {
    public static final String CLASS_NAME = VideoDownloadService.class.getSimpleName();

    private static final String ACTION_START_TASK = "com.duzhaokun123.bilibilihd.services.action.START_TASK";
    private static final String ACTION_CANCEL_TASK = "com.duzhaokun123.bilibilihd.services.action.CANCEL_TASK";
    private static final String ACTION_PAUSE_TASK = "com.duzhaokun123.bilibilihd.services.action.PAUSE_TASK";
    private static final String ACTION_RESUME_TASK = "com.duzhaokun123.bilibilihd.services.action.RESUME_TASK";

    private static final String EXTRA_VIDEO = "com.duzhaokun123.bilibilihd.services.extra.VIDEO";
    private static final String EXTRA_AUDIO = "com.duzhaokun123.bilibilihd.services.extra.AUDIO";
    private static final String EXTRA_DANMAKU_URL = "com.duzhaokun123.bilibilihd.services.extra.DANMAKU_URL";
    private static final String EXTRA_CACHE_PATH = "com.duzhaokun123.bilibilihd.services.extra.CACHE_PATH";
    private static final String EXTRA_VIDEO_TITLE = "com.duzhaokun123.bilibilihd.services.extra.VIDEO_TITLE";
    private static final String EXTRA_MAIN_TITLE = "com.duzhaokun123.bilibilihd.services.extra.MAIN_TITLE";
    private static final String EXTRA_BVID = "com.duzhaokun123.bilibilihd.services.extra.BVID";
    private static final String EXTRA_TASK_ID = "com.duzhaokun123.bilibilihd.services.extra.TASK_ID";
    private static final String EXTRA_IS_VIDEO_ONLY = "com.duzhaokun123.bilibilihd.services.extra.IS_VIDEO_ONLY";
    private static final String EXTRA_PAGE = "com.duzhaokun123.bilibilihd.services.extra.PAGE";

    public VideoDownloadService() {
        super("DownloadService");
    }


    public static void downloadVideo(Context context, String video, String audio, String cachePath, String videoTitle, String mainTitle, String bvid, boolean videoOnly, int page, String danmakuUrl) {
        Intent intent = new Intent(context, VideoDownloadService.class);
        intent.setAction(ACTION_START_TASK);
        intent.putExtra(EXTRA_VIDEO, video);
        intent.putExtra(EXTRA_AUDIO, audio);
        intent.putExtra(EXTRA_CACHE_PATH, cachePath);
        intent.putExtra(EXTRA_VIDEO_TITLE, videoTitle);
        intent.putExtra(EXTRA_MAIN_TITLE, mainTitle);
        intent.putExtra(EXTRA_BVID, bvid);
        intent.putExtra(EXTRA_IS_VIDEO_ONLY, videoOnly);
        intent.putExtra(EXTRA_PAGE, page);
        intent.putExtra(EXTRA_DANMAKU_URL, danmakuUrl);
        context.startService(intent);
    }

    public static void cancelTask(Context context, int taskId) {
        Intent intent = new Intent(context, VideoDownloadService.class);
        intent.setAction(ACTION_CANCEL_TASK);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        context.startService(intent);
    }

    public static void pauseTask(Context context, int taskId) {
        Intent intent = new Intent(context, VideoDownloadService.class);
        intent.setAction(ACTION_PAUSE_TASK);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        context.startService(intent);
    }

    public static void resumeTask(Context context, int taskId) {
        Intent intent = new Intent(context, VideoDownloadService.class);
        intent.setAction(ACTION_RESUME_TASK);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_TASK.equals(action)) {
                handleDownloadVideo(intent.getStringExtra(EXTRA_VIDEO),
                        intent.getStringExtra(EXTRA_AUDIO),
                        intent.getStringExtra(EXTRA_CACHE_PATH),
                        intent.getStringExtra(EXTRA_VIDEO_TITLE),
                        intent.getStringExtra(EXTRA_MAIN_TITLE),
                        intent.getStringExtra(EXTRA_BVID),
                        intent.getBooleanExtra(EXTRA_IS_VIDEO_ONLY, false),
                        intent.getIntExtra(EXTRA_PAGE, 1),
                        intent.getStringExtra(EXTRA_DANMAKU_URL));
            } else if (ACTION_CANCEL_TASK.equals(action)) {
                handleCancelTask(intent.getIntExtra(EXTRA_TASK_ID, 0));
            } else if (ACTION_PAUSE_TASK.equals(action)) {
                handlePauseTask(intent.getIntExtra(EXTRA_TASK_ID, 0));
            } else if (ACTION_RESUME_TASK.equals(action)) {
                handleResumeTask(intent.getIntExtra(EXTRA_TASK_ID, 0));
            }
        }
    }

    private static Map<Integer, VideoTaskHolder> videoTaskHolderMap;

    private void handleDownloadVideo(String video, String audio, String cachePath, String videoTitle, String mainTitle, String bvid, boolean videoOnly, int page, String danmakuUrl) {
        DownloadContext.Builder builder = new DownloadContext.QueueSet()
                .setParentPath(cachePath)
                .setMinIntervalMillisCallbackProcess(1000)
                .commit();
        Map<String, List<String>> headerMapFields = new HashMap<>();
        List<String> userAgent = new ArrayList<>();
        userAgent.add(PBilibiliClient.Companion.getInstance().getBilibiliClient().getBillingClientProperties().getDefaultUserAgent());
        headerMapFields.put("User-Agent", userAgent);
        DownloadTask videoTask = new DownloadTask.Builder(video, cachePath, "video.m4s")
                .setPassIfAlreadyCompleted(false)
                .setHeaderMapFields(headerMapFields)
                .build();
        videoTask.setTag("video");
        builder.bindSetTask(videoTask);
        if (!videoOnly) {
            DownloadTask audioTask = new DownloadTask.Builder(audio, cachePath, "audio.m4s")
                    .setPassIfAlreadyCompleted(false)
                    .setHeaderMapFields(headerMapFields)
                    .build();
            audioTask.setTag("audio");
            builder.bindSetTask(audioTask);
        }
        builder.setListener(new DownloadContextListener() {
            @Override
            public void taskEnd(@NonNull DownloadContext context, @NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, int remainCount) {

            }

            @Override
            public void queueEnd(@NonNull DownloadContext context) {

            }
        });
        DownloadContext downloadContext = builder.build();

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_video_download);
        if (videoTaskHolderMap == null) {
            videoTaskHolderMap = new HashMap<>();
        }
        int id;
        do { //用时间戳分id真的好吗
            id = (int) System.currentTimeMillis();
        } while (videoTaskHolderMap.get(id) != null || !NotificationUtil.isIdUnregistered(id) || id == 0);
        int finalId = id;
        DoubleDownloadListener doubleDownloadListener = new DoubleDownloadListener(new MyDownloadListener(remoteViews, finalId), null);
        VideoTaskHolder videoTaskHolder = new VideoTaskHolder(downloadContext, doubleDownloadListener, finalId, remoteViews, cachePath, bvid, videoTitle, mainTitle, videoOnly, page, danmakuUrl);
        videoTaskHolderMap.put(id, videoTaskHolder);

        remoteViews.setTextViewText(R.id.tv_title, videoTitle);
        remoteViews.setTextViewText(R.id.tv_id, "id:" + finalId);
        if (videoOnly) {
            remoteViews.setProgressBar(R.id.pb_audio, 0, 0, false);
            remoteViews.setTextViewText(R.id.tv_audio_speed, getString(R.string.no_need));
            videoTaskHolder.audioLength = 0;
            videoTaskHolder.audioCurrentOffset = 0;
            videoTaskHolder.audioEndCause = EndCause.COMPLETED;
        }

        Intent downloadActivityIntent = new Intent(this, DownloadActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(downloadActivityIntent);
        PendingIntent downloadActivityPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancelIntent = new Intent(this, VideoDownloadService.class);
        cancelIntent.setAction(ACTION_CANCEL_TASK);
        cancelIntent.putExtra(EXTRA_TASK_ID, finalId);
        PendingIntent cancelPendingIntent = PendingIntent.getService(this, finalId, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, VideoDownloadService.class);
        pauseIntent.setAction(ACTION_PAUSE_TASK);
        pauseIntent.putExtra(EXTRA_TASK_ID, finalId);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, finalId, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this, NotificationUtil.CHANNEL_ID_VIDEO_DOWNLOAD)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setColor(getColor(R.color.colorAccent))
                .setShowWhen(false)
                .setContentTitle(getString(R.string.download))
                .setStyle(new Notification.DecoratedCustomViewStyle())
                .setCustomContentView(remoteViews)
                .setContentIntent(downloadActivityPendingIntent)
                .setAutoCancel(false)
                .addAction(new Notification.Action.Builder(null, getString(android.R.string.cancel), cancelPendingIntent).build())
                .addAction(new Notification.Action.Builder(null, getString(R.string.pause), pausePendingIntent).build())
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        NotificationUtil.show(this, finalId, notification);

        videoTaskHolder.status = VideoTaskHolder.Status.DOWNLOADING;
        downloadContext.startOnParallel(doubleDownloadListener);
    }

    private void merge(@NonNull VideoTaskHolder videoTaskHolder) {
        if (videoTaskHolder.videoOnly) {
            moveVideoToPublicDir(videoTaskHolder);
            return;
        }
        videoTaskHolder.remoteViews.setTextViewText(R.id.tv_total_info, getText(R.string.merging));
        videoTaskHolder.remoteViews.setProgressBar(R.id.pb_total, 0, 0, true);
        videoTaskHolder.status = VideoTaskHolder.Status.MERGING;
        int rc = FFmpeg.execute(new String[]{
                "-i", videoTaskHolder.cachePath + File.separator + "video.m4s",
                "-i", videoTaskHolder.cachePath + File.separator + "audio.m4s",
                "-vcodec", "copy", "-acodec", "copy",
                videoTaskHolder.cachePath + File.separator + "out.flv"});
        if (rc == Config.RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
            videoTaskHolder.remoteViews.setProgressBar(R.id.pb_total, 1, 1, false);
            videoTaskHolder.status = VideoTaskHolder.Status.UNKNOWN;
            moveVideoToPublicDir(videoTaskHolder);
        } else if (rc == Config.RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
            videoTaskHolder.remoteViews.setTextViewText(R.id.tv_total_info, getString(R.string.failure));
            videoTaskHolder.remoteViews.setProgressBar(R.id.pb_total, 1, 0, false);
            Notification notification = NotificationUtil.getNotification(videoTaskHolder.id);
            if (notification != null) {
                notification = Notification.Builder.recoverBuilder(VideoDownloadService.this, notification)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setShowWhen(true)
                        .setAutoCancel(false)
                        .build();
                NotificationUtil.makeNotificationCleanable(notification);
                NotificationUtil.reshow(VideoDownloadService.this, videoTaskHolder.id, notification);
            }
        }
    }

    private void moveVideoToPublicDir(VideoTaskHolder videoTaskHolder) {
        videoTaskHolder.status = VideoTaskHolder.Status.MOVING;
        videoTaskHolder.remoteViews.setTextViewText(R.id.tv_total_info, getString(R.string.moving));

        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dir = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES) + File.separator + "bilibili HD" + File.separator + videoTaskHolder.bvid);
        } else {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + "bilibili HD" + File.separator + videoTaskHolder.bvid);
        }
        if (!dir.exists() && !dir.mkdirs()) {
            Notification notification = NotificationUtil.getNotification(videoTaskHolder.id);
            if (notification != null) {
                notification = Notification.Builder.recoverBuilder(VideoDownloadService.this, notification)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .build();
                videoTaskHolder.remoteViews.setTextViewText(R.id.tv_total_info, "cannot mkdirs() " + dir.getPath());
                NotificationUtil.reshow(VideoDownloadService.this, videoTaskHolder.id, notification);
            }
            NotificationUtil.reshow(this, videoTaskHolder.id);
            return;
        }

        FileOutputStream mainSavedVideoInfoFileOutputStream = null;
        OutputStreamWriter mainSavedVideoInfoOutputStreamWriter = null;
        try {
            mainSavedVideoInfoFileOutputStream = new FileOutputStream(new File(dir, "info.json"));
            mainSavedVideoInfoOutputStreamWriter = new OutputStreamWriter(mainSavedVideoInfoFileOutputStream);
            mainSavedVideoInfoOutputStreamWriter.write(GsonUtil.getGsonInstance().toJson(new MainSavedVideoInfo(videoTaskHolder.mainTitle, videoTaskHolder.bvid)));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mainSavedVideoInfoOutputStreamWriter != null) {
                    mainSavedVideoInfoOutputStreamWriter.close();
                }
                if (mainSavedVideoInfoFileOutputStream != null) {
                    mainSavedVideoInfoFileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dir = new File(dir, String.valueOf(videoTaskHolder.page));
        if (!dir.exists() && !dir.mkdirs()) {
            Notification notification = NotificationUtil.getNotification(videoTaskHolder.id);
            if (notification != null) {
                notification = Notification.Builder.recoverBuilder(VideoDownloadService.this, notification)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .build();
                videoTaskHolder.remoteViews.setTextViewText(R.id.tv_total_info, "cannot mkdirs() " + dir.getPath());
                NotificationUtil.reshow(VideoDownloadService.this, videoTaskHolder.id, notification);
            }
            NotificationUtil.reshow(this, videoTaskHolder.id);
            return;
        }

        FileOutputStream pageSavedVideoInfoFileOutPutStream = null;
        OutputStreamWriter pageSavedVideoInfoOutPutStreamWriter = null;
        FileInputStream videoFileInputStream = null;
        FileOutputStream videoFileOutputStream = null;
        try {
            if (videoTaskHolder.videoOnly) {
                videoFileInputStream = new FileInputStream(new File(videoTaskHolder.cachePath, "video.m4s"));
            } else {
                videoFileInputStream = new FileInputStream(new File(videoTaskHolder.cachePath, "out.flv"));
            }
            videoFileOutputStream = new FileOutputStream(new File(dir, "video.flv"));
            FileUtil.copy(videoFileInputStream, videoFileOutputStream);

            pageSavedVideoInfoFileOutPutStream = new FileOutputStream(new File(dir, "info.json"));
            pageSavedVideoInfoOutPutStreamWriter = new OutputStreamWriter(pageSavedVideoInfoFileOutPutStream);
            pageSavedVideoInfoOutPutStreamWriter.write(GsonUtil.getGsonInstance().toJson(new PageSavedVideoInfo(videoTaskHolder.page, videoTaskHolder.videoTitle, videoTaskHolder.danmakuUrl, System.currentTimeMillis())));

            videoTaskHolder.status = VideoTaskHolder.Status.FINISH;
            videoTaskHolder.remoteViews.setTextViewText(R.id.tv_total_info, getString(R.string.success));
            Notification notification = NotificationUtil.getNotification(videoTaskHolder.id);
            if (notification != null) {
                notification = Notification.Builder.recoverBuilder(VideoDownloadService.this, notification)
                        .setSmallIcon(R.drawable.ic_done)
                        .build();
                notification.actions = null;
                NotificationUtil.makeNotificationCleanable(notification);
                NotificationUtil.reshow(VideoDownloadService.this, videoTaskHolder.id, notification);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Notification notification = NotificationUtil.getNotification(videoTaskHolder.id);
            if (notification != null) {
                notification = Notification.Builder.recoverBuilder(VideoDownloadService.this, notification)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .build();
                videoTaskHolder.remoteViews.setTextViewText(R.id.tv_total_info, e.getMessage());
                NotificationUtil.reshow(VideoDownloadService.this, videoTaskHolder.id, notification);
            }
            NotificationUtil.reshow(this, videoTaskHolder.id);
        } finally {
            if (videoFileInputStream != null) {
                try {
                    videoFileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (videoFileOutputStream != null) {
                try {
                    videoFileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (pageSavedVideoInfoOutPutStreamWriter != null) {
                try {
                    pageSavedVideoInfoOutPutStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (pageSavedVideoInfoFileOutPutStream != null) {
                try {
                    pageSavedVideoInfoFileOutPutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        NotificationUtil.unregister(videoTaskHolder.id);
        videoTaskHolderMap.remove(videoTaskHolder.id);
        if (!FileUtil.deleteDir(new File(videoTaskHolder.cachePath))) {
            Log.d(CLASS_NAME, "cannot delete " + videoTaskHolder.cachePath);
        }
    }

    private void handleCancelTask(int taskId) {
        if (videoTaskHolderMap == null) {
            return;
        }
        VideoTaskHolder videoTaskHolder = videoTaskHolderMap.get(taskId);
        if (videoTaskHolder == null) {
            return;
        }
        videoTaskHolder.downloadContext.stop();
        videoTaskHolderMap.remove(taskId);
        NotificationUtil.remove(this, taskId);
        if (!FileUtil.deleteDir(new File(videoTaskHolder.cachePath))) {
            Log.d(CLASS_NAME, "cannot delete " + videoTaskHolder.cachePath);
        }
    }

    private void handlePauseTask(int taskId) {
        if (videoTaskHolderMap == null) {
            return;
        }
        VideoTaskHolder videoTaskHolder = videoTaskHolderMap.get(taskId);
        if (videoTaskHolder == null) {
            return;
        }
        videoTaskHolder.status = VideoTaskHolder.Status.PAUSING;
        videoTaskHolder.downloadContext.stop();
        Notification notification = NotificationUtil.getNotification(taskId);
        if (notification == null) {
            return;
        }

        Intent resumeIntent = new Intent(this, VideoDownloadService.class);
        resumeIntent.setAction(ACTION_RESUME_TASK);
        resumeIntent.putExtra(EXTRA_TASK_ID, taskId);
        PendingIntent resumePendingIntent = PendingIntent.getService(this, taskId, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = Notification.Builder.recoverBuilder(this, notification)
                .setSmallIcon(R.drawable.ic_pause)
                .setActions(new Notification.Action[]{notification.actions[0], new Notification.Action.Builder(null, getString(R.string.resume), resumePendingIntent).build()})
                .build();
        NotificationUtil.reshow(this, taskId, notification);
    }

    private void handleResumeTask(int taskId) {
        if (videoTaskHolderMap == null) {
            return;
        }
        VideoTaskHolder videoTaskHolder = videoTaskHolderMap.get(taskId);
        if (videoTaskHolder == null) {
            return;
        }

        videoTaskHolder.status = VideoTaskHolder.Status.DOWNLOADING;
        videoTaskHolder.downloadContext.startOnParallel(videoTaskHolder.doubleDownloadListener);

        Notification notification = NotificationUtil.getNotification(taskId);
        if (notification == null) {
            return;
        }
        Intent pauseIntent = new Intent(this, VideoDownloadService.class);
        pauseIntent.setAction(ACTION_PAUSE_TASK);
        pauseIntent.putExtra(EXTRA_TASK_ID, taskId);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, taskId, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = Notification.Builder.recoverBuilder(this, notification)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setActions(new Notification.Action[]{notification.actions[0], new Notification.Action.Builder(null, getString(R.string.pause), pausePendingIntent).build()})
                .build();
        NotificationUtil.reshow(this, taskId, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (videoTaskHolderMap == null) {
            videoTaskHolderMap = new HashMap<>();
        }
        for (VideoTaskHolder videoTaskHolder : videoTaskHolderMap.values()) {
            videoTaskHolder.doubleDownloadListener.setDownloadListener2(null);
        }
        return false;
    }

    public static class MyBinder extends Binder {
        @NonNull
        public Map<Integer, VideoTaskHolder> getVideoTaskHolderMap() {
            if (videoTaskHolderMap == null) {
                videoTaskHolderMap = new HashMap<>();
            }
            return videoTaskHolderMap;
        }
    }

    class MyDownloadListener extends DownloadListener4WithSpeed {

        private RemoteViews remoteViews;
        private int finalId;

        MyDownloadListener(RemoteViews remoteViews, int finalId ) {
            this.remoteViews = remoteViews;
            this.finalId = finalId;
        }

        @Override
        public void taskStart(@NonNull DownloadTask task) {
            Log.d(CLASS_NAME, "taskStart " + task.getFilename());
            if ("video".equals(task.getTag())) {
                remoteViews.setProgressBar(R.id.pb_video, 0, 0, true);
            } else if ("audio".equals(task.getTag())) {
                remoteViews.setProgressBar(R.id.pb_audio, 0, 0, true);
            }
            NotificationUtil.reshow(VideoDownloadService.this, finalId);
        }

        @Override
        public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

        }

        @Override
        public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

        }

        @Override
        public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
            if (videoTaskHolderMap == null) {
                return;
            }
            VideoTaskHolder videoTaskHolder = videoTaskHolderMap.get(finalId);
            if (videoTaskHolder == null) {
                return;
            }
            if ("video".equals(task.getTag())) {
                remoteViews.setProgressBar(R.id.pb_video, (int) info.getTotalLength(), 0, false);
                videoTaskHolder.videoLength = info.getTotalLength();
            } else if ("audio".equals(task.getTag())) {
                remoteViews.setProgressBar(R.id.pb_audio, (int) info.getTotalLength(), 0, false);
                videoTaskHolder.audioLength = info.getTotalLength();
            }
            remoteViews.setProgressBar(R.id.pb_total, (int) videoTaskHolder.getTotalLength(), 0, false);
            NotificationUtil.reshow(VideoDownloadService.this, finalId);
        }

        @Override
        public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {
        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
            if (videoTaskHolderMap == null) {
                return;
            }
            VideoTaskHolder videoTaskHolder = videoTaskHolderMap.get(finalId);
            if (videoTaskHolder == null) {
                return;
            }
            if ("video".equals(task.getTag())) {
                remoteViews.setInt(R.id.pb_video, "setProgress", (int) currentOffset);
                remoteViews.setTextViewText(R.id.tv_video_speed, taskSpeed.getSpeedWithSIAndFlush());
                videoTaskHolder.videoCurrentOffset = currentOffset;
            } else if ("audio".equals(task.getTag())) {
                remoteViews.setInt(R.id.pb_audio, "setProgress", (int) currentOffset);
                remoteViews.setTextViewText(R.id.tv_audio_speed, taskSpeed.getSpeedWithSIAndFlush());
                videoTaskHolder.audioCurrentOffset = currentOffset;
            }
            remoteViews.setInt(R.id.pb_total, "setProgress", (int) videoTaskHolder.getTotalCurrentOffset());
            NotificationUtil.reshow(VideoDownloadService.this, finalId);
        }

        @Override
        public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {

        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
            Log.d(CLASS_NAME, "taskEnd " + task.getFilename() + " cause " + cause.name());
            if (videoTaskHolderMap == null) {
                return;
            }
            VideoTaskHolder videoTaskHolder = videoTaskHolderMap.get(finalId);
            if (videoTaskHolder == null) {
                return;
            }
            if ("video".equals(task.getTag())) {
                remoteViews.setTextViewText(R.id.tv_video_speed, cause.name());
                videoTaskHolder.videoEndCause = cause;
            } else if ("audio".equals(task.getTag())) {
                remoteViews.setTextViewText(R.id.tv_audio_speed, cause.name());
                videoTaskHolder.audioEndCause = cause;
            }
            if (videoTaskHolder.status == VideoTaskHolder.Status.PAUSING) {
                remoteViews.setTextViewText(R.id.tv_total_info, VideoTaskHolder.Status.PAUSING.name());
            }
            if (cause != EndCause.COMPLETED) {
                videoTaskHolder.status = VideoTaskHolder.Status.ERROR;
                remoteViews.setTextViewText(R.id.tv_total_info, VideoTaskHolder.Status.ERROR.name());
            }
            Notification notification = NotificationUtil.getNotification(finalId);
            if (videoTaskHolder.videoEndCause != null && videoTaskHolder.audioEndCause != null) {
                if (videoTaskHolder.videoEndCause == EndCause.COMPLETED && videoTaskHolder.audioEndCause == EndCause.COMPLETED) {
                    videoTaskHolder.status = VideoTaskHolder.Status.UNKNOWN;
                    merge(videoTaskHolder);
                } else if (notification != null && videoTaskHolder.status != VideoTaskHolder.Status.PAUSING) {
                    NotificationUtil.makeNotificationCleanable(notification);
                    notification = Notification.Builder.recoverBuilder(VideoDownloadService.this, notification)
                            .setSmallIcon(android.R.drawable.stat_sys_warning)
                            .setShowWhen(true)
                            .setAutoCancel(false)
                            .build();
                    NotificationUtil.reshow(VideoDownloadService.this, finalId, notification);
                }
            }
            NotificationUtil.reshow(VideoDownloadService.this, finalId);
        }

    }
    public static class VideoTaskHolder {
        VideoTaskHolder(DownloadContext downloadContext, DoubleDownloadListener douDownloadListener, int id, RemoteViews remoteViews, String cachePath, String bvid, String videoTitle, String mainTitle, boolean videoOnly, int page, String danmakuUrl) {
            this.downloadContext = downloadContext;
            this.doubleDownloadListener = douDownloadListener;
            this.id = id;
            this.remoteViews = remoteViews;
            this.cachePath = cachePath;
            this.bvid = bvid;
            this.videoTitle = videoTitle;
            this.mainTitle = mainTitle;
            this.videoOnly = videoOnly;
            this.page = page;
            this.danmakuUrl = danmakuUrl;
        }

        DownloadContext downloadContext;
        DoubleDownloadListener doubleDownloadListener;
        RemoteViews remoteViews;
        String cachePath;
        @Nullable
        EndCause videoEndCause, audioEndCause;
        String bvid;
        String videoTitle, mainTitle;
        String danmakuUrl;
        Status status = Status.UNKNOWN;
        long videoLength, audioLength;
        long videoCurrentOffset, audioCurrentOffset;
        int id, page;
        boolean videoOnly;

        public long getTotalCurrentOffset() {
            return videoCurrentOffset + audioCurrentOffset;
        }

        public long getTotalLength() {
            return videoLength + audioLength;
        }

        public DownloadContext getDownloadContext() {
            return downloadContext;
        }

        public DoubleDownloadListener getDoubleDownloadListener() {
            return doubleDownloadListener;
        }

        public RemoteViews getRemoteViews() {
            return remoteViews;
        }

        public String getCachePath() {
            return cachePath;
        }

        @Nullable
        public EndCause getVideoEndCause() {
            return videoEndCause;
        }

        @Nullable
        public EndCause getAudioEndCause() {
            return audioEndCause;
        }

        public String getBvid() {
            return bvid;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public String getMainTitle() {
            return mainTitle;
        }

        public String getDanmakuUrl() {
            return danmakuUrl;
        }

        public Status getStatus() {
            return status;
        }

        public long getVideoLength() {
            return videoLength;
        }

        public long getAudioLength() {
            return audioLength;
        }

        public long getVideoCurrentOffset() {
            return videoCurrentOffset;
        }

        public long getAudioCurrentOffset() {
            return audioCurrentOffset;
        }

        public int getId() {
            return id;
        }

        public int getPage() {
            return page;
        }

        public boolean isVideoOnly() {
            return videoOnly;
        }

        public enum Status {
            DOWNLOADING, PAUSING, MERGING, MOVING, UNKNOWN, FINISH, ERROR
        }
    }
}

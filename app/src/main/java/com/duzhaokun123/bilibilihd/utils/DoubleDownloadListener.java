package com.duzhaokun123.bilibilihd.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

import java.util.List;
import java.util.Map;

public class DoubleDownloadListener implements DownloadListener {
    private DownloadListener downloadListener1, downloadListener2;

    public DoubleDownloadListener(DownloadListener downloadListener1, DownloadListener downloadListener2) {
        this.downloadListener1 = downloadListener1;
        this.downloadListener2 = downloadListener2;
    }

    public void setDownloadListener2(DownloadListener downloadListener2) {
        this.downloadListener2 = downloadListener2;
    }

    @Override
    public void taskStart(@NonNull DownloadTask task) {
        if (downloadListener1 != null) {
            downloadListener1.taskStart(task);
        }
        if (downloadListener2 != null) {
            downloadListener2.taskStart(task);
        }
    }

    @Override
    public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {
        if (downloadListener1 != null) {
            downloadListener1.connectTrialStart(task, requestHeaderFields);
        }
        if (downloadListener2 != null) {
            downloadListener2.connectTrialStart(task, requestHeaderFields);
        }
    }

    @Override
    public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
        if (downloadListener1 != null) {
            downloadListener1.connectTrialEnd(task, responseCode, responseHeaderFields);
        }
        if (downloadListener2 != null) {
            downloadListener2.connectTrialEnd(task, responseCode, responseHeaderFields);
        }
    }

    @Override
    public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {
        if (downloadListener1 != null) {
            downloadListener1.downloadFromBeginning(task, info, cause);
        }
        if (downloadListener2 != null) {
            downloadListener2.downloadFromBeginning(task, info, cause);
        }
    }

    @Override
    public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
        if (downloadListener1 != null) {
            downloadListener1.downloadFromBreakpoint(task, info);
        }
        if (downloadListener2 != null) {
            downloadListener2.downloadFromBreakpoint(task, info);
        }
    }

    @Override
    public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
        if (downloadListener1 != null) {
            downloadListener1.connectStart(task, blockIndex, requestHeaderFields);
        }
        if (downloadListener2 != null) {
            downloadListener2.connectStart(task, blockIndex, requestHeaderFields);
        }
    }

    @Override
    public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
        if (downloadListener1 != null) {
            downloadListener1.connectEnd(task, blockIndex, responseCode, responseHeaderFields);
        }
        if (downloadListener2 != null) {
            downloadListener2.connectEnd(task, blockIndex, responseCode, responseHeaderFields);
        }
    }

    @Override
    public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
        if (downloadListener1 != null) {
            downloadListener1.fetchStart(task, blockIndex, contentLength);
        }
        if (downloadListener2 != null) {
            downloadListener2.fetchStart(task, blockIndex, contentLength);
        }
    }

    @Override
    public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
        if (downloadListener1 != null) {
            downloadListener1.fetchProgress(task, blockIndex, increaseBytes);
        }
        if (downloadListener2 != null) {
            downloadListener2.fetchProgress(task, blockIndex, increaseBytes);
        }
    }

    @Override
    public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
        if (downloadListener1 != null) {
            downloadListener1.fetchEnd(task, blockIndex, contentLength);
        }
        if (downloadListener2 != null) {
            downloadListener2.fetchEnd(task, blockIndex, contentLength);
        }
    }

    @Override
    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
        if (downloadListener1 != null) {
            downloadListener1.taskEnd(task, cause, realCause);
        }
        if (downloadListener2 != null) {
            downloadListener2.taskEnd(task, cause, realCause);
        }
    }
}

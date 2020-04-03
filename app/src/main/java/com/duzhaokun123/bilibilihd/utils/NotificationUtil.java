package com.duzhaokun123.bilibilihd.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.duzhaokun123.bilibilihd.R;

import java.util.HashMap;
import java.util.Map;

public class NotificationUtil {
    public static String CHANNEL_GROUP_ID_DOWNLOAD = "download";
    public static String CHANNEL_ID_VIDEO_DOWNLOAD = "video_download";

    private static Map<Integer, Notification> notificationMap;

    public static boolean isIdUnregistered(int id) {
        return notificationMap == null || notificationMap.get(id) == null;
    }

    public static void init(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(CHANNEL_GROUP_ID_DOWNLOAD, context.getString(R.string.download));
        notificationManager.createNotificationChannelGroup(notificationChannelGroup);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_VIDEO_DOWNLOAD, context.getString(R.string.video_download), NotificationManager.IMPORTANCE_LOW);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setGroup(CHANNEL_GROUP_ID_DOWNLOAD);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void show(Context context, int id, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        if (notificationMap == null) {
            notificationMap = new HashMap<>();
        }
        if (notificationMap.get(id) != null) {
            Log.e("NotificationUtil", "should use reshow");
            return;
        }
        notificationMap.put(id, notification);
        notificationManager.notify(id, notification);
    }

    public static void reshow(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null || notificationMap == null) {
            return;
        }
        if (notificationMap.get(id) == null) {
            return;
        }
        notificationManager.notify(id, notificationMap.get(id));
    }

    public static void reshow(Context context, int id, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null || notificationMap == null) {
            return;
        }
        if (notificationMap.get(id) == null) {
            return;
        }
        notificationMap.put(id, notification);
        notificationManager.notify(id, notification);
    }

    public static void remove(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null || notificationMap == null) {
            return;
        }
        notificationManager.cancel(id);
        notificationMap.remove(id);
    }

    public static Notification getNotification(int id) {
        return notificationMap == null ? null : notificationMap.get(id);
    }

    public static void makeNotificationCleanable(Notification notification) {
        if (notification != null) {
            notification.flags &= ~ Notification.FLAG_NO_CLEAR;
        }
    }

    public static void unregister(int id) {
        if (notificationMap != null) {
            notificationMap.remove(id);
        }
    }
}

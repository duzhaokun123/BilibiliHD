package com.duzhaokun123.bilibilihd.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import com.duzhaokun123.bilibilihd.R
import java.util.*

object NotificationUtil {
    const val CHANNEL_GROUP_ID_PLAY = "play"
    const val CHANNEL_ID_VIDEO_PLAY_BACKGROUND = "play_background"

    private var notificationMap: MutableMap<Int, Notification> = HashMap()
    private var context: Context? = null
    private var notificationManager: NotificationManager? = null

    fun isIdUnregistered(id: Int): Boolean {
        return notificationMap[id] == null
    }

    fun setContext(context: Context) {
        this.context = context
        this.notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun getNewId(): Int {
        while (true) {
            val id: Int = System.currentTimeMillis().toInt()
            if (id != 0 && isIdUnregistered(id)) {
                return id
            }
        }
    }

    fun init(context: Context) {
        val notificationChannelGroup = NotificationChannelGroup(CHANNEL_GROUP_ID_PLAY, context.getString(R.string.play))
        notificationManager?.createNotificationChannelGroup(notificationChannelGroup)
        val notificationChannel = NotificationChannel(CHANNEL_ID_VIDEO_PLAY_BACKGROUND, context.getString(R.string.play_background), NotificationManager.IMPORTANCE_MIN)
        notificationChannel.enableLights(false)
        notificationChannel.enableVibration(true)
        notificationChannel.group = CHANNEL_GROUP_ID_PLAY
        notificationManager?.createNotificationChannel(notificationChannel)
    }

    fun show(id: Int, notification: Notification) {
        notificationMap[id] = notification
        try {
            notificationManager?.notify(id, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun reshow(id: Int) {
        if (notificationMap[id] == null) {
            return
        }
        try {
            notificationManager?.notify(id, notificationMap[id])
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun reshow(id: Int, notification: Notification?) {
        if (notificationMap[id] == null || notification == null) {
            return
        }
        notificationMap[id] = notification
        try {
            notificationManager?.notify(id, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun remove(id: Int) {
        notificationManager?.cancel(id)
        notificationMap.remove(id)
    }

    fun getNotification(id: Int): Notification? {
        return notificationMap[id]
    }

    /**
     * FIXME: 无法设置为可清除
     */
    fun setNotificationCleanable(id: Int, cleanable: Boolean) {
        val notification = getNotification(id)
        if (notification != null) {
            if (cleanable) {
                notification.flags = (notification.flags and Notification.FLAG_NO_CLEAR).inv()
            } else {
                notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
            }
        }
        reshow(id)
    }

    fun unregister(id: Int) {
        notificationMap.remove(id)
    }
}
package com.example.streaming.streamnotification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.streaming.R
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.offline.Download


/*
* Copyright (C) 2018 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/ /** Helper for creating download notifications.  */
class StreamNT(private val context: Context, private val channelId: String?) {
    private val notificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(context.applicationContext, channelId!!)

    /**
     * Returns a progress notification for the given downloads.
     *
     * @param context A context.
     * @param smallIcon A small icon for the notification.
     * @param actionLeft An optional content intent to send when the notification is clicked.
     * @param actionRight An optional content intent to send when the notification is clicked.
     * @param message An optional message to display on the notification.
     * @param downloads The downloads.
     * @return The notification.
     */
    fun buildProgressNotification(
        context: Context,
        @DrawableRes smallIcon: Int,
        actionLeft: NotificationCompat.Action?,
        message: String?,
        downloads: List<Download>
    ): Notification {
        var totalPercentage = 0f
        var downloadTaskCount = 0
        var allDownloadPercentagesUnknown = true
        var haveDownloadedBytes = false
        var haveDownloadTasks = false
        var haveRemoveTasks = false
        for (i in downloads.indices) {
            val download = downloads[i]
            if (download.state == Download.STATE_REMOVING) {
                haveRemoveTasks = true
                continue
            }
            if (download.state != Download.STATE_RESTARTING
                && download.state != Download.STATE_DOWNLOADING
            ) {
                continue
            }
            haveDownloadTasks = true
            val downloadPercentage = download.percentDownloaded
            if (downloadPercentage != C.PERCENTAGE_UNSET.toFloat()) {
                allDownloadPercentagesUnknown = false
                totalPercentage += downloadPercentage
            }
            haveDownloadedBytes = haveDownloadedBytes or (download.bytesDownloaded > 0)
            downloadTaskCount++
        }
        val titleStringId =
            if (haveDownloadTasks) R.string.exo_download_downloading else if (haveRemoveTasks) R.string.exo_download_removing else NULL_STRING_ID
        var progress = 0
        var indeterminate = true
        if (haveDownloadTasks) {
            progress = (totalPercentage / downloadTaskCount).toInt()
            indeterminate = allDownloadPercentagesUnknown && haveDownloadedBytes
        }
        return buildNotification(
            context,
            smallIcon,
            actionLeft,
            message,
            titleStringId,  /* maxProgress= */
            100,
            progress,
            indeterminate,  /* ongoing= */
            true,  /* showWhen= */
            false
        )
    }

    /**
     * Returns a notification for a completed download.
     *
     * @param context A context.
     * @param smallIcon A small icon for the notifications.
     * @param message An optional message to display on the notification.
     * @return The notification.
     */
    fun buildDownloadCompletedNotification(
        context: Context,
        @DrawableRes smallIcon: Int,
        message: String?
    ): Notification {
        val titleStringId = R.string.exo_download_completed
        return buildEndStateNotification(context, smallIcon, null, null, message, titleStringId)
    }

    /**
     * Returns a notification for a completed download.
     *
     * @param context A context.
     * @param smallIcon A small icon for the notifications.
     * @param actionLeft An optional content intent to send when the notification is clicked.
     * @param actionRight An optional content intent to send when the notification is clicked.
     * @param message An optional message to display on the notification.
     * @return The notification.
     */
    fun buildDownloadPauseNotification(
        context: Context,
        @DrawableRes smallIcon: Int,
        actionLeft: NotificationCompat.Action?,
        actionRight: NotificationCompat.Action?,
        message: String?
    ): Notification {
        val titleStringId = R.string.download_pause
        return buildEndStateNotification(
            context,
            smallIcon,
            actionLeft,
            actionRight,
            message,
            titleStringId
        )
    }

    /**
     * Returns a notification for a completed download.
     *
     * @param context A context.
     * @param smallIcon A small icon for the notifications.
     * @param actionLeft An optional content intent to send when the notification is clicked.
     * @param actionRight An optional content intent to send when the notification is clicked.
     * @param message An optional message to display on the notification.
     * @return The notification.
     */
    fun buildDownloadResumeNotification(
        context: Context,
        @DrawableRes smallIcon: Int,
        actionLeft: NotificationCompat.Action?,
        actionRight: NotificationCompat.Action?,
        message: String?
    ): Notification {
        val titleStringId = R.string.exo_download_completed
        return buildEndStateNotification(
            context,
            smallIcon,
            actionLeft,
            actionRight,
            message,
            titleStringId
        )
    }

    /**
     * Returns a notification for a failed download.
     *
     * @param context A context.
     * @param smallIcon A small icon for the notifications.
     * @param actionLeft An optional content intent to send when the notification is clicked.
     * @param actionRight An optional content intent to send when the notification is clicked.
     * @param message An optional message to display on the notification.
     * @return The notification.
     */
    fun buildDownloadFailedNotification(
        context: Context,
        @DrawableRes smallIcon: Int,
        actionLeft: NotificationCompat.Action?,
        actionRight: NotificationCompat.Action?,
        message: String?
    ): Notification {
        @StringRes val titleStringId = R.string.exo_download_failed
        return buildEndStateNotification(
            context,
            smallIcon,
            actionLeft,
            actionRight,
            message,
            titleStringId
        )
    }

    fun buildDownloadCancelNotification(context: Context,notificationId:Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    private fun buildEndStateNotification(
        context: Context,
        @DrawableRes smallIcon: Int,
        actionLeft: NotificationCompat.Action?,
        actionRight: NotificationCompat.Action?,
        message: String?,
        @StringRes titleStringId: Int
    ): Notification {
        return buildNotification(
            context,
            smallIcon,
            actionLeft,
            message,
            titleStringId,  /* maxProgress= */
            0,  /* currentProgress= */
            0,  /* indeterminateProgress= */
            false,  /* ongoing= */
            false,  /* showWhen= */
            true
        )
    }

    @SuppressLint("RestrictedApi")
    private fun buildNotification(
        context: Context,
        @DrawableRes smallIcon: Int,
        actionLeft: NotificationCompat.Action?,
        message: String?,
        @StringRes titleStringId: Int,
        maxProgress: Int,
        currentProgress: Int,
        indeterminateProgress: Boolean,
        ongoing: Boolean,
        showWhen: Boolean
    ): Notification {
        notificationBuilder.setSmallIcon(smallIcon)
        notificationBuilder.setContentTitle(
            if (titleStringId == NULL_STRING_ID) null else context.resources.getString(titleStringId)
        )


//        notificationBuilder.setStyle(
//            if (message == null) null else NotificationCompat.BigTextStyle().bigText(message)
//        )
        Log.d("checkType","check "+message)
        notificationBuilder.color = ContextCompat.getColor(context, R.color.colorAccent)
        notificationBuilder.setProgress(maxProgress, currentProgress, indeterminateProgress)
        notificationBuilder.setOngoing(ongoing)
        notificationBuilder.setShowWhen(showWhen)
        notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
        if (actionLeft != null){
           if (notificationBuilder.mActions?.size == 0){
                notificationBuilder.addAction(actionLeft)
            }
        }else{
            notificationBuilder.mActions.clear()
        }
        return notificationBuilder.build()
    }

    fun showStatusDownload(largeIcon:Bitmap, contentTitle:String, description:String):Notification{
        return NotificationCompat.Builder(context, channelId!!)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(largeIcon)
            .setContentTitle(contentTitle)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle())
            .build()
    }

    companion object {
        @StringRes
        private val NULL_STRING_ID = 0
    }

}
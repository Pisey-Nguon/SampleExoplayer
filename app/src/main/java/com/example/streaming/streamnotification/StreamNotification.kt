package com.example.streaming.streamnotification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.streaming.R
import com.example.streaming.appconstant.AppConstant
import com.example.streaming.streamservice.DemoDownloadService
import com.google.android.exoplayer2.offline.Download

class StreamNotification(private val context: Context,private val download: Download) {
    private val mNotificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    // Start/Resume Download
    val pIntentStart = Intent(context, DemoDownloadService::class.java).apply {
        action = AppConstant.EXO_DOWNLOAD_ACTION_START
        data = download.request.uri
    }

    val pendingIntentStart = PendingIntent.getService(context, 100, pIntentStart, 0)


    // Pause Download
    val pIntentPause = Intent(context, DemoDownloadService::class.java).apply {
        action = AppConstant.EXO_DOWNLOAD_ACTION_PAUSE
        data = download.request.uri
    }
    val pendingIntentPause = PendingIntent.getService(context, 100, pIntentPause, 0)


    // Cancel Download
    val pIntentStartCancel = Intent(context, DemoDownloadService::class.java).apply {
        data = download.request.uri
        action = AppConstant.EXO_DOWNLOAD_ACTION_CANCEL
    }

    val pendingIntentCancel = PendingIntent.getService(context, 100, pIntentStartCancel, 0)
    val notificationDownloading = NotificationCompat.Builder(context, ConstantNotification.CHANNEL_ID).apply {
        setOngoing(true)
        setAutoCancel(false)
        color = ContextCompat.getColor(context, R.color.colorAccent)
        setContentTitle("Name of video")
        setContentText("0%")
        setOnlyAlertOnce(true)
        setSmallIcon(R.mipmap.ic_launcher) //                        .setGroup(GROUP_KEY_WORK_EMAIL)
        addAction(NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Pause", pendingIntentPause))
        addAction(NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Cancel", pendingIntentCancel))
    }

    val notificationCompleted = NotificationCompat.Builder(context, ConstantNotification.CHANNEL_ID).apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
        setContentTitle("Name of video")
        setContentText("Completed")
        setAutoCancel(false)
        setWhen(System.currentTimeMillis())
        setOnlyAlertOnce(true)
        setSmallIcon(R.mipmap.ic_launcher) //                        .setGroup(GROUP_KEY_WORK_EMAIL)
    }

    val notificationFailed = NotificationCompat.Builder(context, ConstantNotification.CHANNEL_ID).apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
        setContentTitle("Name of video")
        setContentText("Failed")
        setAutoCancel(false)
        setWhen(System.currentTimeMillis())
        setOnlyAlertOnce(true)
        setSmallIcon(R.mipmap.ic_launcher) //                        .setGroup(GROUP_KEY_WORK_EMAIL)
    }


    fun setPercentDownloaded(percentDownloaded:Int){
        Thread {
            notificationDownloading.setContentText(percentDownloaded.toString().plus(" %"))
            notificationDownloading.setProgress(100, percentDownloaded, false)
            mNotificationManagerCompat.notify(ConstantNotification.NOTIFICATION_DOWNLOADED_ID, notificationDownloading.build())
        }.start()
    }
    fun setStatusCompleted(){
        mNotificationManagerCompat.notify(ConstantNotification.NOTIFICATION_DOWNLOADED_ID,notificationCompleted.build())
    }
    fun setStatusFailed(){
        mNotificationManagerCompat.notify(ConstantNotification.NOTIFICATION_FAILED_ID,notificationFailed.build())
    }
}
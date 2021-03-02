package com.example.streaming.streamnotification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.streaming.streamservice.DemoDownloadService
import com.google.android.exoplayer2.offline.DownloadService

object StreamAction {

    fun actionPause(context: Context):NotificationCompat.Action{
        // Pause Download
        val intent = Intent(context, DemoDownloadService::class.java).apply {
            action = DownloadService.ACTION_PAUSE_DOWNLOADS
        }
        val pendingIntentPause = PendingIntent.getService(context, 100, intent, 0)

        return NotificationCompat.Action(null,"Pause",pendingIntentPause)
    }

    fun actionResume(context: Context):NotificationCompat.Action{
        // Resume Download
        val intent = Intent(context, DemoDownloadService::class.java).apply {
            action = DownloadService.ACTION_RESUME_DOWNLOADS
        }
        val pendingIntentResume = PendingIntent.getService(context, 100, intent, 0)

        return NotificationCompat.Action(null,"Resume",pendingIntentResume)
    }

    fun actionStop(context: Context,contentId:String):NotificationCompat.Action{
        // Stop Download
        val intent = Intent(context, DemoDownloadService::class.java).apply {
            action = DownloadService.ACTION_REMOVE_DOWNLOAD
            putExtra(DownloadService.KEY_CONTENT_ID,contentId)

        }
        val pendingIntentRemove = PendingIntent.getService(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Action(null,"Cancel",pendingIntentRemove)
    }
}
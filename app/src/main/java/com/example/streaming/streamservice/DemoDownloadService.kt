/*
 * Copyright (C) 2017 The Android Open Source Project
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
 */
package com.example.streaming.streamservice

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.example.streaming.R
import com.example.streaming.base.DemoUtil
import com.example.streaming.base.DemoUtil.DOWNLOAD_NOTIFICATION_CHANNEL_ID
import com.example.streaming.mediautils.MediaConverter
import com.example.streaming.streamnotification.StreamAction
import com.example.streaming.streamnotification.StreamNT
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util

/** A service for downloading media.  */
class DemoDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    R.string.exo_download_notification_channel_name,  /* channelDescriptionResourceId= */
    0
) {
    override fun getDownloadManager(): DownloadManager {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        val downloadManager: DownloadManager = DemoUtil.getDownloadManager( /* context= */this)
        val downloadNotificationHelper: StreamNT = DemoUtil.getDownloadNotificationHelper( /* context= */this)
        downloadManager.addListener(
            TerminalStateNotificationHelper(
                this, downloadNotificationHelper, FOREGROUND_NOTIFICATION_ID + 1
            )
        )

        return downloadManager
    }

    override fun getScheduler(): PlatformScheduler? {
        return if (Util.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else null
    }

    override fun getForegroundNotification(downloads: List<Download>): Notification {
        if (downloadManager.currentDownloads.size>0){
            Log.d("percentage","percent "+ downloadManager.currentDownloads[0].percentDownloaded)
            Log.d("percentage","bytes "+ downloadManager.currentDownloads[0].bytesDownloaded)
        }
        val mNotificationManagerCompat = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mNotification = DemoUtil.getDownloadNotificationHelper( /* context= */this)
            .buildProgressNotification( /* context= */
                this,
                R.mipmap.ic_launcher,
                StreamAction.actionStop(this,contentId = if (downloads.isNotEmpty()) downloads[0].request.id else ""),
                null,
                downloads
            )
        mNotificationManagerCompat.notify(FOREGROUND_NOTIFICATION_ID,mNotification)
        return mNotification
    }

    /**
     * Creates and displays notifications for downloads when they complete or fail.
     *
     *
     * This helper will outlive the lifespan of a single instance of [DemoDownloadService].
     * It is static to avoid leaking the first [DemoDownloadService] instance.
     */
    private class TerminalStateNotificationHelper(private val context: Context, private val notificationHelper: StreamNT, private var firstNotificationId: Int) : DownloadManager.Listener {

        override fun onDownloadChanged(downloadManager: DownloadManager, download: Download, finalException: Exception?) {
            val notification: Notification = when (download.state) {
                Download.STATE_COMPLETED -> {
                    notificationHelper.showStatusDownload(largeIcon = MediaConverter.getBitmapFromVectorDrawable(context,R.drawable.thumbnail)!!,contentTitle = context.resources.getString(R.string.exo_download_completed),description = download.request.id)
                }

                Download.STATE_FAILED -> {
                    notificationHelper.showStatusDownload(largeIcon = MediaConverter.getBitmapFromVectorDrawable(context,R.drawable.thumbnail)!!,contentTitle = context.resources.getString(R.string.exo_download_completed),description = download.request.id)
                }
//                Download.STATE_REMOVING -> {
//                    null
//                }
//                Download.STATE_RESTARTING -> {
//                    return
//                }
//                Download.STATE_STOPPED -> {
//                    return
//                }
                else -> return
            }
            NotificationUtil.setNotification(context, firstNotificationId++, notification)
        }
    }

    companion object {
        private const val JOB_ID = 1
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }
}

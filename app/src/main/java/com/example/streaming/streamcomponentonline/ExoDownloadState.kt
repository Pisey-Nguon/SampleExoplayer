package com.example.streaming.streamcomponentonline

/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 30/05/20, 12:25 PM.
 */
enum class ExoDownloadState(val value: String) {
    DOWNLOAD_START("Start Download"), DOWNLOAD_PAUSE("Pause Download"), DOWNLOAD_RESUME("Resume Download"), DOWNLOAD_COMPLETED(
        "Downloaded"
    ),
    DOWNLOAD_RETRY("Retry Download"), DOWNLOAD_QUEUE("Download In Queue");

}
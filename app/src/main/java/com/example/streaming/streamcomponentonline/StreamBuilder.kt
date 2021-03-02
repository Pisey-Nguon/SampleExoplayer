package com.example.streaming.streamcomponentonline

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

object StreamBuilder {

    fun buildVideoMediaSource(uri: Uri, dataSourceFactory: DataSource.Factory): MediaSource? {
        return buildVideoMediaSource(uri,dataSourceFactory, null)
    }

    private fun buildVideoMediaSource(uri: Uri, dataSourceFactory: DataSource.Factory, overrideExtension: String?): MediaSource? {
        return when (@C.ContentType val type = Util.inferContentType(uri, overrideExtension)) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_OTHER -> ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }
    fun buildSubtitleMediaSource(uri:Uri,dataSourceFactory: DataSource.Factory):MediaSource{
        val factory = SingleSampleMediaSource.Factory(dataSourceFactory)
        val subtitleFormat = Format.createTextSampleFormat(
                null,
                MimeTypes.APPLICATION_SUBRIP,
                C.SELECTION_FLAG_DEFAULT,  // Selection flags for the track.
                null)

        return factory.createMediaSource(uri, subtitleFormat, C.TIME_UNSET)
    }
}
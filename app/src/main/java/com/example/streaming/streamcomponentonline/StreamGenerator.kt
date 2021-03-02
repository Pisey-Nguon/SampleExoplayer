package com.example.streaming.streamcomponentonline

import com.google.android.exoplayer2.trackselection.TrackSelectionArray

object StreamGenerator {
    fun TrackSelectionArray.listExoHeightResolution(): ArrayList<Int> {
        val reference = ArrayList<Int>()
        val sizeTracking = this[0]!!.length()

        for (i in 0 until sizeTracking) {
            reference.add(this[0]?.getFormat(i)?.height ?: 360)
        }
        return reference
    }

    fun TrackSelectionArray.listExoBitrate(): ArrayList<Int> {
        val reference = ArrayList<Int>()
        val sizeTracking = this[0]!!.length()

        for (i in 0 until sizeTracking) {
            reference.add(this[0]?.getFormat(i)?.bitrate ?: 1000)
        }
        return reference
    }
}
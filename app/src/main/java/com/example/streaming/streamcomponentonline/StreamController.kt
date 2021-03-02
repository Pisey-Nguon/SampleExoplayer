package com.example.streaming.streamcomponentonline

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.upstream.DefaultAllocator

object StreamController {
    fun getDefaultController():DefaultLoadControl{
        val defaultAllocator = DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE)
        return DefaultLoadControl()
    }
}
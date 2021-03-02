package com.example.streaming.streamcomponentonline

object ExtensionChecker {
    fun String.extension():String {
        return this.substring(this.lastIndexOf(".") + 1)
    }

    fun String.isMp4():Boolean{
        return this.extension() == "mp4"
    }

    fun String.isM3U8():Boolean{
        return this.extension() == "m3u8"
    }

    fun String.isMov():Boolean{
        return this.extension() == "mov"
    }

    fun String.isVideo():Boolean{
        return this.isMp4() || this.isM3U8() || this.isMov()
    }

    fun String.isNotVideo():Boolean{
        return !this.isMp4() && !this.isM3U8() && !this.isMov()
    }
}
package com.example.streaming.mediautils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat


object MediaConverter {
    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
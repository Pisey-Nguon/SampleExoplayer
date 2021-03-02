package com.example.streaming.streamcomponentonline

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(
    private val view: View,
    private val toHeight: Int,
    private val fromHeight: Int,
    private val toWidth: Int,
    private val fromWidth: Int,
    duration: Long
) : Animation() {

    init {
        this.duration = duration
    }

    override fun applyTransformation(
        interpolatedTime: Float,
        t: Transformation?
    ) {
        val height = (toHeight - fromHeight) * interpolatedTime + fromHeight
        val width = (toWidth - fromWidth) * interpolatedTime + fromWidth
        val layoutParams = view.layoutParams
        layoutParams.height = height.toInt()
        layoutParams.width = width.toInt()
        view.requestLayout()
    }
}
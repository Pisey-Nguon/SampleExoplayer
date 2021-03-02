package com.example.streaming.streamcomponentonline

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup


object DistantConverter {
    private fun Float.dip2px(context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    fun setAnimationHeight(
        view: View,
        toHeight:Int
    ) {
        val anim = ValueAnimator.ofInt(view.measuredHeight, toHeight)
        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = `val`
            view.layoutParams = layoutParams
        }
        anim.duration = 500
        anim.start()
    }
}
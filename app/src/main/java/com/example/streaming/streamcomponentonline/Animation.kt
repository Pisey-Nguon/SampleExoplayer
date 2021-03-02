package com.example.streaming.streamcomponentonline

import android.animation.Animator
import android.view.View

object Animation {

    private lateinit var mAnimationListener: AnimateListener
     fun animateGoneView(view: View,callBack: AnimateListener?){
         if (callBack != null) {
             mAnimationListener =callBack
         }
        view.animate().alpha(0.0f)!!.setListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                view.visibility= View.GONE
                if (callBack!=null){
                    mAnimationListener.onAnimationEnd()
                }
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {
                if (callBack!=null){
                    mAnimationListener.onAnimationStart()
                }
            }

        }).duration=200
    }

    fun animateVisibleView(view: View,callBack: AnimateListener?){
        if (callBack != null) {
            mAnimationListener =callBack
        }
        view.animate().alpha(1.0f).setListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                if (callBack!=null){
                    mAnimationListener.onAnimationEnd()
                }
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {
                view.visibility=View.VISIBLE
                if (callBack!=null){
                    mAnimationListener.onAnimationStart()
                }
            }

        }).duration=200
    }

    interface AnimateListener{
        fun onAnimationStart(){}
        fun onAnimationEnd(){}
    }

}
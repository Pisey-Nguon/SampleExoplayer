package com.example.streaming.streamcomponentoffline

import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.streaming.R
import com.example.streaming.activity.OfflineVideoActivity
import com.example.streaming.databinding.ActivityOfflineVideoBinding
import com.example.streaming.streamcomponentonline.*

class OfflineViewHelper(private val activity:OfflineVideoActivity,private val mViewBinding: ActivityOfflineVideoBinding) {
    private val btnPlay = activity.findViewById<ImageButton>(R.id.btnPlay)
    private val btnSetting = activity.findViewById<ImageButton>(R.id.btnSetting)
    private val loadingOffline = activity.findViewById<ProgressBar>(R.id.loadingOffline)

    private val doubleRightClickListener = object: DoubleClickListener(){
        override fun onDoubleClick() {

        }

        override fun onSingleClick() {
            actionExoSingleClick()
        }

    }
    private val doubleLeftClickListener = object: DoubleClickListener(){
        override fun onDoubleClick() {

        }

        override fun onSingleClick() {
            actionExoSingleClick()
        }

    }

    fun actionExoSingleClick(){
        if (mViewBinding.playerView.isControllerVisible){
            mViewBinding.playerView.hideController()
        }else{
            mViewBinding.playerView.showController()
        }
    }
    fun setupUIButtonPlay(activity: OfflineVideoActivity, exoPlayState: ExoPlayState){
        activity.runOnUiThread {
            when(exoPlayState){
                ExoPlayState.EXO_PLAY -> {
                    btnPlay.show()
                    Animation.animateGoneView(loadingOffline,null)
                    btnPlay.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext,R.drawable.ic_play))
                }
                ExoPlayState.EXO_PAUSE -> {
                    btnPlay.show()
                    Animation.animateGoneView(loadingOffline,null)
                    btnPlay.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext,R.drawable.ic_pause))
                }
                ExoPlayState.EXO_REPLAY -> {
                    btnPlay.show()
                    Animation.animateGoneView(loadingOffline,null)
                    btnPlay.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext,R.drawable.ic_replay))
                }
                ExoPlayState.EXO_HIDE -> {
                    btnPlay.hide()
                    Animation.animateVisibleView(loadingOffline,null)
                }
            }
        }
    }
    fun initComponentClick(listener: View.OnClickListener){
        btnPlay.setOnClickListener(listener)
        btnSetting.setOnClickListener(listener)
    }

}
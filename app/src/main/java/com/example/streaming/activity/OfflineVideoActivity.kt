package com.example.streaming.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.streaming.R
import com.example.streaming.databinding.ActivityOfflineVideoBinding
import com.example.streaming.streamcomponentoffline.OfflineLogicHelper
import com.example.streaming.streamcomponentoffline.OfflineViewHelper

class OfflineVideoActivity : AppCompatActivity(),View.OnClickListener {
    val TAG = "OfflineVideoActivity"
    lateinit var mViewBinding:ActivityOfflineVideoBinding
    lateinit var mLogicHelper : OfflineLogicHelper
    lateinit var mViewHelper: OfflineViewHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_video)
        mViewBinding = ActivityOfflineVideoBinding.inflate(layoutInflater)
        val view = mViewBinding.root
        setContentView(view)
        mLogicHelper = OfflineLogicHelper(this)
        mViewHelper = OfflineViewHelper(activity = this,mViewBinding = mViewBinding)
        mViewHelper.initComponentClick(this)
        mLogicHelper.initExoplayer()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnPlay -> mLogicHelper.startPlayer()
            R.id.btnSetting ->{}


        }
    }
}
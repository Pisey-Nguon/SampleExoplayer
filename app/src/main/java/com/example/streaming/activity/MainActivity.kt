package com.example.streaming.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.streaming.R
import com.example.streaming.base.DemoUtil
import com.example.streaming.databinding.ActivityMainBinding
import com.example.streaming.streamcomponentonline.StreamLogicHelper
import com.example.streaming.streamcomponentonline.StreamViewHelper
import java.net.CookieManager


class MainActivity : AppCompatActivity(),View.OnClickListener{

    private val TAG = "MainActivity"
    protected var DEFAULT_COOKIE_MANAGER: CookieManager? = null

    lateinit var mLogicHelper : StreamLogicHelper
    lateinit var mViewHelper: StreamViewHelper
    lateinit var mViewBinding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mViewBinding.root
        setContentView(view)
        mLogicHelper = StreamLogicHelper(this)
        mLogicHelper.requestPermission()
        mViewHelper = StreamViewHelper(activity = this, mViewBinding = mViewBinding)
        mViewHelper.initComponentClick(listener = this)
        mViewHelper.initComponentMirror()
        mLogicHelper.initComponentStream()

    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnPlay -> mLogicHelper.startPlayer()

            R.id.btnSetting -> {
            }

            R.id.btnOpenPlay -> startActivity(Intent(this, OfflineVideoActivity::class.java))
            R.id.btnDownload -> {
                mLogicHelper.fetchDownloadOptions(mLogicHelper.streamUrl)
            }
            R.id.btnPause -> {
                mLogicHelper.pauseDownload(
                    DemoUtil.getDownloadTracker(this.applicationContext).getDownloadRequest(
                        Uri.parse(mLogicHelper.streamUrl)
                    ) ?: return
                )
            }
            R.id.btnResume -> {
                mLogicHelper.resumeDownload(
                    DemoUtil.getDownloadTracker(this.applicationContext).getDownloadRequest(
                        Uri.parse(
                            mLogicHelper.streamUrl
                        )
                    )
                        ?: return
                )
            }
            mViewBinding.btnMirrorPause.id -> {
            }
            mViewBinding.btnMirrorPlay.id -> {
//                mViewHelper.castVideoToScreen()
            }
            mViewBinding.btnSearchTv.id -> {
//                mViewHelper.showTVDevices()
            }
            mViewBinding.btnDisconnectTv.id ->{
//                mViewHelper.disconnectTV()
            }
            R.id.btnMirror ->{
                mLogicHelper.showTVDevices()
//                mViewHelper.showTVDevices()
            }
        }
    }

}
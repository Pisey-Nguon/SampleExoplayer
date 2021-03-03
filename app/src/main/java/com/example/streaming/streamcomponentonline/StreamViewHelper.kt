package com.example.streaming.streamcomponentonline

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.mediarouter.app.MediaRouteButton
import com.connectsdk.core.MediaInfo
import com.connectsdk.core.SubtitleInfo
import com.connectsdk.discovery.DiscoveryManager
import com.connectsdk.discovery.DiscoveryProvider
import com.connectsdk.service.DeviceService
import com.connectsdk.service.capability.MediaPlayer
import com.connectsdk.service.command.ServiceCommandError
import com.example.streaming.R
import com.example.streaming.activity.MainActivity
import com.example.streaming.databinding.ActivityMainBinding
import com.example.streaming.mirrorcomponent.dialog.MirrorController
import com.example.streaming.streamdialog.ItemListDownloadDialogFragment
import com.google.android.exoplayer2.ui.PlayerView

class StreamViewHelper(
    private val activity: MainActivity,
    private val mViewBinding: ActivityMainBinding
) {

    private val toolbar = activity.findViewById<Toolbar>(R.id.exoToolbar)
    private val btnDownload = activity.findViewById<Button>(R.id.btnDownload)
    private val btnPause = activity.findViewById<Button>(R.id.btnPause)
    private val btnResume = activity.findViewById<Button>(R.id.btnResume)
    private val btnOpenPlay = activity.findViewById<Button>(R.id.btnOpenPlay)
    private val btnPlay = activity.findViewById<ImageButton>(R.id.btnPlay)
    private val btnSetting = activity.findViewById<Button>(R.id.btnSetting)

    private val btnSeekForward = activity.findViewById<ImageButton>(R.id.btnForward)
    private val btnSeekRewind = activity.findViewById<ImageButton>(R.id.btnRewind)
    private val loadingExoplayer = activity.findViewById<ProgressBar>(R.id.loadingExoplayer)
    private val mMediaRouteButton = activity.findViewById<MediaRouteButton>(R.id.media_route_button)

    private val mMirrorController = MirrorController(activity = activity)

//        val streamUrl = "https://milio-media.s3-ap-southeast-1.amazonaws.com/another-stream/1575271849439-649b8d93c8416bf0e0d269655b40b52f8987e918277699e973a265facc7862af.m3u8"
    val streamUrl = "https://d2cqvl54b1gtkt.cloudfront.net/PRODUCTION/5d85da3fa81ada4c66211a07/media/post/video/1613121797855-35ad0c18-ba1a-4cb1-97b9-fe307269ecbc/1613121797856-7b6ee2437302d02c26d14d325fd7f56a0ce51591e690.mp4"
    val subtitleUrl = "https://cdn.filesend.jp/private/OCoJQeyjKgKvBlqBsRlQFMkx1SYBeREmssE5I1t5QJzlcqSNGDj4Ipct_wq8qomh/The.Wandering.Earth.2019.720p.BluRay.x264.%5BYTS.MX%5D-English.srt"
    val thumbnail = "https://d2cqvl54b1gtkt.cloudfront.net/PRODUCTION/5d85da3fa81ada4c66211a07/media/post/thumbnail/1613121797856-7b6ee2437302d02c26d14d325fd7f56a0ce51591e690.jpeg"

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


    fun initComponentClick(listener: View.OnClickListener){
        btnDownload.setOnClickListener(listener)
        btnResume.setOnClickListener(listener)
        btnPause.setOnClickListener(listener)
        btnOpenPlay.setOnClickListener(listener)
        btnPlay.setOnClickListener(listener)
        btnSetting.setOnClickListener(listener)
        mMediaRouteButton.setOnClickListener(listener)
        mViewBinding.btnMirrorPause.setOnClickListener(listener)
        mViewBinding.btnMirrorPlay.setOnClickListener(listener)
        mViewBinding.btnSearchTv.setOnClickListener(listener)
        mViewBinding.btnDisconnectTv.setOnClickListener(listener)

        btnSeekForward.setOnClickListener(listener)
        btnSeekRewind.setOnClickListener(listener)
    }


    fun initComponentMirror(){
        val mDiscoveryManager = DiscoveryManager.getInstance()
//        mDiscoveryManager.registerDefaultDeviceTypes()
        mDiscoveryManager.setPairingLevel(DiscoveryManager.PairingLevel.ON)
        try {
//             //AirPlay
//            mDiscoveryManager?.registerDeviceService(Class.forName("com.connectsdk.service.AirPlayService") as Class<DeviceService?>,
//                Class.forName("com.connectsdk.discovery.provider.ZeroconfDiscoveryProvider") as Class<DiscoveryProvider?>)
             //webOS SSAP (Simple Service Access Protocol)
            mDiscoveryManager?.registerDeviceService(Class.forName("com.connectsdk.service.WebOSTVService") as Class<DeviceService?>,
                    Class.forName("com.connectsdk.discovery.provider.SSDPDiscoveryProvider") as Class<DiscoveryProvider?>)
            // DLNA
            mDiscoveryManager?.registerDeviceService(Class.forName("com.connectsdk.service.DLNAService") as Class<DeviceService?>,
                    Class.forName("com.connectsdk.discovery.provider.SSDPDiscoveryProvider") as Class<DiscoveryProvider?>)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        DiscoveryManager.getInstance().start()



    }

    fun setupUIButtonPlay(activity: MainActivity, exoPlayState: ExoPlayState){
        activity.runOnUiThread {
            when(exoPlayState){
                ExoPlayState.EXO_PLAY -> {
                    Animation.animateGoneView(loadingExoplayer, null)
                    Animation.animateVisibleView(btnPlay, null)
                    btnPlay.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity.applicationContext,
                            R.drawable.ic_play
                        )
                    )
                }
                ExoPlayState.EXO_PAUSE -> {
                    Animation.animateGoneView(loadingExoplayer, null)
                    Animation.animateVisibleView(btnPlay, null)
                    btnPlay.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity.applicationContext,
                            R.drawable.ic_pause
                        )
                    )
                }
                ExoPlayState.EXO_REPLAY -> {
                    Animation.animateGoneView(loadingExoplayer, null)
                    Animation.animateVisibleView(btnPlay, null)
                    btnPlay.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity.applicationContext,
                            R.drawable.ic_replay
                        )
                    )
                }
                ExoPlayState.EXO_HIDE -> {
                    Animation.animateGoneView(btnPlay, null)
                    Animation.animateVisibleView(loadingExoplayer, null)
                }
            }
        }
    }

    fun actionExoSingleClick(){
        if (mViewBinding.playerView.isControllerVisible){
            mViewBinding.playerView.hideController()
        }else{
            mViewBinding.playerView.showController()
        }
    }

    fun setPlayerHeight(height: Int){
        val params= mViewBinding.playerView.layoutParams
        params.height = height
        mViewBinding.playerView.layoutParams = params

    }
    fun getPlayerWidth():Int{
        return mViewBinding.playerView.measuredWidth
    }
    fun getPlayerHeight():Int{
        return mViewBinding.playerView.measuredHeight
    }

    fun getPlayerView():PlayerView{
        return mViewBinding.playerView
    }

    fun showListDownload(itemListDownload: ArrayList<Int>){

        val bottomSheet = ItemListDownloadDialogFragment.newInstance(itemListDownload)
        bottomSheet.registerCallBack(object : ItemListDownloadDialogFragment.ItemClickListener {
            override fun onItemClick(itemDownload: Int, index: Int) {

            }
        })
        bottomSheet.show(activity.supportFragmentManager, "")
    }

    fun showTVDevices(){
        mMirrorController.showListDevices()
    }
    fun disconnectTV(){
        mMirrorController.disconnectTv()
    }
    fun checkCapability():String{
        return if (mMirrorController.getTv()?.hasCapability(MediaPlayer.Subtitle_SRT) == true)
            subtitleUrl
        else
                ""
    }

    fun castVideoToScreen(){
        val subtitleBuilder: SubtitleInfo.Builder?
        subtitleBuilder = SubtitleInfo.Builder(checkCapability())
        subtitleBuilder.setLabel("English").setLanguage("en")

        val mediaInfo: MediaInfo = MediaInfo.Builder(streamUrl, "video/mp4")
            .setTitle("Media Mirror")
            .setDescription("One SDK Eight Media Platforms")
            .setIcon(thumbnail)
            .setSubtitleInfo(subtitleBuilder.build())
            .build()
        mMirrorController.castVideoToScreen(mediaInfo = mediaInfo,shouldLoop = false,
            launchListener = object:
            MediaPlayer.LaunchListener{
            override fun onError(error: ServiceCommandError?) {
                error
            }

            override fun onSuccess(success: MediaPlayer.MediaLaunchObject?) {
                success
            }

        })

    }


}
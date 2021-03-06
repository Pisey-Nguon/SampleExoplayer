package com.example.streaming.mirrorcomponent.dialog

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.connectsdk.core.MediaInfo
import com.connectsdk.core.SubtitleInfo
import com.connectsdk.device.ConnectableDevice
import com.connectsdk.device.ConnectableDeviceListener
import com.connectsdk.service.DeviceService
import com.connectsdk.service.DeviceService.PairingType
import com.connectsdk.service.capability.*
import com.connectsdk.service.capability.listeners.ResponseListener
import com.connectsdk.service.command.ServiceCommandError
import com.connectsdk.service.sessions.LaunchSession

class MirrorController(private val activity: AppCompatActivity) {
    var mTV: ConnectableDevice? = null
    var launcher: Launcher? = null
    var mediaPlayer: MediaPlayer? = null
    var mediaControl: MediaControl? = null
    var tvControl: TVControl? = null
    var volumeControl: VolumeControl? = null
    var toastControl: ToastControl? = null
    var mouseControl: MouseControl? = null
    var textInputControl: TextInputControl? = null
    var powerControl: PowerControl? = null
    var externalInputControl: ExternalInputControl? = null
    var keyControl: KeyControl? = null
    var webAppLauncher: WebAppLauncher? = null
    var launchSession: LaunchSession? = null
    val mTVDevicesDialog = TVDevicesDialog.instance(activity)

    //        val streamUrl = "https://milio-media.s3-ap-southeast-1.amazonaws.com/another-stream/1575271849439-649b8d93c8416bf0e0d269655b40b52f8987e918277699e973a265facc7862af.m3u8"
    val streamUrl = "https://d2cqvl54b1gtkt.cloudfront.net/PRODUCTION/5d85da3fa81ada4c66211a07/media/post/video/1613121797855-35ad0c18-ba1a-4cb1-97b9-fe307269ecbc/1613121797856-7b6ee2437302d02c26d14d325fd7f56a0ce51591e690.mp4"
    val subtitleUrl = "https://cdn.filesend.jp/private/OCoJQeyjKgKvBlqBsRlQFMkx1SYBeREmssE5I1t5QJzlcqSNGDj4Ipct_wq8qomh/The.Wandering.Earth.2019.720p.BluRay.x264.%5BYTS.MX%5D-English.srt"
    val thumbnail = "https://d2cqvl54b1gtkt.cloudfront.net/PRODUCTION/5d85da3fa81ada4c66211a07/media/post/thumbnail/1613121797856-7b6ee2437302d02c26d14d325fd7f56a0ce51591e690.jpeg"

    /**
     * deviceListener is use full for detect something changed
     */
    private val deviceListener: ConnectableDeviceListener = object : ConnectableDeviceListener {
        override fun onPairingRequired(
            device: ConnectableDevice,
            service: DeviceService,
            pairingType: PairingType
        ) {
            when (pairingType) {
                PairingType.FIRST_SCREEN -> {
                    mTVDevicesDialog.showConfirmPairCode()

                }
                PairingType.PIN_CODE, PairingType.MIXED -> {
                    mTVDevicesDialog.showEnterPairCode(itemClickListener = itemClickListener)
                }
                PairingType.NONE -> {
                }
                else -> {
                }
            }
        }

        override fun onConnectionFailed(device: ConnectableDevice, error: ServiceCommandError) {
            connectEnded(device = device)
            Toast.makeText(activity.applicationContext, "Connect Failed", Toast.LENGTH_LONG).show()
        }

        override fun onDeviceReady(device: ConnectableDevice) {
            mTVDevicesDialog.dismissEnterPairCode()
            mTVDevicesDialog.dismissConfirmPairCode()
            registerSuccess(mTv = mTV)
//            castVideoToScreen()
            castThumbnailToScreen(object:MediaPlayer.LaunchListener{
                override fun onError(error: ServiceCommandError?) {
                    error
                }

                override fun onSuccess(`object`: MediaPlayer.MediaLaunchObject?) {
                    `object`
                }

            })
            Toast.makeText(activity.applicationContext, "Connect Success", Toast.LENGTH_LONG).show()
        }

        override fun onDeviceDisconnected(device: ConnectableDevice) {
            Toast.makeText(activity.applicationContext, "Disconnected Device", Toast.LENGTH_LONG).show()

        }

        override fun onCapabilityUpdated(
            device: ConnectableDevice,
            added: List<String>,
            removed: List<String>
        ) {

        }
    }

    private val itemClickListener = object : TVDevicesDialog.OnItemClickListener {
        override fun onConnectTV(device: ConnectableDevice?) {
            mTV = device
            mTV?.setPairingType(null)
            mTV?.addListener(deviceListener)
            mTV?.connect()




        }

        override fun onEnterPairingCode(value: String) {
            mTV?.sendPairingKey(value)
        }

        override fun onNotConfirmParingCode() {

        }

    }
    fun registerSuccess(mTv: ConnectableDevice?){
        launcher = mTv?.getCapability(Launcher::class.java)
        mediaPlayer = mTv?.getCapability(MediaPlayer::class.java)
        mediaControl = mTv?.getCapability(MediaControl::class.java)
        tvControl = mTv?.getCapability(TVControl::class.java)
        volumeControl = mTv?.getCapability(VolumeControl::class.java)
        toastControl = mTv?.getCapability(ToastControl::class.java)
        textInputControl = mTv?.getCapability(TextInputControl::class.java)
        mouseControl = mTv?.getCapability(MouseControl::class.java)
        externalInputControl = mTv?.getCapability(ExternalInputControl::class.java)
        powerControl = mTv?.getCapability(PowerControl::class.java)
        keyControl = mTv?.getCapability(KeyControl::class.java)
        webAppLauncher = mTv?.getCapability(WebAppLauncher::class.java)
    }
    @JvmName("getMediaPlayer1")
    fun getMediaPlayer():MediaPlayer?{
        return mediaPlayer
    }
    fun getTv():ConnectableDevice?{
        return mTV
    }
    fun showListDevices(){
        mTVDevicesDialog.showDevices(itemClickListener = itemClickListener)
    }

    fun isConnected():Boolean?{
        return mTV?.isConnected

    }

    @JvmName("setLaunchSession1")
    fun setLaunchSession(launchSession: LaunchSession?){
        this.launchSession = launchSession
    }
    fun disconnectTv(){
        if (getMediaPlayer() != null) {
            if (launchSession != null) {
                launchSession?.close(object : ResponseListener<Any> {
                    override fun onError(error: ServiceCommandError?) {
                        Toast.makeText(
                                activity.applicationContext,
                                error?.message,
                                Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onSuccess(`object`: Any?) {
                        Toast.makeText(
                            activity.applicationContext,
                            "Disconnected Device",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
                launchSession = null
            }
        }
    }

    fun connectEnded(device: ConnectableDevice?) {
        mTVDevicesDialog.dismissConfirmPairCode()
        mTVDevicesDialog.dismissEnterPairCode()
        if (mTV?.isConnectable == false) {
            mTV!!.removeListener(deviceListener)
            mTV = null
        }
    }

    /**
     * castVideoToScreen use for implement video into TV
     */
    fun castVideoToScreen(
        mediaInfo: MediaInfo,
        shouldLoop: Boolean,
        launchListener: MediaPlayer.LaunchListener
    ){
        mediaPlayer?.playMedia(mediaInfo, shouldLoop, launchListener)
    }

    /**
     * castThumbnailToScreen use for show image like thumbnail sample before play media
     */
    fun castThumbnailToScreen(launchListener: MediaPlayer.LaunchListener){
        val mediaInfo: MediaInfo = MediaInfo.Builder(thumbnail, "image/jpeg")
            .setTitle("Media Mirror")
            .setDescription("One SDK Eight Media Platforms")
            .build()
        mediaPlayer?.playMedia(mediaInfo, false, launchListener)
    }
    private fun checkCapability():String{
        return if (getTv()?.hasCapability(MediaPlayer.Subtitle_SRT) == true)
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
        castVideoToScreen(mediaInfo = mediaInfo, shouldLoop = false,
                launchListener = object :
                        MediaPlayer.LaunchListener {
                    override fun onError(error: ServiceCommandError?) {
                        error
                    }

                    override fun onSuccess(success: MediaPlayer.MediaLaunchObject?) {
                        setLaunchSession(success?.launchSession)
                        success
                    }

                })

    }
    fun setVolume(value: Float){
        volumeControl?.setVolume(value, null)
    }
}
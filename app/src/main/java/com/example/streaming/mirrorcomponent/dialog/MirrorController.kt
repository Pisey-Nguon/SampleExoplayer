package com.example.streaming.mirrorcomponent.dialog

import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.connectsdk.core.MediaInfo
import com.connectsdk.device.ConnectableDevice
import com.connectsdk.device.ConnectableDeviceListener
import com.connectsdk.service.DeviceService
import com.connectsdk.service.DeviceService.PairingType
import com.connectsdk.service.capability.*
import com.connectsdk.service.command.ServiceCommandError

class MirrorController(activity: AppCompatActivity) {
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
    val mTVDevicesDialog = TVDevicesDialog.instance(activity)
    val thumbnail = "https://cdn.filesend.jp/private/t1bePrF9qz8j9D0bhEmBO6bYOmfKkjZhriVpYqY0gP1CvTXhH4MKb4s8PClERGW9/Samsung-screen-Mirroring.jpg"
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
            Toast.makeText(activity.applicationContext, error.message, Toast.LENGTH_LONG).show()
        }

        override fun onDeviceReady(device: ConnectableDevice) {
            mTVDevicesDialog.dismissEnterPairCode()
            mTVDevicesDialog.dismissConfirmPairCode()
            registerSuccess(mTv = mTV)
            castThumbnailToScreen(object :MediaPlayer.LaunchListener{
                override fun onError(error: ServiceCommandError?) {
                    if (error?.message == ""){

                    }
                    mTVDevicesDialog.showEnterPairCode(itemClickListener = itemClickListener)
                }

                override fun onSuccess(`object`: MediaPlayer.MediaLaunchObject?) {

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
        override fun onConnectTV(parent: AdapterView<*>?, positionDevices: Int) {
            mTV = parent?.getItemAtPosition(positionDevices) as ConnectableDevice
            mTV?.setPairingType(DeviceService.PairingType.PIN_CODE)
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

    fun disconnectTv(){
        if (mTV?.isConnected == true){
            mTV?.disconnect()
//            mTV?.removeListener(deviceListener)
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
    fun castVideoToScreen(mediaInfo: MediaInfo,shouldLoop:Boolean,launchListener: MediaPlayer.LaunchListener){
        mediaPlayer?.playMedia(mediaInfo,shouldLoop,launchListener)
    }
    fun castThumbnailToScreen(launchListener: MediaPlayer.LaunchListener){
        val mediaInfo: MediaInfo = MediaInfo.Builder(thumbnail, "image/jpeg")
            .setTitle("Media Mirror")
            .setDescription("One SDK Eight Media Platforms")
            .build()
        mediaPlayer?.playMedia(mediaInfo,false,launchListener)
    }
}
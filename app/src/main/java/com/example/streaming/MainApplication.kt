package com.example.streaming

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.connectsdk.discovery.DiscoveryManager
import com.connectsdk.service.DIALService

class MainApplication:MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        DIALService.registerApp("com.example.streaming")
        DiscoveryManager.init(applicationContext)
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
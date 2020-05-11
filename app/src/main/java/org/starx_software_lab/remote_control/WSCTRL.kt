package org.starx_software_lab.remote_control

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.provider.Settings
import android.util.Log


class WSCTRL() : IntentService("WSCTRL") {
    val tag = "Service"
    lateinit var server: Websocket
    @SuppressLint("HardwareIds")
    override fun onHandleIntent(intent: Intent?) {
        server = Websocket(1060)
        server.setID(Settings.Secure.getString(applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID))
        server.start()
        while (true) {
            Thread.sleep(10000000)
        }
    }



    override fun onDestroy() {
        Log.i("Destroy","Destroy")
        //server.stop()
        super.onDestroy()
    }
}
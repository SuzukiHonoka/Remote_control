package org.starx_software_lab.remote_control

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.Build
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
        server.connectionLostTimeout
        server.start()
        while (true) {
            //60*60*24*30 = 30 days
            Thread.sleep(2592000)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return super.onStartCommand(intent, flags, startId)
        }
        return Service.START_STICKY

    }
    override fun onDestroy() {
        Log.i("Destroy","Destroy")
        server.stop()
        super.onDestroy()
    }
}
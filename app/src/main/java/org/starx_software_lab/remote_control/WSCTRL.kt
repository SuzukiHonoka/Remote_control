package org.starx_software_lab.remote_control

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.provider.Settings


class WSCTRL() : IntentService("WSCTRL") {
    @SuppressLint("HardwareIds")
    override fun onHandleIntent(intent: Intent?) {
        val server = Websocket(1060)
        server.setID(Settings.Secure.getString(applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID))
        server.start()
    }
}
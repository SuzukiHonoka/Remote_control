package org.starx_software_lab.remote_control

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AUTOSTART : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (Util().getsp(context, "AUTOBOOT").toBoolean()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(Intent(context, WSCTRL::class.java))
                }
                context.startService(Intent(context, WSCTRL::class.java))
                popup(context, "受控服务已启动于端口1060。")
            }
        } else {
            popup(context, "已开机，受控未启动。")
        }

    }
}
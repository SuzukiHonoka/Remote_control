package org.starx_software_lab.remote_control

import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent

class Util {
    val tag = "Util"
    val cmd = "input keyevent "
    val and = "&"
    val su_c = "su $and"
    val simple_sd = "reboot -p"
    val simple_rt = "reboot"
    val shutdown = "am start -a com.android.internal.intent.action.REQUEST_SHUTDOWN"
    val reboot = "am start -a android.intent.action.REBOOT"
    val connect_fail = 0
    val connect_success = 1
    val connect_msg = 2
    val connect_close = 3
    val connect_id = 4
    val connect_adb_msg = 5
    val connect_adb_closed = 6
    val connect_adb_ok = 7

    fun sendtoUI(what: Int, objects: String?, ehr: Handler) {
        val tmpMSG = Message.obtain()
        tmpMSG.what = what
        if (objects != null) {
            tmpMSG.obj = objects
        }
        ehr.handleMessage(tmpMSG)
    }

    fun keyevent(key: String): String {
        val echo = when (key) {
            "up" -> mixCMD(KeyEvent.KEYCODE_DPAD_UP)
            "down" -> mixCMD(KeyEvent.KEYCODE_DPAD_DOWN)
            "left" -> mixCMD(KeyEvent.KEYCODE_DPAD_LEFT)
            "right" -> mixCMD(KeyEvent.KEYCODE_DPAD_RIGHT)
            "enter" -> mixCMD(KeyEvent.KEYCODE_ENTER)
            "back" -> mixCMD(KeyEvent.KEYCODE_BACK)
            "menu" -> mixCMD(KeyEvent.KEYCODE_MENU)
            "home" -> mixCMD(KeyEvent.KEYCODE_HOME)
            "vol+" -> mixCMD(KeyEvent.KEYCODE_VOLUME_UP)
            "vol-" -> mixCMD(KeyEvent.KEYCODE_VOLUME_DOWN)
            "mute" -> mixCMD(KeyEvent.KEYCODE_VOLUME_MUTE)
            "power" -> mixCMD(KeyEvent.KEYCODE_POWER)
            else -> ""
        }
        Log.i(tag, echo)
        return echo
    }

    private fun mixCMD(keycode: Int): String {
        return this.cmd + keycode.toString()
    }

}
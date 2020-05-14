package org.starx_software_lab.remote_control

import android.content.Context
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

    fun setsp(context: Context, section: String, data: String): Boolean {
        Log.i(tag, "SET Section: $section Value: $data")
        val sharedPref =
            context.getSharedPreferences("Setting", Context.MODE_PRIVATE) ?: return false
        with(sharedPref.edit()) {
            putString(section, data)
            apply()
            return true
        }
    }

    fun getsp(context: Context, section: String): String {
        Log.i(tag, "Get Section: $section")
        val sharedPref = context.getSharedPreferences("Setting", Context.MODE_PRIVATE) ?: return ""
        return sharedPref.getString(section, "")!!
    }

    fun isvalidIP(ip: String): Boolean {
        val sp = ip.split(".")
        if (sp.size == 4) {
            sp.forEach {
                val si = it.toInt()
                if ((si > 0) and (si <= 255)) {
                    return true
                }
                return false
            }
        }
        return false
    }

    fun isvalidPORT(port: String): Boolean {
        val p = port.toInt()
        return (p > 0) and (p <= 65535)
    }

}
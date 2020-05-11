package org.starx_software_lab.remote_control


import android.view.KeyEvent
import java.io.DataOutputStream

class Control {
    val cmd = "input keyevent "
    val shutdown = "am start -a com.android.internal.intent.action.REQUEST_SHUTDOWN"
    val reboot = "am start -a android.intent.action.REBOOT"
    val su = Runtime.getRuntime().exec("su")
    val ops = DataOutputStream(su.outputStream)

    fun performAction(action : String) {
        when (action) {
            "up" -> opsWrite(mixCMD(KeyEvent.KEYCODE_DPAD_UP))
            "down" -> opsWrite(mixCMD(KeyEvent.KEYCODE_DPAD_DOWN))
            "left" -> opsWrite(mixCMD(KeyEvent.KEYCODE_DPAD_LEFT))
            "right" -> opsWrite(mixCMD(KeyEvent.KEYCODE_DPAD_RIGHT))
            "enter" -> opsWrite(mixCMD(KeyEvent.KEYCODE_ENTER))
            "back" -> opsWrite(mixCMD(KeyEvent.KEYCODE_BACK))
            "menu" -> opsWrite(mixCMD(KeyEvent.KEYCODE_MENU))
        }
    }

    fun shutdown() {
        opsWrite(this.shutdown)
    }

    fun reboot() {
        opsWrite(this.reboot)
    }

    fun exit() {
        opsWrite("exit")
    }
    private fun mixCMD(keycode: Int): String {
        return this.cmd + keycode.toString()
    }

    private fun opsWrite(cmd: String) {
        this.ops.writeBytes(cmd)
        this.ops.flush()
    }

}
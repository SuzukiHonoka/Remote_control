package org.starx_software_lab.remote_control


import android.util.Log
import java.io.DataOutputStream

class Control {
    val tag = "Control"


    val su = Runtime.getRuntime().exec("su")
    val ops = DataOutputStream(su.outputStream)

    fun performAction(action : String) {
        Log.i(tag,action)
        opsWrite(Util().keyevent(action))
    }

    fun shutdown() {
        opsWrite(Util().shutdown)
    }

    fun reboot() {
        opsWrite(Util().reboot)
    }

    fun exit() {
        opsWrite("exit")
    }

    private fun opsWrite(cmd: String) {
        this.ops.writeBytes(cmd + "\n")
        this.ops.flush()
    }

}
package org.starx_software_lab.remote_control

import android.content.Context
import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream


class Adb() {
    val tag = "adb"
    val secp = "/adb"
    val arg1 = " connect "
    lateinit var ip: String
    lateinit var port: String
    lateinit var context: Context
    lateinit var path: String
    lateinit var process: Process
    lateinit var dataInputStream: DataInputStream
    lateinit var dataOutputStream: DataOutputStream

    fun setup(context: Context, path: String, ip: String, port: String) {
        this.context = context
        this.path = path
        this.ip = ip
        this.port = port
    }

    fun start() {
        Thread {
            if (!this.binaryexist()) {
                initfiles()
            }
            Log.i(tag, "Starting Process.")
            process = Runtime.getRuntime().exec("$path$secp$arg1$ip:$port")
            dataOutputStream = DataOutputStream(process.outputStream)
            dataInputStream = DataInputStream(process.inputStream)
            Log.i(tag, "Got streams without error!")
        }.start()
    }


    fun binaryexist(): Boolean {
        return File(path + secp).exists()
    }

    fun initfiles() {
        val opt = FileOutputStream(path + secp)
        context.assets.open("adb").use {
            it.copyTo(opt)
        }
        File(path + secp).setExecutable(true)
        Log.i(tag, "Adb file inited.")
        opt.close()
    }


}
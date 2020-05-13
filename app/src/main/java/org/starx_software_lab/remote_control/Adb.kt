package org.starx_software_lab.remote_control

import android.util.Log
import com.cgutman.adblib.AdbBase64
import com.cgutman.adblib.AdbConnection
import com.cgutman.adblib.AdbCrypto
import com.cgutman.adblib.AdbStream
import org.apache.commons.codec.binary.Base64
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.Socket
import java.net.UnknownHostException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException


class ADB() {
    val tag = "ADB"
    //
    lateinit var ip: String
    lateinit var port: String
    lateinit var path: String
    lateinit var mhandler: android.os.Handler
    //
    lateinit var adb: AdbConnection;
    lateinit var sock: Socket;
    lateinit var crypto: AdbCrypto;
    lateinit var stream: AdbStream
    var last_msg = ""
    var msg = ""
    var rec = 0

    fun getBase64Impl(): AdbBase64? {
        return AdbBase64 { arg0 -> Base64.encodeBase64String(arg0) }
    }

    private fun setupCrypto(): AdbCrypto? {
        val pub = File("$path/pub.key")
        val priv = File("$path/priv.key")
        var c: AdbCrypto? = null
        // Try to load a key pair from the files
        if (pub.exists() && priv.exists()) {
            c = try {
                AdbCrypto.loadAdbKeyPair(this.getBase64Impl(), priv, pub)
            } catch (e: IOException) { // Failed to read from file
                null
            } catch (e: InvalidKeySpecException) { // Key spec was invalid
                null
            } catch (e: NoSuchAlgorithmException) { // RSA algorithm was unsupported with the crypo packages available
                null
            }
        }
        if (c == null) { // We couldn't load a key, so let's generate a new one
            c = AdbCrypto.generateAdbKeyPair(this.getBase64Impl())
            // Save it
            c.saveAdbKeyPair(priv, pub)
            println("Generated new keypair")
        } else {
            println("Loaded existing keypair")
        }
        return c
    }

    fun setup(
        ip: String,
        port: String,
        path: String,
        handler: android.os.Handler
    ) {
        this.ip = ip
        this.port = port
        this.path = path
        this.mhandler = handler
    }

    @ExperimentalStdlibApi
    fun start() {

        // Setup the crypto object required for the AdbConnection
        Thread {
            try {
                crypto = this.setupCrypto()!!;
                Log.i(tag, "Crypto OK")
            } catch (e: NoSuchAlgorithmException) {
                Log.i(tag, e.printStackTrace().toString())
            } catch (e: InvalidKeySpecException) {
            } catch (e: IOException) {
            }
            try {
                sock = Socket(ip, port.toInt())
                Log.i(tag, "Socket OK")
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                adb = AdbConnection.create(sock, crypto)
                Log.i(tag, "ADB connect OK")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                var success = false
                Thread {
                    // timeout check
                    Thread.sleep(5000)
                    if (!success) {
                        adb.close()
                        adb.connect()
                        stream = adb.open("shell:")
                        Log.i(tag, "Timeout!!")
                    }
                }
                adb.connect()
                stream = adb.open("shell:")
                Log.i(tag, "ADB open OK")
                success = true
                Util().sendtoUI(Util().connect_adb_ok, "", this.mhandler)
                Thread {
                    try {
                        while (!stream.isClosed) {
                            val tmp = stream.read().decodeToString() + "\n"
                            if (last_msg != tmp) {
                                rec += 1
                                Log.i(tag, tmp)
                                last_msg = tmp
                                msg += tmp
                                Util().sendtoUI(Util().connect_adb_msg, tmp, this.mhandler)
                            }
                        }
                    } catch (e: Exception) {
                    }
                    Util().sendtoUI(Util().connect_adb_closed, "", this.mhandler)
                }.start()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun send(cli: String) {
        Thread {
            Log.i(tag, cli)
            stream.write(" $cli\n")
        }.start()
    }

    fun stop() {
        Thread {
            adb.close()
            Util().sendtoUI(Util().connect_adb_closed, "", this.mhandler)
        }.start()
    }

}
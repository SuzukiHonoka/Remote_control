package org.starx_software_lab.remote_control

import android.os.Handler
import android.os.Message
import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class Client(s: String): WebSocketClient(URI(s)) {
    //
    val connect_fail = 0
    val connect_success = 1
    val connect_msg = 2
    val connect_close = 3
    val connect_id = 4
    //
    var rec_count = 0
    lateinit var id: String
    val tag = "Client"
    lateinit var ehr:Handler

    fun setHandler(hr: Handler) {
        this.ehr = hr
    }

    fun sendtoUI(what: Int, objects: String?) {
        val tmpMSG = Message.obtain()
        tmpMSG.what= what
        if (objects != null) {
            tmpMSG.obj = objects
        }
        this.ehr.handleMessage(tmpMSG)
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        sendtoUI(connect_success,null)
        Log.i(tag,"Connected: " + this.remoteSocketAddress.address.hostAddress)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        sendtoUI(connect_close, reason)
        Log.i(tag,"Client Closed.")
    }

    override fun onMessage(message: String?) {
        rec_count += 1
        if (rec_count == 2) {
            this.id = message.toString()
        }
        Log.i(tag,message.toString())
        sendtoUI(connect_msg,message)
    }

    override fun onError(ex: Exception?) {
        sendtoUI(connect_fail,ex?.printStackTrace().toString())
    }
}
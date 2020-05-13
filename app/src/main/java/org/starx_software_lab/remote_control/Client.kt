package org.starx_software_lab.remote_control

import android.os.Handler
import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class Client(s: String): WebSocketClient(URI(s)) {
    //
    var rec_count = 0
    lateinit var id: String
    val tag = "Client"
    lateinit var ehr:Handler

    fun setHandler(hr: Handler) {
        this.ehr = hr
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Util().sendtoUI(Util().connect_success, null, this.ehr)
        Log.i(tag,"Connected: " + this.remoteSocketAddress.address.hostAddress)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Util().sendtoUI(Util().connect_close, reason, this.ehr)
        Log.i(tag,"Client Closed.")
    }

    override fun onMessage(message: String?) {
        rec_count += 1
        if (rec_count == 2) {
            this.id = message.toString()
            Util().sendtoUI(Util().connect_id, this.id, this.ehr)
        }
        Log.i(tag,message.toString())
        Util().sendtoUI(Util().connect_msg, message, this.ehr)
    }

    override fun onError(ex: Exception?) {
        Util().sendtoUI(Util().connect_fail, ex?.printStackTrace().toString(), this.ehr)
    }

    override fun send(text: String?) {
        Log.i(tag, text.toString())
        super.send(text)
    }



}
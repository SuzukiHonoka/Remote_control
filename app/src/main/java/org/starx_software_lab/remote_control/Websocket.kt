package org.starx_software_lab.remote_control

import android.content.ContentResolver
import android.provider.Settings
import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress


class Websocket(p: Int) : WebSocketServer(InetSocketAddress(p)) {
    val tag = "Websocket"
    var id = ""
    var control = Control()

    fun setID(id: String) {
        this.id = id
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        broadcast("New Connection: " + handshake?.resourceDescriptor)
        conn?.send("Welcome: connected to the backend")
        conn?.send(this.id)
        conn?.send(conn.remoteSocketAddress.address.hostAddress)
        conn?.send("RFRM.")
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        val remote_addr = conn?.remoteSocketAddress?.address?.hostAddress
        broadcast("Connection Closed: IP $remote_addr Reason $reason")
        conn?.send("Bye!")
    }

    override fun onMessage(conn: WebSocket?, message: String?) {

        if (!message.isNullOrEmpty()) {
                if (message.startsWith(this.id)) {
                    val msg = message.split(",")[1]
                    if (!msg.equals("exit")) {
                        control.performAction(msg)
                    } else {
                        control.exit()
                    }

                } else {
                    conn?.send("WRONG ID FOUND!")
                }
            }
    }

    override fun onStart() {
        Log.i(tag,"Started.")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        Log.e(tag,"Websocket " + conn?.resourceDescriptor.toString() + " Error:\n" + ex?.printStackTrace())
    }

}
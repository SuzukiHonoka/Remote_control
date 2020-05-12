package org.starx_software_lab.remote_control


import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress


class Websocket(p: Int) : WebSocketServer(InetSocketAddress(p)) {
    //Main
    val tag = "Websocket"
    var id = ""
    var control = Control()

    fun setID(id: String) {
        this.id = id
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        Log.i(tag,"Websocket started at Port:" + this.port)
        conn?.send("Welcome: connected to the backend")
        conn?.send(this.id)
        broadcast("IP: " + conn?.remoteSocketAddress?.address?.hostAddress + " has connected to the backend")
        conn?.send("Ready for receive cli commands.")
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        val remote_addr = conn?.remoteSocketAddress?.address?.hostAddress
        broadcast("Connection Closed: IP $remote_addr Reason $reason")
    }

    override fun onMessage(conn: WebSocket?, message: String?) {

        if (!message.isNullOrEmpty()) {
                if (message.startsWith(this.id)) {
                    val msg = message.split(",")[1]
                    Log.i(tag,message)
                    when (msg) {
                        "exit" -> {
                            control.exit()
                            this.stop()
                            Log.i(tag,"Websocket Exited")
                        }
                        "reboot" -> {
                            control.reboot()
                        }
                        "shutdown" -> {
                            control.shutdown()
                        }
                        else -> {
                            control.performAction(msg)
                        }
                    }

                } else {
                    Log.e(tag,"ID NOT FOUND!")
                    conn?.send("ID NOT FOUND!")
                }
            }
    }

    override fun onStart() {
        Log.i(tag,"Started.")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        Log.e(tag, ex?.printStackTrace().toString())
    }

}
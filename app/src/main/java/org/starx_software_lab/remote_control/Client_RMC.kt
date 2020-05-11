package org.starx_software_lab.remote_control

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_client__r_m_c.*

class Client_RMC : AppCompatActivity() {
    //
    val connect_fail = 0
    val connect_success = 1
    val connect_msg = 2
    val connect_close = 3
    val connect_id = 4
    //
    var status = 0
    var allow = false
    //
    lateinit var client_rd: Client
    lateinit var id: String
    //
    private val mhandler =
        object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                when(msg.what) {
                    connect_fail -> runOnUiThread {  Toast.makeText(applicationContext,"连接失败。",Toast.LENGTH_SHORT).show() }
                    connect_success -> runOnUiThread { Toast.makeText(applicationContext,"连接成功。",Toast.LENGTH_SHORT).show() }
                    connect_close -> runOnUiThread { Toast.makeText(applicationContext,"连接关闭。",Toast.LENGTH_SHORT).show() }
                    connect_msg -> runOnUiThread { logs.append("\n"+msg.obj.toString()) }
                    connect_id -> runOnUiThread{
                        allow = true
                        id = msg.obj.toString()
                        Toast.makeText(applicationContext,"已获取到ID。",Toast.LENGTH_SHORT).show()
                    }
                }
                super.handleMessage(msg)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client__r_m_c)

        client_connect.setOnClickListener {
            if (status == 0) {
                status = 1
                client_connect.text = "断开"
                editable(server_ip,false)
                editable(server_port,false)
                val ip = server_ip.text
                val port = server_port.text
                if (!ip.isNullOrEmpty() and !port.isNullOrEmpty()) {
                    val uri = "ws://" + ip + ":" + port
                    client_rd = Client(uri)
                    client_rd.setHandler(mhandler)
                    client_rd.connect()
                }
            } else {
                client_connect.text = resources.getString(R.string.connect)
                editable(server_ip,true)
                editable(server_port,true)
                status = 0
                client_rd.close()
            }


        }


    }

    }

    fun editable(v:EditText,b: Boolean) {
        if (b) {
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            v.isClickable = true
        } else {
            v.isFocusable = false
            v.isFocusableInTouchMode = false
            v.isClickable = false
        }
    }


package org.starx_software_lab.remote_control

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_client__r_m_c.*

class Client_RMC : AppCompatActivity(),View.OnClickListener {
    val tag = "Client"
    //
    val connect_fail = 0
    val connect_success = 1
    val connect_msg = 2
    val connect_close = 3
    val connect_id = 4
    //
    var status = 0
    var allow = false
    var way = 1
    //
    lateinit var client_rd: Client
    lateinit var id: String
    //
    private val mhandler =
        object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                when(msg.what) {
                    connect_fail -> runOnUiThread {
                        Toast.makeText(applicationContext,"连接失败。",Toast.LENGTH_SHORT).show()
                        client_connect.text = resources.getString(R.string.connect)
                        editable(server_ip,true)
                        editable(server_port,true)
                        status = 0
                    }
                    connect_success -> runOnUiThread { Toast.makeText(applicationContext,"连接成功。",Toast.LENGTH_SHORT).show() }
                    connect_close -> runOnUiThread { Toast.makeText(applicationContext,"连接关闭。",Toast.LENGTH_SHORT).show()
                        client_connect.text = resources.getString(R.string.connect)
                        editable(server_ip,true)
                        editable(server_port,true)
                        status = 0
                    }
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
        usews.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                useadb.isChecked = false
                way = 1
            }
        }
        useadb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                usews.isChecked = false
                way = 2
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.client_connect -> {
                if (status == 0) {
                    val ip = server_ip.text.toString().trim()
                    val port = server_port.text.toString().trim()
                    if (ip.isNotEmpty() and port.isNotEmpty()) {
                        if (way == 1) {
                            status = 1
                            client_connect.text = "断开"
                            val uri = "ws://$ip:$port"
                            client_rd = Client(uri)
                            client_rd.setHandler(mhandler)
                            client_rd.connect()
                        } else {
//                            val adb = Adb()
//                            adb.setup(applicationContext,filesDir.toString(),ip,port)
//                            adb.start()
                            Toast.makeText(this, "Under developing..", Toast.LENGTH_SHORT).show()
                            usews.isChecked = true
                        }

                    }
                } else {
                    client_connect.text = resources.getString(R.string.connect)
                    editable(server_ip,true)
                    editable(server_port,true)
                    status = 0
                    if (way == 1) {
                        client_rd.close()
                    }

                }
            }
            R.id.back -> {
                if (allow) {
                    Log.i(tag,"$id,back")
                    client_rd.send("$id,back")
                }
            }
            R.id.poweroff -> {
                if (allow) {
                    Log.i(tag,"$id,shutdown")
                    client_rd.send("$id,shutdown")
                }
            }
            R.id.reboot -> {
                if (allow) {
                    Log.i(tag,"$id,reboot")
                    client_rd.send("$id,reboot")
                }
            }
            R.id.up -> {
                if (allow) {
                    Log.i(tag,"$id,up")
                    client_rd.send("$id,up")
                }
            }
            R.id.down -> {
                if (allow) {
                    Log.i(tag,"$id,down")
                    client_rd.send("$id,down")
                }
            }
            R.id.left -> {
                if (allow) {
                    Log.i(tag,"$id,left")
                    client_rd.send("$id,left")
                }
            }
            R.id.right -> {
                if (allow) {
                    Log.i(tag,"$id,right")
                    client_rd.send("$id,right")
                }
            }
            R.id.enter -> {
                if (allow) {
                    Log.i(tag,"$id,enter")
                    client_rd.send("$id,enter")
                }
            }
            R.id.home -> {
                if (allow) {
                    Log.i(tag,"$id,home")
                    client_rd.send("$id,home")
                }
            }
            R.id.menu -> {
                if (allow) {
                    Log.i(tag,"$id,menu")
                    client_rd.send("$id,menu")
                }
            }
            R.id.volup -> {
                if (allow) {
                    Log.i(tag,"$id,vol+")
                    client_rd.send("$id,vol+")
                }
            }
            R.id.volmi -> {
                if (allow) {
                    Log.i(tag,"$id,vol-")
                    client_rd.send("$id,vol-")
                }
            }
            R.id.mute -> {
                if (allow) {
                    Log.i(tag,"$id,mute")
                    client_rd.send("$id,mute")
                }
            }
            R.id.power -> {
                if (allow) {
                    Log.i(tag,"$id,power")
                    client_rd.send("$id,power")
                }
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


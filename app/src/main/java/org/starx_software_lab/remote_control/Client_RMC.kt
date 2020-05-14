package org.starx_software_lab.remote_control

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_client__r_m_c.*

class Client_RMC : AppCompatActivity(),View.OnClickListener {
    val tag = "Client"
    //
    var status = 0
    var allow = false
    var way = 1
    //
    lateinit var client_rd: Client
    lateinit var id: String
    lateinit var adbd: ADB
    //
    lateinit var saved_ip: String
    lateinit var saved_port: String
    //
    var adb_prefer = false
    var ws_prefer = true
    //
    private val mhandler =
        object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                when(msg.what) {
                    Util().connect_fail -> runOnUiThread {
                        Toast.makeText(applicationContext,"连接失败。",Toast.LENGTH_SHORT).show()
                        client_connect.text = resources.getString(R.string.connect)
                        editable(server_ip,true)
                        editable(server_port,true)
                        status = 0
                    }
                    Util().connect_success -> runOnUiThread { popup(applicationContext, "连接成功。") }
                    Util().connect_close -> runOnUiThread {
                        popup(applicationContext, "连接关闭。")
                        client_connect.text = resources.getString(R.string.connect)
                        editable(server_ip,true)
                        editable(server_port,true)
                        status = 0
                    }
                    Util().connect_msg -> runOnUiThread { logs.append("\n" + msg.obj.toString()) }
                    Util().connect_id -> {
                        allow = true
                        id = msg.obj.toString()
                        runOnUiThread {
                            popup(applicationContext, "已获取到ID。")
                        }
                    }
                    Util().connect_adb_msg -> runOnUiThread {
                        logs.append(msg.obj.toString())
                    }
                    Util().connect_adb_closed -> runOnUiThread {
                        val s = msg.obj.toString()
                        popup(applicationContext, "ADB连接已关闭。\n原因: $s")
                    }
                    Util().connect_adb_ok -> {
                        allow = true
                        runOnUiThread {
                            popup(applicationContext, "ADB连接已成功打开。")
                        }
                    }
                }
                super.handleMessage(msg)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client__r_m_c)
        Thread {
            saved_ip = Util().getsp(this, "IP")
            saved_port = Util().getsp(this, "PORT")
            adb_prefer = Util().getsp(this, "ADBP").toBoolean()
            ws_prefer = Util().getsp(this, "WSP").toBoolean()
            if ((!adb_prefer) and (!ws_prefer)) {
                ws_prefer = true
            }
            runOnUiThread {
                usews.isChecked = ws_prefer
                useadb.isChecked = adb_prefer
            }


            if (saved_ip.isNotEmpty() and saved_port.isNotEmpty()) {
                server_ip.setText(saved_ip)
                server_port.setText(saved_port)
                runOnUiThread {
                    popup(this, "数据已读取")
                }

            } else {
                runOnUiThread {
                    popup(this, "数据为空")
                }
            }
        }.start()
        usews.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                useadb.isChecked = false
                way = 1
            }
            Util().setsp(this, "WSP", isChecked.toString())
        }
        useadb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                usews.isChecked = false
                way = 2
            }
            Util().setsp(this, "ADBP", isChecked.toString())
        }
    }

    @ExperimentalStdlibApi
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.client_connect -> {
                if (status == 0) {
                    val ip = server_ip.text.toString().trim()
                    val port = server_port.text.toString().trim()
                    if (ip.isNotEmpty() and port.isNotEmpty()) {
                        if (!Util().isvalidIP(ip) or !Util().isvalidPORT(port)) {
                            popup(this, "非法IP或端口(目前仅支持IPV4，若有IPV6需求请提交至ISSUES)")
                            return
                        }
                        if (!ip.equals(saved_ip) or !port.equals(saved_port)) {
                            if (Util().setsp(this, "IP", ip) and Util().setsp(this, "PORT", port)) {
                                popup(this, "数据保存成功")
                                saved_ip = ip
                                saved_port = port
                            } else {
                                popup(this, "数据保存失败")
                            }
                        }
                        if (way == 1) {
                            status = 1
                            client_connect.text = "断开"
                            val uri = "ws://$ip:$port"
                            client_rd = Client(uri)
                            client_rd.setHandler(mhandler)
                            client_rd.connect()
                        } else {
                            status = 1
                            client_connect.text = "断开"
                            adbd = ADB()
                            adbd.setup(ip, port, filesDir.toString(), mhandler)
                            adbd.start()
//                            Toast.makeText(this, "Under developing..", Toast.LENGTH_SHORT).show()
//                            usews.isChecked = true
                        }

                    } else {
                        popup(this, "IP及端口不能为空")
                    }
                } else {
                    client_connect.text = resources.getString(R.string.connect)
                    editable(server_ip,true)
                    editable(server_port,true)
                    if (way == 1) {
                        try {
                            client_rd.close()
                            status = 0
                        } catch (e: Exception) {
                            popup(this, e.printStackTrace().toString())
                        }
                    } else {
                        try {
                            adbd.stop()
                            status = 0
                        } catch (e: Exception) {
                            popup(this, e.printStackTrace().toString())
                        }

                    }

                }
            }
            R.id.back -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,back")
                    } else {
                        adbd.send(Util().keyevent("back"))
                    }

                }
            }
            R.id.poweroff -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,shutdown")
                    } else {
                        adbd.send(Util().simple_sd + Util().and + Util().su_c + Util().shutdown)
                    }
                }
            }
            R.id.reboot -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,reboot")
                    } else {
                        adbd.send(Util().simple_rt + Util().and + Util().su_c + Util().reboot)
                    }

                }
            }
            R.id.up -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,up")
                    } else {
                        adbd.send(Util().keyevent("up"))
                    }
                }
            }
            R.id.down -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,down")
                    } else {
                        adbd.send(Util().keyevent("down"))
                    }

                }
            }
            R.id.left -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,left")
                    } else {
                        adbd.send(Util().keyevent("left"))
                    }

                }
            }
            R.id.right -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,right")
                    } else {
                        adbd.send(Util().keyevent("right"))
                    }

                }
            }
            R.id.enter -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,enter")
                    } else {
                        adbd.send(Util().keyevent("enter"))
                    }

                }
            }
            R.id.home -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,home")
                    } else {
                        adbd.send(Util().keyevent("home"))
                    }
                }
            }
            R.id.menu -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,menu")
                    } else {
                        adbd.send(Util().keyevent("menu"))
                    }

                }
            }
            R.id.volup -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,vol+")
                    } else {
                        adbd.send(Util().keyevent("vol+"))
                    }

                }
            }
            R.id.volmi -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,vol-")
                    } else {
                        adbd.send(Util().keyevent("vol-"))
                    }

                }
            }
            R.id.mute -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,mute")
                    } else {
                        adbd.send(Util().keyevent("mute"))
                    }

                }
            }
            R.id.power -> {
                if (allow) {
                    if (way == 1) {
                        client_rd.send("$id,power")
                    } else {
                        adbd.send(Util().keyevent("power"))
                    }

                }
            }
        }
    }

    override fun onDestroy() {
        if (::adbd.isInitialized) {
            adbd.stop()
        }
        super.onDestroy()
    }
}


fun editable(v: EditText, b: Boolean) {
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

fun popup(context: Context, string: String) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
}



package org.starx_software_lab.remote_control

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var status = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        server.setOnClickListener {
            if (status == 0) {
                status = 1
                applicationContext.startService(Intent(applicationContext, WSCTRL::class.java))
                Toast.makeText(this,"受控服务已启动于端口1060。",Toast.LENGTH_SHORT).show()
            } else {
                status = 0
                applicationContext.stopService(Intent(applicationContext, WSCTRL::class.java))
                Toast.makeText(this,"受控服务已关闭。",Toast.LENGTH_SHORT).show()
            }

        }
        client.setOnClickListener {
            startActivity(Intent(applicationContext,Client_RMC::class.java))
        }
    }
}

package org.starx_software_lab.remote_control

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    var status = 0
    var twice = 0
    var auto = false
    var task = false
    //
    var back: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auto = Util().getsp(this, "AUTOBOOT").toBoolean()
        autoboot.isChecked = auto
        task = Util().getsp(this, "AUTOBOOT").toBoolean()
        autoclose.isChecked = task
        server.setOnClickListener {
            if (isServiceRunning(WSCTRL::class.java)) {
                twice += 1
                status = 1
                if (twice == 2) {
                    twice = 0
                } else {
                    popup(this, "后台服务已在运行..\n再次点按即可停止")
                    return@setOnClickListener
                }
            }
            if (status == 0) {
                status = 1
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(this, WSCTRL::class.java))
                }
                startService(Intent(this, WSCTRL::class.java))
                popup(this, "受控服务已启动于端口1060。")
            } else {
                status = 0
                stopService(Intent(this, WSCTRL::class.java))
                popup(this, "受控服务已关闭。")
            }
        }
        client.setOnClickListener {
            startActivity(Intent(this,Client_RMC::class.java))
        }
        autoboot.setOnCheckedChangeListener { _, isChecked ->
            var s = "已打开"
            if (isChecked != auto) {
                auto = isChecked
                Util().setsp(this, "AUTOBOOT", isChecked.toString())
            }
            if (!isChecked) {
                s = "已关闭"
            }
            popup(this, "受控端自启$s")
        }

        autoclose.setOnCheckedChangeListener { _, isChecked ->
            var s = "已打开"
            if (isChecked != task) {
                task = isChecked
                Util().setsp(this, "AUTOCLOSE", isChecked.toString())
            }
            if (!isChecked) {
                s = "已关闭"
            }
            popup(this, "进程结束$s")
        }

    }


    @Suppress("DEPRECATION")
    fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any {
                it.service.className == service.name
            }
    }

    override fun onBackPressed() {
        Snackbar.make(window.decorView, "再按一次即可退出", Snackbar.LENGTH_SHORT).show();
        if (back == null) {
            back = Date().time;
        } else if (Date().time - back!! <= 2000) {
            finish()
        } else {
            back = Date().time;
        }
        //super.onBackPressed()
    }

    override fun onDestroy() {
        if (task) {
            finishAndRemoveTask()
            if (isServiceRunning(WSCTRL::class.java)) {
                stopService(Intent(this, WSCTRL::class.java))
            }
        }
        super.onDestroy()
    }
}

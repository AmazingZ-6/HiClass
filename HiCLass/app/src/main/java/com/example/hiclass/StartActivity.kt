package com.example.hiclass

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hiclass.schedule.ScheduleMain
import kotlinx.android.synthetic.main.tab_layout.*
import java.io.File
import java.util.*


class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val view = layoutInflater.inflate(R.layout.activity_start, null)
//        val controller = ViewCompat.getWindowInsetsController(view)
//        controller?.hide(WindowInsetsCompat.Type.statusBars())
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_start)


        Timer().schedule(object : TimerTask() {
            override fun run() {
                val file = File(applicationContext.filesDir, "load_info")
                if (file.exists()) {
                    val s = this@StartActivity.openFileInput("load_info").bufferedReader()
                        .useLines { lines ->
                            lines.fold("") { some, text ->
                                "$some$text"
                            }
                        }
                    if (s.length > 10) {
                        val nextIntent = Intent(this@StartActivity, ScheduleMain::class.java)
                        nextIntent.putExtra("load_info", s)
                        startActivity(nextIntent)
                        finish()
                    } else {
                        val nextIntent = Intent(this@StartActivity, GetClassInfo::class.java)
                        startActivity(nextIntent)
                        finish()
                    }
                } else {
                    val nextIntent = Intent(this@StartActivity, GetClassInfo::class.java)
                    startActivity(nextIntent)
                    finish()
                }
            }
        }, 1000)

    }
}
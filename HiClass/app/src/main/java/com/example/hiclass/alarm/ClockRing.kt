package com.example.hiclass.alarm

import android.app.KeyguardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.example.hiclass.R
import com.example.hiclass.alarmDao
import com.example.hiclass.alarmList
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.data_class.DeliverInfoBean
import com.example.hiclass.data_class.ResourceBean
import com.example.hiclass.utils.ClockedAlarm
import com.example.hiclass.utils.FastBlurUtility
import com.example.hiclass.utils.MakeStatusBarTransparent
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_about_me.*
import kotlinx.android.synthetic.main.activity_clock_ring.*
import kotlinx.android.synthetic.main.item_add_base.*
import kotlin.concurrent.thread

class ClockRing : AppCompatActivity() {

    private val mediaPlayer = MediaPlayer()
    lateinit var alarm: AlarmDataBean
    private var right = ""
    private var alarmId = -1L
    private var a = ""
    private var b = ""
    private var c = ""
    private var d = ""
    private var content = ""
    private var correct = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        MakeStatusBarTransparent.makeStatusBarTransparent(this)
        super.onCreate(savedInstanceState)
        alarmId = intent.getLongExtra("alarm_id", -1L)
        content = intent.getStringExtra("que_content").toString()
        a = intent.getStringExtra("que_a").toString()
        b = intent.getStringExtra("que_b").toString()
        d = intent.getStringExtra("que_d").toString()
        c = intent.getStringExtra("que_c").toString()
        correct = intent.getStringExtra("que_correct").toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
            Log.d("time", "clock!")
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        setContentView(R.layout.activity_clock_ring)
        val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
        val isInitBg = sharedPre.getInt("init_bg", R.color.little_white)
        val blurBg = sharedPre.getString("background_blur", "")
        if (isInitBg >= 0) {
            clock_ring_main.setBackgroundResource(R.drawable.bg_blur)
        } else {
            val decode = Base64.decode(blurBg!!.toByteArray(), 1)
            val bmp = BitmapFactory.decodeByteArray(decode, 0, decode.size)
            clock_ring_main.background = BitmapDrawable(resources, bmp)
        }
        initInfo()
        initOption()
        initMediaPlayer()
        mediaPlayer.start()
        mediaPlayer.isLooping = true
    }

    private fun initInfo() {
        for (entity in alarmList) {
            if (entity.id == alarmId) {
                alarm = entity
                break
            }
        }
        val timeT = alarm.alarmTime
        ring_time.text = timeT
        val nameT = alarm.alarmName
        ring_name.text = nameT
        ring_que_content.text = content
        ring_que_a.text = a
        ring_que_b.text = b
        ring_que_c.text = c
        ring_que_d.text = d
        right = correct
    }

    private fun initMediaPlayer() {
        val assetManager = assets
        val fd = assetManager.openFd("default.wav")
        mediaPlayer.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mediaPlayer.prepare()
        mediaPlayer.setVolume(0.7f, 0.7f)
    }

    private fun initOption() {
        val list = arrayListOf<androidx.appcompat.widget.AppCompatButton>(
            ring_que_a, ring_que_b, ring_que_c, ring_que_d
        )
        when (right) {
            "A" -> {
                list[0].setOnClickListener {
                    stopRing()
                }
                for (i in 1..3) {
                    list[i].setOnClickListener {
                        chooseWrong()
                    }
                }
            }
            "B" -> {
                list[1].setOnClickListener {
                    stopRing()
                }
                for (i in listOf(0, 2, 3)) {
                    list[i].setOnClickListener {
                        chooseWrong()
                    }
                }
            }
            "C" -> {
                list[2].setOnClickListener {
                    stopRing()
                }
                for (i in listOf(0, 1, 3)) {
                    list[i].setOnClickListener {
                        chooseWrong()
                    }

                }
            }
            "D" -> {
                list[3].setOnClickListener {
                    stopRing()
                }
                for (i in listOf(0, 1, 2)) {
                    list[i].setOnClickListener {
                        chooseWrong()
                    }
                }
            }
        }
    }

    private fun stopRing() {
        mediaPlayer.stop()
        mediaPlayer.release()
        updateAlarm()
        finish()
    }

    private fun updateAlarm() {
        thread {
            for (i in alarmList) {
                if (i.id == alarmId) {
                    i.alarmSwitch = false
                    alarmDao.updateAlarm(i)
                    ClockedAlarm.cAlarm = i
                    ClockedAlarm.cFlag = true
                    break
                }
            }
        }
    }

    private fun chooseWrong() {
        AlertDialog.Builder(this).apply {
            setTitle("选择了错误的答案噢！")
            setMessage("闹钟将以低音量持续响铃3分钟，请等待片刻后再次答题")
            setCancelable(false)
            setPositiveButton("确定") { _, _ ->
            }
        }.show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle("答题正确才可以关闭闹钟噢！")
            setMessage("若有紧急情况可以连续点击右下方按钮强制关闭")
            setCancelable(false)
            setPositiveButton("确定") { _, _ ->
            }
        }.show()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer.stop()
//        mediaPlayer.release()
//    }
}
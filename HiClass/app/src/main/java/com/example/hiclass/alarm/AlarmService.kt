package com.example.hiclass.alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hiclass.alarmList
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.data_class.DeliverInfoBean
import com.example.hiclass.data_class.ResourceBean
import com.example.hiclass.resourceDao
import com.example.hiclass.schedule.ScheduleMain
import com.example.hiclass.utils.TypeSwitcher.charToInt
import java.util.*
import kotlin.concurrent.thread

class AlarmService : Service() {

    private lateinit var alarm: AlarmDataBean
    private val piMap = mutableMapOf<AlarmDataBean,PendingIntent>()


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyService", "onCreate executed !")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, ScheduleMain::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val channel = NotificationChannel(
            "my_service",
            "前台service通知",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, "my_service")
            .setContentTitle("Alarm")
            .setContentText("Alarm is running")
            .setContentIntent(pi)
            .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra("alarm_id", -1)
        for (entity in alarmList) {
            if (alarmId == entity.id) {
                alarm = entity
                break
            }
        }
        thread {
            if (alarm.alarmSwitch) {
                val random = (0..900).random()
                val randomQue = resourceDao.getRandomQue(random.toLong(), "english")
                setClock(randomQue)
            } else {
                cancelClock()
            }

        }
        return START_REDELIVER_INTENT
    }

    private fun setClock(que: ResourceBean) {
        val mAlarmManager: AlarmManager = getSystemService(Service.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ClockRing::class.java)
        intent.putExtra("alarm_id",alarm.id)
        intent.putExtra("que_content",que.content)
        intent.putExtra("que_a",que.A)
        intent.putExtra("que_b",que.B)
        intent.putExtra("que_c",que.C)
        intent.putExtra("que_d",que.D)
        intent.putExtra("que_correct",que.correct)
        val pi = PendingIntent.getActivity(this, alarm.id.toInt(), intent, 0)
        piMap[alarm] = pi
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8")
        if ('7' in alarm.alarmWeekday) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
        }
        val hourTemp = charToInt(alarm.alarmTime.split(":")[0][0]) * 10 + charToInt(
            alarm.alarmTime.split(":")[0][1]
        )
        val minuteTemp = charToInt(alarm.alarmTime.split(":")[1][0]) * 10 + charToInt(
            alarm.alarmTime.split(":")[1][1]
        )
        calendar.set(Calendar.HOUR_OF_DAY, hourTemp)
        calendar.set(Calendar.MINUTE,minuteTemp)
        Log.d("time", calendar.time.toString())
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
    }

    private fun cancelClock() {
        val mAlarmManager: AlarmManager = getSystemService(Service.ALARM_SERVICE) as AlarmManager
        val pi = piMap[alarm]
        mAlarmManager.cancel(pi)
    }
}
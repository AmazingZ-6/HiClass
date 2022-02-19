package com.example.hiclass.alarm

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
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
import com.example.hiclass.utils.CalendarUtil
import com.example.hiclass.utils.CalendarUtil.getDate
import com.example.hiclass.utils.TypeSwitcher.charToInt
import com.example.hiclass.utils.TypeSwitcher.chineseToInt
import java.util.*
import kotlin.concurrent.thread

class AlarmService : Service() {

    private lateinit var alarm: AlarmDataBean
    private val piMap = mutableMapOf<Long, ResourceBean>()


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
            if (alarm.alarmSwitch && this::alarm.isInitialized) {
                val random = (0..900).random()
                val randomQue = resourceDao.getRandomQue(random.toLong(), "english")
                if (alarm.alarmType == 1) {
                    analyseWeekday(randomQue)
                } else {
                    analyseTermDay(randomQue)
                }
            } else {
                cancelClock()
            }

        }
        return START_REDELIVER_INTENT
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setClock(que: ResourceBean, cal: Calendar) {
        val mAlarmManager: AlarmManager = getSystemService(Service.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ClockRing::class.java)
        intent.putExtra("alarm_id", alarm.id)
        intent.putExtra("que_content", que.content)
        intent.putExtra("que_a", que.A)
        intent.putExtra("que_b", que.B)
        intent.putExtra("que_c", que.C)
        intent.putExtra("que_d", que.D)
        intent.putExtra("que_correct", que.correct)
        val pi = PendingIntent.getActivity(this, alarm.id.toInt(), intent, FLAG_UPDATE_CURRENT)
        piMap[alarm.id] = que
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelClock() {
        val mAlarmManager: AlarmManager = getSystemService(Service.ALARM_SERVICE) as AlarmManager
        val que = piMap[alarm.id]
        piMap.remove(alarm.id)
        val intent = Intent(this, ClockRing::class.java)
        intent.putExtra("alarm_id", alarm.id)
        intent.putExtra("que_content", que!!.content)
        intent.putExtra("que_a", que.A)
        intent.putExtra("que_b", que.B)
        intent.putExtra("que_c", que.C)
        intent.putExtra("que_d", que.D)
        intent.putExtra("que_correct", que.correct)
        val pi = PendingIntent.getActivity(this, alarm.id.toInt(), intent, FLAG_UPDATE_CURRENT)
        mAlarmManager.cancel(pi)
    }

    private fun analyseTermDay(que: ResourceBean) {
        val hVal = charToInt(alarm.alarmTime[0]) * 10 + charToInt(alarm.alarmTime[1])
        val mVal = charToInt(alarm.alarmTime[3]) * 10 + charToInt(alarm.alarmTime[4])
        val weekSt = alarm.alarmTermDay.split("周")[0].subSequence(
            1, alarm.alarmTermDay.split("周")[0].length
        )
        val week = if (weekSt.length > 1) {
            charToInt(weekSt[0]) * 10 + charToInt(weekSt[1])
        } else {
            charToInt(weekSt[0])
        }
        val weekday = chineseToInt(alarm.alarmTermDay[alarm.alarmTermDay.length - 6])
        val date = getDate(week, weekday)
        val month = if (date.split(".")[0].length > 1) {
            charToInt(date.split(".")[0][0]) * 10 + charToInt(date.split(".")[0][1])
        } else {
            charToInt(date.split(".")[0][0])
        }
        val dayOfMonth = if (date.split(".")[1].length > 1) {
            charToInt(date.split(".")[1][0]) * 10 + charToInt(date.split(".")[1][1])
        } else {
            charToInt(date.split(".")[1][0])
        }
        if (alarm.alarmInterval == 0) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.timeZone = TimeZone.getTimeZone("GMT+8")
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, hVal)
            calendar.set(Calendar.MINUTE, mVal)
            calendar.set(Calendar.SECOND, 1)
            Log.d("time", calendar.time.toString())
            setClock(que, calendar)
        }
    }

    private fun analyseWeekday(que: ResourceBean) {
        val hVal = charToInt(alarm.alarmTime[0]) * 10 + charToInt(alarm.alarmTime[1])
        val mVal = charToInt(alarm.alarmTime[3]) * 10 + charToInt(alarm.alarmTime[4])
        val weekdayT = mutableListOf<Int>()
        for (wd in alarm.alarmWeekday.split(",")) {
            if (wd.isNotEmpty()) {
                weekdayT.add(charToInt(wd[0]))
            }
        }
        weekdayT.sort()
        for (i in weekdayT) {
            if (i > 6) {
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.timeZone = TimeZone.getTimeZone("GMT+8")
                if (CalendarUtil.judgeDayOut(hVal, mVal)) calendar.set(
                    Calendar.DAY_OF_MONTH,
                    calendar.get(Calendar.DAY_OF_MONTH) + 1
                )
                calendar.set(Calendar.HOUR_OF_DAY, hVal)
                calendar.set(Calendar.MINUTE, mVal)
                calendar.set(Calendar.SECOND, 1)
                Log.d("time", calendar.time.toString())
                setClock(que, calendar)
            } else {
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.timeZone = TimeZone.getTimeZone("GMT+8")
                val wdNow = calendar.get(Calendar.DAY_OF_WEEK) - 1
                if (i + 1 > wdNow) {
                    calendar.set(Calendar.DAY_OF_WEEK, i + 2)
                    calendar.set(Calendar.HOUR_OF_DAY, hVal)
                    calendar.set(Calendar.MINUTE, mVal)
                    calendar.set(Calendar.SECOND, 1)
                    Log.d("time", calendar.time.toString())
                    setClock(que, calendar)
                } else if (i + 1 < wdNow) {
                    calendar.set(Calendar.DAY_OF_WEEK, i + 2)
                    calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1)
                    calendar.set(Calendar.HOUR_OF_DAY, hVal)
                    calendar.set(Calendar.MINUTE, mVal)
                    calendar.set(Calendar.SECOND, 1)
                    Log.d("time", calendar.time.toString())
                    setClock(que, calendar)
                } else {
                    if (CalendarUtil.judgeDayOut(hVal, mVal)) {
                        calendar.set(Calendar.DAY_OF_WEEK, i + 2)
                        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1)
                        calendar.set(Calendar.HOUR_OF_DAY, hVal)
                        calendar.set(Calendar.MINUTE, mVal)
                        calendar.set(Calendar.SECOND, 1)
                        Log.d("time", calendar.time.toString())
                        setClock(que, calendar)
                    } else {
                        calendar.set(Calendar.DAY_OF_WEEK, i + 2)
                        calendar.set(Calendar.HOUR_OF_DAY, hVal)
                        calendar.set(Calendar.MINUTE, mVal)
                        calendar.set(Calendar.SECOND, 1)
                        Log.d("time", calendar.time.toString())
                        setClock(que, calendar)
                    }
                }
            }
        }
    }

}
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
import com.example.hiclass.*
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.data_class.DeliverInfoBean
import com.example.hiclass.data_class.ResourceBean
import com.example.hiclass.schedule.ScheduleMain
import com.example.hiclass.utils.CalendarUtil
import com.example.hiclass.utils.CalendarUtil.getDate
import com.example.hiclass.utils.GroupAlarm
import com.example.hiclass.utils.TypeSwitcher.charToInt
import com.example.hiclass.utils.TypeSwitcher.chineseToInt
import java.util.*
import kotlin.concurrent.thread

class AlarmService : Service() {


    private lateinit var alarm: AlarmDataBean
    private val piMap = mutableMapOf<Long, MutableMap<Int, ResourceBean>>()
    private val existedPiId = mutableListOf<Int>()
    private val dateMap = mutableMapOf<Int, Int>()
    private val dateMap2 = mutableMapOf<Int, Int>()
    private var isDelete: Boolean? = false


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
            "hiclass_service",
            "前台service通知",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, "hiclass_service")
            .setContentTitle("Alarm")
            .setContentText("Alarm is running")
            .setContentIntent(pi)
            .build()
        startForeground(1, notification)
        initDateMap()
        isServiceAlive = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra("alarm_id", -1)
        val isGroup = intent?.getBooleanExtra("isGroup", false)
        isDelete = intent?.getBooleanExtra("isDelete", false)
        val isClock = intent?.getBooleanExtra("isCLock", false)
        if (isClock == true) {
            for (entity in alarmList) {
                if (alarmId == entity.id) {
                    alarm = entity
                    break
                }
            }
            clockedClock()
        }
        if (!isGroup!!) {
            for (entity in alarmList) {
                if (alarmId == entity.id) {
                    alarm = entity
                    break
                }
            }
            thread {
                if (this::alarm.isInitialized) {
                    if (alarm.alarmSwitch) {
                        if (alarm.alarmType == 1) {
                            analyseWeekday()
                        } else {
                            analyseTermDay()
                        }
                    } else {
                        cancelClock()
                    }
                }
            }
        } else {
            val group = GroupAlarm.readG()
            thread {
                for (al in group) {
                    if (al.alarmSwitch) {
                        alarm = al
                        if (alarm.alarmType == 0)
                            analyseTermDay()
                        else
                            analyseWeekday()
                    }
                }
                GroupAlarm.clearG()
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
        val piId = isRepeated((0..3000).random())
        existedPiId.add(piId)
        if (piMap[alarm.id] == null) {
            piMap[alarm.id] = mutableMapOf()
            piMap[alarm.id]?.put(piId, que)
        } else {
            piMap[alarm.id]?.put(piId, que)
        }
        val pi = PendingIntent.getActivity(this, piId, intent, FLAG_UPDATE_CURRENT)
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelClock() {
        val mAlarmManager: AlarmManager = getSystemService(Service.ALARM_SERVICE) as AlarmManager
        for (i in piMap[alarm.id]?.entries!!) {
            val que = i.value
            val intent = Intent(this, ClockRing::class.java)
            intent.putExtra("alarm_id", alarm.id)
            intent.putExtra("que_content", que.content)
            intent.putExtra("que_a", que.A)
            intent.putExtra("que_b", que.B)
            intent.putExtra("que_c", que.C)
            intent.putExtra("que_d", que.D)
            intent.putExtra("que_correct", que.correct)
            val pi = PendingIntent.getActivity(this, i.key, intent, FLAG_UPDATE_CURRENT)
            Log.d("time", "${alarm.id}+${i.key}+${i.value.id}")
            mAlarmManager.cancel(pi)
        }
        piMap.remove(alarm.id)
        if (isDelete == true) {
            deleteAlarm(alarm)
            isDelete = false
        }
    }

    private fun clockedClock() {
        if (piMap[alarm.id] != null) {
            piMap.remove(alarm.id)
        }
    }

    private fun analyseTermDay() {
        val random = (0..900).random()
        val que = resourceDao.getRandomQue(random.toLong(), "english")
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
        val weekday = chineseToInt(alarm.alarmTermDay.split("星期")[1][0])
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
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis = System.currentTimeMillis()
            if (calendar.timeInMillis >= cal1.timeInMillis && piMap[alarm.id] == null) {
                setClock(que, calendar)
            }
        }
    }

    private fun analyseWeekday() {
        val hVal = charToInt(alarm.alarmTime[0]) * 10 + charToInt(alarm.alarmTime[1])
        val mVal = charToInt(alarm.alarmTime[3]) * 10 + charToInt(alarm.alarmTime[4])
        val weekdayT = mutableListOf<Int>()
        val calNow = Calendar.getInstance()
        calNow.timeInMillis = System.currentTimeMillis()
        for (wd in alarm.alarmWeekday.split(",")) {
            if (wd.isNotEmpty()) {
                weekdayT.add(charToInt(wd[0]))
            }
        }
        weekdayT.sort()
        for (wd in weekdayT) {
            val random = (0..900).random()
            val que = resourceDao.getRandomQue(random.toLong(), "english")
            if (wd > 6) {
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.timeZone = TimeZone.getTimeZone("GMT+8")
                if (CalendarUtil.judgeDayOut(hVal, mVal)) calendar.set(
                    Calendar.DAY_OF_YEAR,
                    calendar.get(Calendar.DAY_OF_YEAR) + 1
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
                val wdNow = dateMap[calendar.get(Calendar.DAY_OF_WEEK)]!!
                if (wd > wdNow) {
                    calendar.set(Calendar.DAY_OF_WEEK, dateMap2[wd]!!)
                    if (wd == 6) {
                        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1)
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, hVal)
                    calendar.set(Calendar.MINUTE, mVal)
                    calendar.set(Calendar.SECOND, 1)
                    Log.d("time", calendar.time.toString())
                    setClock(que, calendar)
                } else if (wd < wdNow) {
                    calendar.set(Calendar.DAY_OF_WEEK, dateMap2[wd]!!)
                    calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1)
                    calendar.set(Calendar.HOUR_OF_DAY, hVal)
                    calendar.set(Calendar.MINUTE, mVal)
                    calendar.set(Calendar.SECOND, 1)
                    Log.d("time", calendar.time.toString())
                    setClock(que, calendar)
                } else {
                    if (CalendarUtil.judgeDayOut(hVal, mVal)) {
                        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1)
                        calendar.set(Calendar.HOUR_OF_DAY, hVal)
                        calendar.set(Calendar.MINUTE, mVal)
                        calendar.set(Calendar.SECOND, 1)
                        Log.d("time", calendar.time.toString())
                        setClock(que, calendar)
                    } else {
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

    private fun isRepeated(num: Int): Int {
        for (entry in existedPiId) {
            if (entry == num) {
                return isRepeated((0..3000).random())
            }
        }
        return num
    }

    private fun initDateMap() {
        for (i in 2..7) {
            dateMap[i] = i - 2
        }
        dateMap[1] = 6
        for (j in 0..5) {
            dateMap2[j] = j + 2
        }
        dateMap2[6] = 1
    }

//    private fun clearClock() {
//        for (alarm in alarmList) {
//            alarm.alarmSwitch = false
//        }
//    }

    private fun deleteAlarm(ala: AlarmDataBean) {
        alarmList.remove(ala)
        thread {
            alarmDao.deleteAlarm(ala)
        }
        if (ala.alarmType == 0) {
            for (i in matchList.indices) {
                if (ala.id == matchList[i].alarmId) {
                    val m = matchList.removeAt(i)
                    thread {
                        matchDao.deleteAInfo(m)
                    }
                    break
                }
            }
        }
    }

}
package com.example.hiclass.utils

import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.data_class.TimeGap
import com.example.hiclass.utils.CalendarUtil.judgeDayOut
import com.example.hiclass.utils.TypeSwitcher.charToInt
import java.util.*

object TimeGapUtil {

    data class DateTimeBean(
        var weekday: Int,
        var hour: Int, var minute: Int
    )

    fun getTimeGap(alarmList: MutableList<AlarmDataBean>): TimeGap {
        val gapTotal = mutableListOf<TimeGap>()
        for (entity in alarmList) {
            val hVal = charToInt(entity.alarmTime[0]) * 10 + charToInt(entity.alarmTime[1])
            val mVal = charToInt(entity.alarmTime[3]) * 10 + charToInt(entity.alarmTime[4])
            val weekdayList = mutableListOf<Int>()
            val gapList = mutableListOf<TimeGap>()
            for (d in entity.alarmWeekday.split(",")) {
                if (d.isNotEmpty()) {
                    weekdayList.add(charToInt(d[0]) + 1)
                }
            }
            for (wd in weekdayList) {
                val dateT = DateTimeBean(wd, hVal, mVal)
                val gapT = calculateTimeGap(dateT)
                gapList.add(gapT)
            }
            val minGap = compareTimeGap(gapList)
            gapTotal.add(minGap)
        }
        return compareTimeGap(gapTotal)
    }

    private fun calculateTimeGap(dateTime: DateTimeBean): TimeGap {
        val calNow = Calendar.getInstance()
        calNow.timeInMillis = System.currentTimeMillis()
        calNow.timeZone = TimeZone.getTimeZone("GMT+8")
        var dayGap = 0
        var hourGap = 0
        var minuteGap = 0
//    val dayNow = calNow.get(Calendar.DAY_OF_YEAR)
//    val weekNow = calNow.get(Calendar.WEEK_OF_MONTH)
//    val monthNow = calNow.get(Calendar.MONTH)  // month - 1
        val weekdayNow = calNow.get(Calendar.DAY_OF_WEEK) // weekday + 1
        val hourNow = calNow.get(Calendar.HOUR_OF_DAY)
        val minuteNow = calNow.get(Calendar.MINUTE)
        if (dateTime.weekday > weekdayNow - 1) {
            dayGap = dateTime.weekday - weekdayNow + 1
            if (judgeDayOut(dateTime.hour, dateTime.minute)) {
                val minuteGapAll1 = 24 * 60 - (hourNow * 60 + minuteNow)
                val minuteGapAll2 = dateTime.hour * 60 + dateTime.minute
                val minuteGapAll3 = minuteGapAll1 + minuteGapAll2
                hourGap = (minuteGapAll3 - minuteGapAll3 % 60) / 60
                minuteGap = minuteGapAll3 % 60
                dayGap -= 1
            } else {
                val minuteGapAll =
                    (dateTime.hour * 60 + dateTime.minute) - (hourNow * 60 + minuteNow)
                minuteGap = minuteGapAll % 60
                hourGap = (minuteGapAll - minuteGapAll % 60) / 60
            }
        } else if (dateTime.weekday == weekdayNow - 1) {
            if (!judgeDayOut(dateTime.hour, dateTime.minute)) {
                val minuteGapAll =
                    (dateTime.hour * 60 + dateTime.minute) - (hourNow * 60 + minuteNow)
                hourGap = (minuteGapAll - minuteGapAll % 60) / 60
                minuteGap = minuteGapAll % 60
                dayGap = 0
            } else {
                val minuteGapAll1 = 24 * 60 - (hourNow * 60 + minuteNow)
                val minuteGapAll2 = dateTime.hour * 60 + dateTime.minute
                val minuteGapAll3 = minuteGapAll1 + minuteGapAll2
                minuteGap = minuteGapAll3 % 60
                hourGap = (minuteGapAll3 - minuteGap) / 60
                dayGap = 6
            }
        } else if (dateTime.weekday < weekdayNow - 1) {
            dayGap = 7 - (weekdayNow - 1 - dateTime.weekday)
            if (judgeDayOut(dateTime.hour, dateTime.minute)) {
                val minuteGapAll1 = 24 * 60 - (hourNow * 60 + minuteNow)
                val minuteGapAll2 = dateTime.hour * 60 + dateTime.minute
                val minuteGapAll3 = minuteGapAll1 + minuteGapAll2
                minuteGap = minuteGapAll3 % 60
                hourGap = (minuteGapAll3 - minuteGap) / 60
                dayGap -= 1
            } else {
                val minuteGapAll =
                    (dateTime.hour * 60 + dateTime.minute) - (hourNow * 60 + minuteNow)
                minuteGap = minuteGapAll % 60
                hourGap = (minuteGapAll - minuteGap) / 60
            }
        }
        return TimeGap(dayGap, hourGap, minuteGap)
    }

    private fun compareTimeGap(list: MutableList<TimeGap>): TimeGap {
        val minuteTotalList = mutableListOf<Int>()
        for (g in list) {
            val hourTotal = g.dayGap * 24 + g.hGap
            val minuteTotal = hourTotal * 60 + g.mGap
            minuteTotalList.add(minuteTotal)
        }
        minuteTotalList.sort()
        val minG = minuteTotalList[0]
        val mTemp = minG % 60
        val hTemp = (minG - mTemp) / 60
        val d = (hTemp - (hTemp % 24)) / 24
        val h = hTemp - d * 24
        return TimeGap(d, h, mTemp)
    }
}
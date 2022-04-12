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

    fun getTimeGap(alarmOpenList: MutableList<AlarmDataBean>): TimeGap {
        val calNow = Calendar.getInstance()
        calNow.timeInMillis = System.currentTimeMillis()
        calNow.timeZone = TimeZone.getTimeZone("GMT+8")
        val gapTotal = mutableListOf<TimeGap>()
        for (entity in alarmOpenList) {
            if (entity.alarmType == 1) {
                val hVal = charToInt(entity.alarmTime[0]) * 10 + charToInt(entity.alarmTime[1])
                val mVal = charToInt(entity.alarmTime[3]) * 10 + charToInt(entity.alarmTime[4])
                val weekdayList = mutableListOf<Int>()
                val gapList = mutableListOf<TimeGap>()
                for (d in entity.alarmWeekday.split(",")) {
                    if (d.isNotEmpty()) {
                        if (charToInt(d[0]) < 7) {
                            weekdayList.add(charToInt(d[0]) + 1)
                        } else {
                            if (judgeDayOut(hVal, mVal)) {
                                weekdayList.add(calNow.get(Calendar.DAY_OF_WEEK))
                            } else {
                                weekdayList.add(calNow.get(Calendar.DAY_OF_WEEK) - 1)
                            }
                        }
                    }
                }
                for (wd in weekdayList) {
                    val dateT = DateTimeBean(wd, hVal, mVal)
                    val gapT = calculateTimeGap(dateT)
                    gapList.add(gapT)
                }
                val minGap = compareTimeGap(gapList)
                gapTotal.add(minGap)
            } else {
                val hVal = charToInt(entity.alarmTime[0]) * 10 + charToInt(entity.alarmTime[1])
                val mVal = charToInt(entity.alarmTime[3]) * 10 + charToInt(entity.alarmTime[4])
                val weekSt = entity.alarmTermDay.split("周")[0].subSequence(
                    1, entity.alarmTermDay.split("周")[0].length
                )
                val week = if (weekSt.length > 1) {
                    charToInt(weekSt[0]) * 10 + charToInt(weekSt[1])
                } else {
                    charToInt(weekSt[0])
                }
                val weekday =
                    TypeSwitcher.chineseToInt(entity.alarmTermDay.split("星期")[1][0])
                val date = CalendarUtil.getDate(week, weekday)
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
                var dGap = calculateDayGap(month, dayOfMonth)
                val hMGap = calculateHMGap(hVal, mVal)
                val hGap = hMGap[0]
                val mGap = hMGap[1]
                dGap -= hMGap[2]
                val tGap = TimeGap(dGap, hGap, mGap)
                gapTotal.add(tGap)
            }
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

    private fun calculateDayGap(mon: Int, day: Int): Int {
        val calNow = Calendar.getInstance()
        calNow.timeInMillis = System.currentTimeMillis()
        calNow.timeZone = TimeZone.getTimeZone("GMT+8")
        val calCpa = Calendar.getInstance()
        calCpa.timeInMillis = System.currentTimeMillis()
        calCpa.timeZone = TimeZone.getTimeZone("GMT+8")
        calCpa.set(Calendar.MONTH, mon - 1)
        calCpa.set(Calendar.DAY_OF_MONTH, day)
        val dToday = calNow.get(Calendar.DAY_OF_YEAR)
        val dCpa = calCpa.get(Calendar.DAY_OF_YEAR)
        return dCpa - dToday
    }

    private fun calculateHMGap(h: Int, m: Int): MutableList<Int> {
        val resList = mutableListOf<Int>()
        val calNow = Calendar.getInstance()
        calNow.timeInMillis = System.currentTimeMillis()
        calNow.timeZone = TimeZone.getTimeZone("GMT+8")
        val hourNow = calNow.get(Calendar.HOUR_OF_DAY)
        val minuteNow = calNow.get(Calendar.MINUTE)
        if (judgeDayOut(h, m)) {
            val minuteGapAll1 = 24 * 60 - (hourNow * 60 + minuteNow)
            val minuteGapAll2 = h * 60 + m
            val minuteGapAll3 = minuteGapAll1 + minuteGapAll2
            val minuteGap = minuteGapAll3 % 60
            val hourGap = (minuteGapAll3 - minuteGap) / 60
            resList.add(0, hourGap)
            resList.add(1, minuteGap)
            resList.add(2, 1)
        } else {
            val minuteGapAll =
                (h * 60 + m) - (hourNow * 60 + minuteNow)
            val minuteGap = minuteGapAll % 60
            val hourGap = (minuteGapAll - minuteGap) / 60
            resList.add(0, hourGap)
            resList.add(1, minuteGap)
            resList.add(2, 0)
        }
        return resList
    }
}
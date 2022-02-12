package com.example.hiclass.utils

import java.util.*


object CalendarUtil {
    fun getNowTime(): List<Int> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.timeZone = TimeZone.getTimeZone("GMT+8")
        return arrayListOf(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }

    fun judgeDayOut(hour: Int, min: Int): Boolean {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.timeZone = TimeZone.getTimeZone("GMT+8")
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        if (hour > h) {
            return false
        }
        if (hour < h) return true
        if (hour == h) {
            return min < m
        }
        return false
    }

    fun getTodayDate(): String {
        var res = ""
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.timeZone = TimeZone.getTimeZone("GMT+8")
        res =
            "${cal.get(Calendar.YEAR)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)}" +
                    "  周${cal.get(Calendar.DAY_OF_WEEK)}"
        return res
    }
}
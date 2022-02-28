package com.example.hiclass.utils

import java.util.*
import kotlin.collections.ArrayList


object CalendarUtil {
    private val dataList = listOf(
        "2.28", "3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9", "3.10",
        "3.11", "3.12", "3.13", "3.14", "3.15", "3.16", "3.17", "3.18", "3.19",
        "3.20", "3.21", "3.22", "3.23", "3.24", "3.25", "3.26", "3.27", "3.28",
        "3.29", "3.30", "3.31", "4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7",
        "4.8", "4.9", "4.10", "4.11", "4.12", "4.13", "4.14", "4.15", "4.16",
        "4.17", "4.18", "4.19", "4.20", "4.21", "4.22", "4.23", "4.24", "4.25",
        "4.26", "4.27", "4.28", "4.29", "4.30", "5.1", "5.2", "5.3",
        "5.4", "5.5", "5.6", "5.7", "5.8", "5.9", "5.10", "5.11", "5.12",
        "5.13", "5.14", "5.15", "5.16", "5.17", "5.18", "5.19", "5.20",
        "5.21", "5.22", "5.23", "5.24", "5.25", "5.26", "5.27", "5.28",
        "5.29", "5.30", "5.31", "6.1", "6.2", "6.3", "6.4", "6.5", "6.6", "6.7",
        "6.8", "6.9", "6.10", "6.11", "6.12", "6.13", "6.14", "6.15", "6.16",
        "6.17", "6.18", "6.19", "6.20", "6.21", "6.22", "6.23", "6.24", "6.25",
        "6.26", "6.27", "6.28", "6.29", "6.30", "7.1", "7.2", "7.3", "7.4",
        "7.5", "7.6", "7.7", "7.8", "7.9", "7.10", "7.11", "7.12", "7.13", "7.14",
        "7.15", "7.16", "7.17"
    )


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

    fun getWeekDate(): List<ArrayList<Int>> {
        val dL = ArrayList<Int>()
        val mL = ArrayList<Int>()
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.timeZone = TimeZone.getTimeZone("GMT+8")
        for (i in 2..7) {
            cal.set(Calendar.DAY_OF_WEEK, i)
            dL.add(cal.get(Calendar.DAY_OF_MONTH))
            mL.add(cal.get(Calendar.MONTH) + 1)
        }
        cal.set(Calendar.DAY_OF_WEEK, 1)
        cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) + 1)
        dL.add(cal.get(Calendar.DAY_OF_MONTH))
        mL.add(cal.get(Calendar.MONTH) + 1)
        return listOf(dL, mL)
    }

    fun getDate(week: Int, weekday: Int): String {
        return dataList[(week - 1) * 7 + weekday - 1]
    }

    fun getStaticWd(input: Int): Int {
        when (input) {
            0 -> return 1
            1 -> return 2
            2 -> return 3
            3 -> return 4
            4 -> return 5
            5 -> return 6
            6 -> return 0
        }
        return 7
    }

    fun getBoldDay(): List<Int> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.timeZone = TimeZone.getTimeZone("GMT+8")
        val dayNow = "${cal.get(Calendar.MONTH) + 1}.${cal.get(Calendar.DAY_OF_MONTH)}"
        for (i in dataList.indices) {
            if (dayNow == dataList[i]) {
                val terDayNow = (i + 1) % 7
                val termWeekNow = (i + 1 - terDayNow) / 7
                return listOf(termWeekNow, terDayNow)
            }
        }
        return listOf(-1, -1)
    }
}
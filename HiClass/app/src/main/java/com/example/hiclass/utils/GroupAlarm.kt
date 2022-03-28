package com.example.hiclass.utils

import com.example.hiclass.data_class.AlarmDataBean

object GroupAlarm {
    private val groupAlarm = mutableListOf<AlarmDataBean>()

    fun addG(list: List<AlarmDataBean>) {
        for (i in list) {
            groupAlarm.add(i)
        }
    }

    fun clearG() {
        groupAlarm.clear()
    }

    fun readG(): List<AlarmDataBean> {
        return groupAlarm
    }
}
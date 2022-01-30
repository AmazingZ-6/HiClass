package com.example.hiclass.data_class

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmDataBean(
    var alarmType:Int,
    var alarmName: String,
    var alarmTermDay: String,
    var alarmWeekday:String,
    var alarmTime: String,
    var alarmQueType:Int,
    var alarmInterval:Int,
    var alarmSwitch: Boolean
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

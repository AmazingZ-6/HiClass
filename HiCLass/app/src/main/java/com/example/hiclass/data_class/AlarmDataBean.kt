package com.example.hiclass.data_class

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmDataBean(
    var alarmName: String,
    var alarmTermDay: String,
    var alarmTime: String,
    var alarmQueType:String,
    var alarmInterval:String,
    var alarmSwitch: Boolean
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

package com.example.hiclass.data_class

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MatchInfoBean(var alarmId: Long, var tableId: Int, var itemId: Long) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
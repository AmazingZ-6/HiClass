package com.example.hiclass.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.hiclass.data_class.AlarmDataBean

@Dao
interface AlarmDao {
    @Insert
    fun insertAlarm(alarm: AlarmDataBean) :Long

    @Query("select * from AlarmDataBean")
    fun loadAllAlarms() :List<AlarmDataBean>

    @Delete
    fun deleteAlarm(alarm:AlarmDataBean)
}
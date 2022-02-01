package com.example.hiclass.dao

import androidx.room.*
import com.example.hiclass.data_class.AlarmDataBean

@Dao
interface AlarmDao {
    @Insert
    fun insertAlarm(alarm: AlarmDataBean) :Long

    @Query("select * from AlarmDataBean")
    fun loadAllAlarms() :List<AlarmDataBean>

    @Delete
    fun deleteAlarm(alarm:AlarmDataBean)

    @Update
    fun updateAlarm(alarm: AlarmDataBean)
}
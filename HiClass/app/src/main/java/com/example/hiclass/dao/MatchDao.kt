package com.example.hiclass.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.hiclass.data_class.MatchInfoBean

@Dao
interface MatchDao {
    @Insert
    fun insertInfo(match: MatchInfoBean): Long

    @Query("select * from MatchInfoBean")
    fun loadAllInfo(): List<MatchInfoBean>

    @Delete
    fun deleteAInfo(match: MatchInfoBean)
}
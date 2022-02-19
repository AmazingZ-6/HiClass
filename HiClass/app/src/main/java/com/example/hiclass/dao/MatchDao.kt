package com.example.hiclass.dao

import androidx.room.*
import com.example.hiclass.data_class.MatchInfoBean

@Dao
interface MatchDao {
    @Insert
    fun insertInfo(match: MatchInfoBean): Long

    @Query("select * from MatchInfoBean")
    fun loadAllInfo(): List<MatchInfoBean>

    @Delete
    fun deleteAInfo(match: MatchInfoBean)

    @Update
    fun updateInfo(match: MatchInfoBean)
}
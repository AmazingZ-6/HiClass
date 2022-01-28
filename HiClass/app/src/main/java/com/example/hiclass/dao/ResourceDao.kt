package com.example.hiclass.dao

import androidx.room.*
import com.example.hiclass.data_class.ResourceBean


@Dao
interface ResourceDao {
    @Insert
    fun insertQue(item: ResourceBean): Long


    @Query("select * from ResourceBean where type = :type and id = :id ")
    fun getRandomQue(id: Long, type: String): ResourceBean


    @Query("delete from ResourceBean")
    fun clearAllQues()
}
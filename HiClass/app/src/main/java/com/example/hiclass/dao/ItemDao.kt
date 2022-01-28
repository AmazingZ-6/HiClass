package com.example.hiclass.dao

import androidx.room.*
import com.example.hiclass.data_class.ItemDataBean

@Dao
interface ItemDao {

    @Insert
    fun insertItem(item:ItemDataBean) :Long

    @Update
    fun updateItem(item: ItemDataBean)

    @Query("select * from ItemDataBean")
    fun loadAllItems() :List<ItemDataBean>

    @Delete
    fun deleteItem(item:ItemDataBean)

    @Query("delete from ItemDataBean")
    fun clearAllItems()
}
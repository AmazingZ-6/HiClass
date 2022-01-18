package com.example.hiclass.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.schedule.itemDao
import kotlin.concurrent.thread

object GetDbInfo {

    fun getDbInfo(): MutableList<MutableLiveData<ItemDataBean>>{

        val itemListLiveData = mutableListOf<MutableLiveData<ItemDataBean>>()
        thread {
            for(item in itemDao.loadAllItems()){
                val itemLiveData =  MutableLiveData<ItemDataBean>()
                itemLiveData.value = item
                itemListLiveData.add(itemLiveData)
            }
        }
        return itemListLiveData
    }
}
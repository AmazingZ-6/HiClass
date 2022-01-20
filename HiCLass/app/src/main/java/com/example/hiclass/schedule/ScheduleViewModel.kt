package com.example.hiclass.schedule


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.utils.ChangeItem
import kotlin.concurrent.thread

class ScheduleViewModel : ViewModel() {


    val changeFlag: LiveData<Int>
        get() = _changeFlag


    private val _changeFlag = MutableLiveData<Int>()

    private val _index = MutableLiveData<Int>()


    fun updateFlag() {
        if (ChangeItem.itemUpdateFlag != 0) {
            _changeFlag.value = 1
        }
    }

    fun deleteFlag() {
        if (ChangeItem.changedItem != null) {
            val id = ChangeItem.changedItem!!.id
            val week = ChangeItem.changedItem!!.itemWeek
            for (entity in weekList[week - 1].dayItemList) {
                if (entity.id == id) {
                    weekList[week - 1].dayItemList.remove(entity)
                    thread {
                        itemDao.deleteItem(entity)
                    }
                    break
                }
            }
        }
        if (ChangeItem.itemDeleteFlag != 0) {
            _changeFlag.value = 2
        }
    }

    fun addFlag() {
        if (ChangeItem.AddItemList != null){
            for (entity in ChangeItem.AddItemList!!){
                weekList[entity.itemWeek-1].dayItemList.add(entity)
            }
        }
        if (ChangeItem.itemAddFlag != 0) {
            _changeFlag.value = 3
        }
    }


    fun setIndex(index: Int) {
        _index.value = index
    }


}
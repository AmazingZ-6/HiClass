package com.example.hiclass.schedule


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.hiclass.alarmList
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.itemDao
import com.example.hiclass.utils.ChangeItem
import com.example.hiclass.weekList
import kotlin.concurrent.thread

class ScheduleViewModel : ViewModel() {


    val changeFlag: LiveData<Int>
        get() = _changeFlag


    val position: LiveData<Int>
        get() = _position

    val exitFlag:LiveData<Boolean>
    get() = _exitFlag
    private val _exitFlag = MutableLiveData<Boolean>()

    private val _position = MutableLiveData<Int>()

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

    fun deleteBatchFlag() {
        if (ChangeItem.changedItem != null) {
            val name = ChangeItem.changedItem!!.itemName
            for (i in 0..19) {
                val temp = arrayListOf<ItemDataBean>()
                for (entity in weekList[i].dayItemList) {
                    if (entity.itemName == name) {
                        ChangeItem.deleteItemIdList.add(entity.id)
                        thread {
                            itemDao.deleteItem(entity)
                        }
                        temp.add(entity)
                    }
                }
                for(t in temp){
                    weekList[i].dayItemList.remove(t)
                }
            }

        }
        if (ChangeItem.itemBatchDeleteFlag != 0) {
            _changeFlag.value = 4
        }
    }

    fun addFlag() {

        if (ChangeItem.itemAddFlag != 0) {
            _changeFlag.value = 3
        }
    }


    fun setIndex(index: Int) {
        _index.value = index
    }

    fun updatePosition(index: Int) {
        _position.value = index
    }

    fun exit(){
        for (i in weekList){
            i.dayItemList.clear()
        }
        alarmList.clear()
    }


}
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

    val dateShowIndex: LiveData<Int>
        get() = _dateShowIndex
    private val _dateShowIndex = MutableLiveData<Int>()

    val exitFlag: LiveData<Boolean>
        get() = _exitFlag

    private val _exitFlag = MutableLiveData<Boolean>()

    private val _position = MutableLiveData<Int>()

    private val _changeFlag = MutableLiveData<Int>()

    private val _index = MutableLiveData<Int>()

    val isApply: LiveData<Boolean>
        get() = _isApply
    private val _isApply = MutableLiveData<Boolean>()

    val isApplyStorage: LiveData<Boolean>
        get() = _isApplyStorage
    private val _isApplyStorage = MutableLiveData<Boolean>()

    val updateBold: LiveData<Int>
        get() = _updateBold
    private val _updateBold = MutableLiveData<Int>()


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
                for (t in temp) {
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

    fun updateDate(index: Int) {
        _dateShowIndex.value = index
    }

    fun exit() {
        for (i in weekList) {
            i.dayItemList.clear()
        }
        alarmList.clear()
    }

    fun applyHasFinished() {
        _isApply.value = true
    }

    fun applyStoHasFinished() {
        _isApplyStorage.value = true
    }

    fun updateBold(index: Int) {
        _updateBold.value = index
    }

}
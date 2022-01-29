package com.example.hiclass.alarm_single

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hiclass.alarmDao
import com.example.hiclass.alarmList
import com.example.hiclass.data_class.AlarmDataBean
import kotlin.concurrent.thread

class SetSingleViewModel : ViewModel() {
    val typeSelectedPosition: LiveData<Int>
        get() = _typeSelectedPosition
    private val _typeSelectedPosition = MutableLiveData<Int>()

    val weekdaySelectedPosition: LiveData<MutableList<Int>>
        get() = _weekdaySelectedPosition
    private val _weekdaySelectedPosition = MutableLiveData<MutableList<Int>>()

    private val tempDayList = mutableListOf<Int>()

    val isFinished: LiveData<Boolean>
        get() = _isFinished
    private val _isFinished = MutableLiveData<Boolean>()

    fun weekdaySelected(position: Int) {
        if (position in tempDayList) {
            tempDayList.remove(position)
        } else {
            tempDayList.add(position)
        }
        _weekdaySelectedPosition.value = tempDayList
    }

    fun typeSelected(position: Int) {
        _typeSelectedPosition.value = position
    }

    fun saveAlarm(alarm: AlarmDataBean) {
        thread {
            alarm.id = alarmDao.insertAlarm(alarm)
            alarmList.add(alarm)
            _isFinished.postValue(true)
        }
    }

}
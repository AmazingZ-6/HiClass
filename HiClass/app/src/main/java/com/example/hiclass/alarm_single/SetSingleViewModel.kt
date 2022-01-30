package com.example.hiclass.alarm_single

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hiclass.alarmDao
import com.example.hiclass.alarmList
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.utils.CalendarUtil.judgeDayOut
import com.example.hiclass.utils.TypeSwitcher.charToInt
import kotlin.concurrent.thread

class SetSingleViewModel : ViewModel() {
    val typeSelectedPosition: LiveData<Int>
        get() = _typeSelectedPosition
    private val _typeSelectedPosition = MutableLiveData<Int>()

    val weekdaySelectedPosition: LiveData<MutableList<Int>>
        get() = _weekdaySelectedPosition
    private val _weekdaySelectedPosition = MutableLiveData<MutableList<Int>>()

    val tempDayList = mutableListOf<Int>()


    val isFinished: LiveData<Boolean>
        get() = _isFinished
    private val _isFinished = MutableLiveData<Boolean>()

    val isDayOut: LiveData<Boolean>
        get() = _isDayOut
    private val _isDayOut = MutableLiveData<Boolean>()

    fun weekdaySelected(position: Int) {

        if (position in tempDayList) {
            tempDayList.remove(position)
        } else {
            tempDayList.add(position)
        }

        if (tempDayList.size > 1) {
            if (7 in tempDayList) tempDayList.remove(7)
            if (8 in tempDayList) tempDayList.remove(8)
        }

        if (tempDayList.size < 1) {
            if (isDayOut.value == true) tempDayList.add(7)
            else tempDayList.add(8)
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

    fun judgeDay(hour: String, min: String) {
        _isDayOut.value = judgeDayOut(
            charToInt(hour[0]) * 10 + charToInt(hour[1]),
            charToInt(min[0]) * 10 + charToInt(min[1])
        )
    }

}
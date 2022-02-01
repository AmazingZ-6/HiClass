package com.example.hiclass.alarm

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hiclass.App
import com.example.hiclass.alarmList

class AlarmDisplayViewModel : ViewModel() {

    var updateAlarmId = -1L

    val clickedPos: MutableLiveData<Long>
        get() = _clickedPos
    private val _clickedPos = MutableLiveData<Long>()

    val switchFlag:LiveData<Int>
    get() = _switchFlag
    private val _switchFlag = MutableLiveData<Int>()

    fun click(alarmId: Long) {
        _clickedPos.value = alarmId
    }

    fun editClassAlarm() {

    }

    fun editIndividualAlarm() {

    }

    fun startClock(alarmId: Long) {
        for (i in alarmList) {
            if (i.id == alarmId) {
                i.alarmSwitch = true
                updateAlarmId = alarmId
                _switchFlag.value = 1
            }
        }
    }

    fun cancelClock(alarmId: Long) {
        for (i in alarmList) {
            if (i.id == alarmId) {
                i.alarmSwitch = false
                updateAlarmId = alarmId
                _switchFlag.value = 0
            }
        }
    }
}
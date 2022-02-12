package com.example.hiclass.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hiclass.alarmDao
import com.example.hiclass.alarmList
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.data_class.ClickBean
import com.example.hiclass.utils.ChangeAlarm
import kotlin.concurrent.thread

class AlarmDisplayViewModel : ViewModel() {

    var updateAlarmId = -1L

    val clickedPos: MutableLiveData<ClickBean>
        get() = _clickedPos
    private val _clickedPos = MutableLiveData<ClickBean>()

    val switchFlag: LiveData<Int>
        get() = _switchFlag
    private val _switchFlag = MutableLiveData<Int>()

    val refreshFlag: LiveData<Int>
        get() = _refreshFlag
    private val _refreshFlag = MutableLiveData<Int>()

    fun click(ck: ClickBean) {
        _clickedPos.value = ck
    }

    fun editClassAlarm() {

    }

    fun editIndividualAlarm() {

    }

    fun deleteAlarm(alarm: AlarmDataBean) {
        alarmList.remove(alarm)
        thread {
            alarmDao.deleteAlarm(alarm)
        }
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

    fun refresh() {
        if (ChangeAlarm.alarmAddFlag == 1){
            _refreshFlag.value = 1
        }
        if (ChangeAlarm.alarmUpdateFlag == 1){
            _refreshFlag.value = 2
        }
    }
}
package com.example.hiclass.alarm_set

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hiclass.alarmDao
import com.example.hiclass.alarmList
import com.example.hiclass.dao.MatchDao
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.data_class.MatchInfoBean
import com.example.hiclass.matchDao
import com.example.hiclass.matchList
import kotlin.concurrent.thread

class SetAlarmViewModel : ViewModel() {
    val typeSelectedPosition: LiveData<Int>
        get() = _typeSelectedPosition
    private val _typeSelectedPosition = MutableLiveData<Int>()

    val isFinished: LiveData<Boolean>
        get() = _isFinished
    private val _isFinished = MutableLiveData<Boolean>()

    fun selectedChange(position: Int) {
        _typeSelectedPosition.value = position
    }

    fun saveAlarm(alarm: AlarmDataBean, tableId: Int) {
        thread {
            alarm.id = alarmDao.insertAlarm(alarm)
            alarmList.add(alarm)
            val m = MatchInfoBean(alarm.id, tableId)
            m.id = matchDao.insertInfo(m)
            matchList.add(m)
            _isFinished.postValue(true)
        }
    }
}
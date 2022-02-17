package com.example.hiclass.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hiclass.alarmDao
import com.example.hiclass.alarmList
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.data_class.ClickBean
import com.example.hiclass.utils.ChangeAlarm
import com.example.hiclass.utils.ClockedAlarm
import com.example.hiclass.utils.TimeGapUtil.getTimeGap
import com.example.hiclass.utils.TypeSwitcher
import kotlin.concurrent.thread

class AlarmDisplayViewModel : ViewModel() {

    var updateAlarmId = -1L

    var editAlarmId = -1L

    val clockTime: LiveData<String>
        get() = _clockTime
    private val _clockTime = MutableLiveData<String>()

    val clickedPos: MutableLiveData<ClickBean>
        get() = _clickedPos
    private val _clickedPos = MutableLiveData<ClickBean>()

    val switchFlag: LiveData<Int>
        get() = _switchFlag
    private val _switchFlag = MutableLiveData<Int>()

    val refreshFlag: LiveData<Int>
        get() = _refreshFlag
    private val _refreshFlag = MutableLiveData<Int>()

    val editFlag: LiveData<Int>
        get() = _editFlag
    private val _editFlag = MutableLiveData<Int>()

    val clockedFlag: LiveData<Long>
        get() = _clockedFlag
    private val _clockedFlag = MutableLiveData<Long>()

    fun click(ck: ClickBean) {
        _clickedPos.value = ck
    }

    fun editClassAlarm() {

    }

    fun editIndividualAlarm(id: Long) {
        editAlarmId = id
        _editFlag.value = 1
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
        if (ChangeAlarm.alarmAddFlag == 1) {
            _refreshFlag.value = 1
        }
        if (ChangeAlarm.alarmUpdateFlag == 1) {
            _refreshFlag.value = 2
        }
    }

    fun refreshTime() {
        val openAlarm = mutableListOf<AlarmDataBean>()
        for (ala in alarmList) {
            if (ala.alarmSwitch) {
                openAlarm.add(ala)
            }
        }
        if (openAlarm.size == 0) {
            _clockTime.postValue("暂无开启闹钟")
        } else {
            val minGap = getTimeGap(openAlarm)
            _clockTime.postValue("${minGap.dayGap} 天 ${minGap.hGap} 小时 ${minGap.mGap} 分钟后响铃")
        }
    }

    fun clocked() {
        if (ClockedAlarm.cFlag) {
            _clockedFlag.value = ClockedAlarm.cAlarm?.id
        }
    }
}
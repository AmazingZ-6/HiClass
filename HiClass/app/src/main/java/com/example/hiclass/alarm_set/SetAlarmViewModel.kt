package com.example.hiclass.alarm_set

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SetAlarmViewModel:ViewModel() {
    val typeSelectedPosition :LiveData<Int>
    get() = _typeSelectedPosition
    private val _typeSelectedPosition = MutableLiveData<Int>()

    fun selectedChange(position:Int){
        _typeSelectedPosition.value = position
    }
}
package com.example.hiclass.schedule_week

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScheduleWeekViewModel : ViewModel() {

    val isApply: LiveData<Boolean>
        get() = _isApply
    private val _isApply = MutableLiveData<Boolean>()

    fun applyHasFinished() {
        _isApply.value = true
    }
}
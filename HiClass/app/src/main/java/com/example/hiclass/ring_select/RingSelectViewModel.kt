package com.example.hiclass.ring_select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RingSelectViewModel : ViewModel() {

    val clickedPosition: LiveData<Int>
        get() = _clickedPosition
    private val _clickedPosition = MutableLiveData<Int>()

    fun updateClick(pos: Int) {
        _clickedPosition.value = pos
    }

}
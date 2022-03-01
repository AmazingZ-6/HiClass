package com.example.hiclass.setting.ring_select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hiclass.data_class.ClickBean2

class RingSelectViewModel : ViewModel() {

    val clickedPosition: LiveData<ClickBean2>
        get() = _clickedPosition
    private val _clickedPosition = MutableLiveData<ClickBean2>()

    val selectedPos: LiveData<Int>
        get() = _selectedPos
    private val _selectedPos = MutableLiveData<Int>()

    fun updateClick(click: ClickBean2) {
        click.state = !click.state
        _clickedPosition.value = click
    }

    fun updateSelect(pos: Int) {
        _selectedPos.value = pos
    }

}
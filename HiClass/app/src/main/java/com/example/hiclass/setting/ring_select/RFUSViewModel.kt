package com.example.hiclass.setting.ring_select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hiclass.data_class.ClickBean2

class RFUSViewModel : ViewModel() {

    val clickedPos: LiveData<ClickBean2>
        get() = _clickedPos
    private val _clickedPos = MutableLiveData<ClickBean2>()

    fun updateClick(click: ClickBean2) {
        click.state = !click.state
        _clickedPos.value = click
    }
}
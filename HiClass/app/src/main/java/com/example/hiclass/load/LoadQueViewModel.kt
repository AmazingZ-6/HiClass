package com.example.hiclass.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoadQueViewModel : ViewModel() {
    val loadFlag: LiveData<Int>
        get() = _loadFlag
    private val _loadFlag = MutableLiveData<Int>()


    fun loadStateUpdate(state: Int) {
        when (state) {
            1 -> {
                _loadFlag.postValue(1)
            }
            2 -> {
                _loadFlag.postValue(2)
            }
        }
    }


}
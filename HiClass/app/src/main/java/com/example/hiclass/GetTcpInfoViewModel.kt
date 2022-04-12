package com.example.hiclass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GetTcpInfoViewModel : ViewModel() {
    val isLoginFailed: LiveData<Boolean>
        get() = _isLoginFailed
    private val _isLoginFailed = MutableLiveData<Boolean>()

    fun loginFailed() {
        _isLoginFailed.postValue(true)
    }
}
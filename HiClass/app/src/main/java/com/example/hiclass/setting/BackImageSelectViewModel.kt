package com.example.hiclass.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hiclass.data_class.ImageInfoBean

class BackImageSelectViewModel : ViewModel() {

    val selectedImage: LiveData<ImageInfoBean>
        get() = _selectedImage
    private val _selectedImage = MutableLiveData<ImageInfoBean>()

    fun selectedUpdate(info: ImageInfoBean) {
        _selectedImage.value = info
    }
}
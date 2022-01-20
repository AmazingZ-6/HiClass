package com.example.hiclass.data_class

import androidx.lifecycle.MutableLiveData

data class ItemEditBean(
    val itemWeekList: MutableLiveData<ArrayList<Int>>,
    var itemWeekDay: String,
    var itemTime: String,
    var itemName: String,
    var itemAddress: String,
    var itemTeacher: String,
    var itemRemarks: String
)

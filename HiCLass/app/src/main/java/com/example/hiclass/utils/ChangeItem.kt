package com.example.hiclass.utils

import com.example.hiclass.data_class.ItemDataBean

object ChangeItem {
    var itemUpdateFlag = 0
    var itemDeleteFlag = 0
    var itemAddFlag = 0
    var changedItem: ItemDataBean? = null
    var AddItemList: MutableList<ItemDataBean>? = null
}
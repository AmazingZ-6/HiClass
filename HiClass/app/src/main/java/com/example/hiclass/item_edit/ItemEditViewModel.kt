package com.example.hiclass.item_edit

import androidx.lifecycle.ViewModel
import com.example.hiclass.R
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.itemDao
import com.example.hiclass.weekList
import com.example.hiclass.utils.ChangeItem
import kotlin.concurrent.thread

class ItemEditViewModel : ViewModel() {

    var weeki: Int = -1
    var itemi: ItemDataBean? = null
    var name = itemi?.itemName
    var address = itemi?.itemAddress
    var teacher = itemi?.itemTeacher
    var idi: Long = -1

    var nameEdit = ""
    var addressEdit = ""
    var teacherEdit = ""
    var remarksEdit = ""
    var bgColor = 0
    var defaultColor = 0

    fun getItem() {
        if (idi != -1L && weeki != -1) {
            for (entity in weekList[weeki - 1].dayItemList) {
                if (entity.id == idi) {
                    itemi = entity
                    name = itemi!!.itemName
                    address = itemi!!.itemAddress
                    teacher = itemi!!.itemTeacher
                    bgColor = itemi!!.itemColor
                    when(bgColor){
                        R.color.colorTrans1 -> bgColor = -855785341
                        R.color.colorTrans2 -> bgColor = -855931004
                        R.color.colorTrans3 -> bgColor = -865631275
                    }
                    defaultColor = bgColor
                    break
                }
            }
        }
    }

    fun saveEditInfo() {

        if (idi != -1L && weeki != -1) {
            for (entity in weekList[weeki - 1].dayItemList) {
                if (entity.id == idi) {
                    entity.itemName = nameEdit
                    entity.itemAddress = addressEdit
                    entity.itemTeacher = teacherEdit
                    entity.itemColor = bgColor
                    thread {
                        itemDao.updateItem(entity)
                    }
                    ChangeItem.itemUpdateFlag = 1
                    ChangeItem.changedItem = entity
                    break
                }
            }

        }
    }

    fun deleteBatchInfo() {
        ChangeItem.changedItem = itemi
        ChangeItem.itemBatchDeleteFlag = 1
    }

    fun deleteInfo() {
        ChangeItem.changedItem = itemi
        ChangeItem.itemDeleteFlag = 1
    }
}






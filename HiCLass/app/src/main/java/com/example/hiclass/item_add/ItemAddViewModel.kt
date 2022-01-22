package com.example.hiclass.item_add

import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.data_class.ItemEditBean
import com.example.hiclass.itemDao
import com.example.hiclass.weekList
import com.example.hiclass.utils.ChangeItem
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class ItemAddViewModel : ViewModel() {

    val editList = mutableListOf<ItemEditBean>()
    private val saveList = mutableListOf<ItemDataBean>()
    var maxWeek = 20
    val nameViewGroup = mutableListOf<androidx.appcompat.widget.AppCompatEditText>()
    val teacherViewGroup = mutableListOf<androidx.appcompat.widget.AppCompatEditText>()
    val addressViewGroup = mutableListOf<androidx.appcompat.widget.AppCompatEditText>()
    val remarkViewGroup = mutableListOf<androidx.appcompat.widget.AppCompatEditText>()
    fun judgeType(list: ArrayList<Int>): Int {
        val oddListCount = list.count {
            it % 2 == 1
        }
        val evenCount = maxWeek / 2
        val oddCount = maxWeek - evenCount
        // 0表示不是全部的单周也不是全部的双周
        if (oddCount == oddListCount && oddCount == list.size) {
            return 1
        }
        if (evenCount == list.size && oddListCount == 0) {
            return 2
        }
        return 0
    }

    fun saveAddItemList() {
        for (i in 0 until editList.size) {
            editList[i].itemName = nameViewGroup[i].text.toString()
            editList[i].itemTeacher = teacherViewGroup[i].text.toString()
            editList[i].itemAddress = addressViewGroup[i].text.toString()
            editList[i].itemRemarks = remarkViewGroup[i].text.toString()
        }


        thread {
            for (entry in editList) {
                for (index in entry.itemWeekList.value!!) {
                    if (index != 0) {
                        val temp = ItemDataBean(
                            index,
                            entry.itemWeekDay.value.toString().substring(0, 3),
                            entry.itemWeekDay.value.toString().substring(3),
                            entry.itemName,
                            entry.itemAddress,
                            entry.itemTeacher,
                            entry.itemRemarks,
                            false,
                            ""
                        )
                        temp.id = itemDao.insertItem(temp)
                        saveList.add(temp)
                    }
                }
            }
            ChangeItem.AddItemList = saveList
            ChangeItem.itemAddFlag = 1
        }
        Thread.sleep(500)
    }
}


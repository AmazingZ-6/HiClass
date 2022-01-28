package com.example.hiclass.data_class

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hiclass.utils.TypeSwitcher
import kotlinx.android.parcel.Parcelize

@Entity
data class ItemDataBean(
    var itemWeek: Int,
    var itemWeekDay: String,
    var itemTime: String,
    var itemName: String,
    var itemAddress: String,
    var itemTeacher: String,
    var itemRemarks: String,
    var isSetAlarm: Boolean,
    var itemAlarmTime: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun getTimeString1(): String {
        return "第${itemWeek}周 $itemWeekDay$itemTime"
    }

    fun getTimeString2(): String {
        return itemWeekDay + itemTime
    }

    fun getTimeString3(): String {
        val weekDay = TypeSwitcher.charToChinese(itemWeekDay[2])
        return "第${itemWeek}周 星期$weekDay$itemTime"
    }

}

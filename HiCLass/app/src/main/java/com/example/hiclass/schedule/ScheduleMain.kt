package com.example.hiclass.schedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.example.hiclass.AppDatabase
import com.example.hiclass.R
import com.example.hiclass.dao.ItemDao
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.utils.TypeSwitcher
import java.io.File
import kotlin.concurrent.thread


lateinit var askClassInfo: String
var hasAskClassInfo: Boolean = false
lateinit var itemDao :ItemDao




class WeekItemList(week: Int) {
    val weekNum = week
    val dayItemList = mutableListOf<ItemDataBean>()
}


val weekList: MutableList<WeekItemList> = mutableListOf(
    WeekItemList(1),
    WeekItemList(2),
    WeekItemList(3),
    WeekItemList(4),
    WeekItemList(5),
    WeekItemList(6),
    WeekItemList(7),
    WeekItemList(8),
    WeekItemList(9),
    WeekItemList(10),
    WeekItemList(11),
    WeekItemList(12),
    WeekItemList(13),
    WeekItemList(14),
    WeekItemList(15),
    WeekItemList(16),
    WeekItemList(17),
    WeekItemList(18),
    WeekItemList(19),
    WeekItemList(20)
)

class ScheduleMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.currentItem = 15
        itemDao = initDatabase()
        initInfo()

    }

    private fun initDatabase(): ItemDao {
        return AppDatabase.getDatabase(this).itemDao()
    }

    private fun initInfo() {
        val file = File(applicationContext.filesDir, "load_info")
        val isReLogin = intent.getStringExtra("isReLogin")
        if (file.exists() && isReLogin != "true") {
            getDbInfo()
            hasAskClassInfo = true
        } else {
            askClassInfo = intent.getStringExtra("class_info").toString()
            val filename = "load_info"
            val fileContents = askClassInfo
            this.openFileOutput(filename, MODE_PRIVATE).use {
                it.write(fileContents.toByteArray())
            }
            if (askClassInfo.length > 10) {
                hasAskClassInfo = true
                initTimeTable()
            }
        }
    }

    private fun initTimeTable() {
        val classInfoSpilt = askClassInfo.split(",").toMutableList()
        classInfoSpilt[classInfoSpilt.size - 1] = classInfoSpilt[classInfoSpilt.size - 1].substring(
            0,
            classInfoSpilt[classInfoSpilt.size - 1].length - 2
        )
        for (i in 0 until classInfoSpilt.size) {
            if (classInfoSpilt[i].toCharArray()[0] == '[') {
                classInfoSpilt[i] = classInfoSpilt[i].substring(1, classInfoSpilt[i].length)
            } else {
                classInfoSpilt[i] = classInfoSpilt[i].substring(1, classInfoSpilt[i].length)
            }
        }

        val range = 0 until classInfoSpilt.size
        for (i in range step 5) {
            val timeTemp = classInfoSpilt[i + 1]
            val weekDay = timeTemp.substring(0, 3)
            val time = timeTemp.substring(3)
            val name = classInfoSpilt[i + 2]
            val address = classInfoSpilt[i + 3]
            val teacher = classInfoSpilt[i + 4]
            val week = if (classInfoSpilt[i].length == 13) {
                TypeSwitcher.charToInt(classInfoSpilt[i][11])
            } else {
                val t1 = TypeSwitcher.charToInt(classInfoSpilt[i][11])
                val t2 = TypeSwitcher.charToInt(classInfoSpilt[i][12])
                t1 * 10 + t2
            }
            val item = ItemDataBean(
                week, weekDay, time, name, address, teacher, "",
                false, ""
            )
            weekList[week - 1].dayItemList.add(item)
        }

        saveDbInfo()

    }

    private fun saveDbInfo() {
        thread {
            for (i in 0 until 20) {
                val dayT = weekList[i].dayItemList
                for (j in 0 until dayT.size) {
                    dayT[j].id = itemDao.insertItem(dayT[j])
                }
            }
        }
    }

    private fun getDbInfo() {
        thread {
            for (item in itemDao.loadAllItems()) {
                weekList[item.itemWeek - 1].dayItemList.add(item)
            }
        }
    }

}
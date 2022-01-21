package com.example.hiclass.schedule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.hiclass.AppDatabase
import com.example.hiclass.GetClassInfo
import com.example.hiclass.R
import com.example.hiclass.dao.ItemDao
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.item_add.ItemAdd
import com.example.hiclass.utils.TypeSwitcher
import com.google.android.material.navigation.NavigationView
import java.io.File
import kotlin.concurrent.thread


lateinit var askClassInfo: String
var hasAskClassInfo: Boolean = false
lateinit var itemDao: ItemDao


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

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_fragment)
        val navView: NavigationView = findViewById(R.id.nav_view)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.currentItem = 15
        val titleTemp = "第16周"
        toolbar.title = titleTemp
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.updatePosition(position + 1)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
//        val titleString = "HiClass"
//        toolbar.title = titleString
        viewModel.position.observe(this, Observer {
            toolbar.title = "第${viewModel.position.value}周"
        })
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_baseline_settings_24)
        }
//        toolbar.inflateMenu(R.menu.toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_login -> {
                    val intent = Intent(      //注意在这里杀掉服务进程
                        this,
                        GetClassInfo::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("isReLogin", "true")
                    startActivity(intent)
                    android.os.Process.killProcess(android.os.Process.myPid())
                    true
                }
//                        R.id.nav_timer -> {
//                            val intent = Intent(activity, AlarmEdit::class.java)
//                            startActivity(intent)
//                            true
//                        }
//                        R.id.nav_about_me -> {
//                            val intent = Intent(activity, AboutMe::class.java)
//                            startActivity(intent)
//                            true
//                        }

                else -> false
            }
        }
        itemDao = initDatabase()
        initInfo()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                val intent = Intent(this, ItemAdd::class.java)
                startActivity(intent)
            }
        }
        return true
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
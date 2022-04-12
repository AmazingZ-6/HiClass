package com.example.hiclass.alarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiclass.*
import com.example.hiclass.alarm_set.SetAlarm
import com.example.hiclass.alarm_single.SetAlarmSingle
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.data_class.MatchInfoBean
import com.example.hiclass.utils.CalendarUtil.getBoldDay
import com.example.hiclass.utils.CalendarUtil.getClassAutoClockTime
import com.example.hiclass.utils.CalendarUtil.judgeAlarmOff
import com.example.hiclass.utils.ChangeAlarm
import com.example.hiclass.utils.ClockedAlarm
import com.example.hiclass.utils.GroupAlarm
import com.example.hiclass.utils.StatusUtil
import com.example.hiclass.utils.TypeSwitcher.charToInt
import kotlinx.android.synthetic.main.activity_alarm_display.*
import kotlinx.android.synthetic.main.activity_set_alarm_clock.*
import java.util.*
import kotlin.concurrent.thread

class AlarmDisplay : AppCompatActivity() {

    private val alarmShow = mutableListOf<AlarmDataBean>()
    private lateinit var viewModel: AlarmDisplayViewModel
    private lateinit var adapter: AlarmDisplayAdapter

    data class InfoTemp(var tableId: Int, var itemId: Long)

    private val allInfoMap = mutableMapOf<AlarmDataBean, InfoTemp>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_alarm_display)
        viewModel = ViewModelProvider(this).get(AlarmDisplayViewModel::class.java)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_alarm_show)
        setSupportActionBar(toolbar)
        initAlarmShow()
        initRecycleView()
        initOptions()
        initTime()

        viewModel.clockTime.observe(this, Observer {
            next_clock_ring.text = it
        })

        viewModel.editFlag.observe(this, Observer {
            when (it) {
                0 -> {
                    for (i in alarmList) {
                        if (i.id == viewModel.editAlarmId) {
                            if (i.alarmSwitch) {
                                Toast.makeText(this, "请先关闭闹钟", Toast.LENGTH_SHORT).show()
                                break
                            } else {
                                val intent = Intent(this, SetAlarm::class.java)
                                intent.putExtra("alarm_id", viewModel.editAlarmId)
                                intent.putExtra("isUpdate", true)
                                startActivity(intent)
                            }
                        }
                    }

                }
                1 -> {
                    for (i in alarmList) {
                        if (i.id == viewModel.editAlarmId) {
                            if (i.alarmSwitch) {
                                Toast.makeText(this, "请先关闭闹钟", Toast.LENGTH_SHORT).show()
                                break
                            } else {
                                val intent = Intent(this, SetAlarmSingle::class.java)
                                intent.putExtra("alarm_id", viewModel.editAlarmId)
                                intent.putExtra("isUpdate", true)
                                startActivity(intent)
                            }
                        }

                    }
                }
            }
        })

        viewModel.switchFlag.observe(this, Observer {
            when (it) {
                0 -> {
                    cancelAlarm(viewModel.updateAlarmId)
                }
                1 -> {
                    startAlarm(viewModel.updateAlarmId)
                }
            }
        })

        viewModel.refreshFlag.observe(this, Observer {
            when (it) {
                1 -> addRefresh()
                2 -> updateRefresh()
            }
        })

        viewModel.clockedFlag.observe(this, Observer {
            for (index in alarmShow.indices) {
                if (alarmShow[index].id == it) {
                    adapter.notifyItemChanged(index)
                    ClockedAlarm.cFlag = false
                    ClockedAlarm.cAlarm = null
                    break
                }
            }
        })

        viewModel.preDeleteFlag.observe(this, Observer {
            val intent = Intent(this, AlarmService::class.java)
            intent.putExtra("alarm_id", it)
            intent.putExtra("isDelete", true)
            startService(intent)
//            for (i in alarmList){
//                if (i.id == it){
//                    viewModel.deleteAlarm(i)
//                    break
//                }
//            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        viewModel.clocked()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.alarm_show, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selectItems = arrayOf("本周", "下周")
        var selection = 0
        when (item.itemId) {
            R.id.menu_alarm_auto -> {
                AlertDialog.Builder(this).apply {
                    setTitle("自动为上午事项设置闹钟")
                    setSingleChoiceItems(
                        selectItems, 0
                    ) { _, which -> selection = which }
                    setCancelable(false)
                    setPositiveButton("确认") { _, _ ->
                        when (selection) {
                            0 -> {
                                autoSetClock(0)
                            }
                            1 -> {
                                autoSetClock(1)
                            }
                        }
                    }

                    setNegativeButton("取消") { _, _ ->

                    }
                }.show()
            }

        }
        return true
    }


    private fun initAlarmShow() {
        val tempList = mutableListOf<AlarmDataBean>()
        for (i in alarmList) {
            tempList.add(i)
        }
        for (j in tempList.indices) {
            var minHVal = 23
            var minMVal = 59
            var temp: AlarmDataBean? = null
            for (alarm in tempList) {
                val hourVal = charToInt(alarm.alarmTime[0]) * 10 + charToInt(alarm.alarmTime[1])
                val minuteVal = charToInt(alarm.alarmTime[3]) * 10 + charToInt(alarm.alarmTime[4])
                if (hourVal < minHVal) {
                    temp = alarm
                    minHVal = hourVal
                    minMVal = minuteVal
                } else if (hourVal == minHVal) {
                    if (minuteVal <= minMVal) {
                        temp = alarm
                        minHVal = hourVal
                        minMVal = minuteVal
                    }
                }
            }
            if (temp != null) {
                alarmShow.add(j, temp)
                for (i in tempList.indices) {
                    if (temp.id == tempList[i].id) {
                        tempList.removeAt(i)
                        break
                    }
                }
            }
        }
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        alarm_show_recyclerView.layoutManager = layoutManager
        adapter = AlarmDisplayAdapter(alarmShow, this, this)
        alarm_show_recyclerView.adapter = adapter
//        alarm_show_recyclerView.setItemViewCacheSize(10000)
    }

    private fun initOptions() {
        alarm_show_fab.setOnClickListener {
            val intent = Intent(this, SetAlarmSingle::class.java)
            intent.putExtra("isAdd", true)
            startActivity(intent)
        }
    }

    private fun startAlarm(alarmId: Long) {
        thread {
            for (a in alarmShow) {
                if (a.id == alarmId) {
                    alarmDao.updateAlarm(a)
                    break
                }
            }
        }
        val intent = Intent(this, AlarmService::class.java)
        intent.putExtra("alarm_id", alarmId)
        startService(intent)
    }

    private fun cancelAlarm(alarmId: Long) {
        thread {
            for (a in alarmShow) {
                if (a.id == alarmId) {
                    alarmDao.updateAlarm(a)
                    break
                }
            }
        }
        val intent = Intent(this, AlarmService::class.java)
        intent.putExtra("alarm_id", alarmId)
        startService(intent)
    }

    private fun addRefresh() {
        val addTemp = ChangeAlarm.changedAlarm!!
        val hVal = charToInt(addTemp.alarmTime[0]) * 10 + charToInt(addTemp.alarmTime[1])
        val mVal = charToInt(addTemp.alarmTime[3]) * 10 + charToInt(addTemp.alarmTime[4])
        if (alarmShow.size == 0) {
            alarmShow.add(0, addTemp)
            adapter.notifyItemInserted(alarmShow.size - 1)
        } else {
            for (i in alarmShow.indices) {
                val h =
                    charToInt(alarmShow[i].alarmTime[0]) * 10 + charToInt(alarmShow[i].alarmTime[1])
                val m =
                    charToInt(alarmShow[i].alarmTime[3]) * 10 + charToInt(alarmShow[i].alarmTime[4])
                if (hVal < h) {
                    alarmShow.add(i, addTemp)
                    adapter.notifyItemInserted(i)
                    adapter.notifyItemRangeChanged(i, alarmShow.size - i)
                    break
                }
                if (hVal == h) {
                    if (mVal <= m) {
                        alarmShow.add(i, addTemp)
                        adapter.notifyItemInserted(i)
                        adapter.notifyItemRangeChanged(i, alarmShow.size - i)
                        break
                    }
                }
                if (i == alarmShow.size - 1) {
                    alarmShow.add(addTemp)
                    adapter.notifyItemInserted(alarmShow.size - 1)
                }
            }
        }
        ChangeAlarm.alarmAddFlag = 0
        ChangeAlarm.alarmUpdateFlag = 0
        ChangeAlarm.changedAlarm = null
    }

    private fun updateRefresh() {
        for (index in alarmShow.indices) {
            if (alarmShow[index].id == ChangeAlarm.changedAlarm?.id) {
                alarmShow.removeAt(index)
                adapter.notifyItemRemoved(index)
                adapter.notifyItemRangeChanged(index, alarmShow.size - index)
                addRefresh()
                break
            }
        }
    }

    private fun initTime() {
        viewModel.refreshTime()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                viewModel.refreshTime()
            }
        }, 1000, 1000)
    }

    private fun autoSetClock(weekFlag: Int) {
        val alarms = mutableListOf<AlarmDataBean>()
        val alarmsTrue = mutableListOf<AlarmDataBean>()
        val matches = mutableListOf<MatchInfoBean>()
        val tables = mutableListOf<Int>()
        val alarmIds = mutableListOf<Long>()
        val itemIds = mutableListOf<Long>()
        val sp = getSharedPreferences("com.example.hiClass_preferences", MODE_PRIVATE)
        val advancedTime = sp.getString("auto_advanced", "0")
        val queTypeT = sp.getString("auto_que", "0")
        val queType = queTypeT?.get(0)?.let { charToInt(it) }
        val spanFlag = when (sp.getStringSet("auto_time", setOf("0"))) {
            setOf("0") -> arrayOf(1, 2, 3, 4)
            setOf("0", "1") -> arrayOf(1, 2, 3, 4, 5, 6, 7, 8)
            setOf("0", "1", "2") -> arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
            setOf("0", "2") -> arrayOf(1, 2, 3, 4, 9, 10, 11, 12)
            setOf("1") -> arrayOf(5, 6, 7, 8)
            setOf("2") -> arrayOf(9, 10, 11, 12)
            else -> arrayOf(1, 2, 3, 4)
        }
        val week = if (weekFlag == 0) getBoldDay()[0]
        else getBoldDay()[0] + 1
        for (item in weekList[week].dayItemList) {
            val startClassT = item.getTimeString3().split("-")[0]
            val startClass1 = charToInt(startClassT[startClassT.length - 2])
            val startClass2 = charToInt(startClassT[startClassT.length - 1])
            val startClass = startClass1 * 10 + startClass2
            if (startClass in spanFlag) {
                val alarmName = item.itemName
                val alarmTermDay = item.getTimeString3()

                val timeList = getClassAutoClockTime(startClass)
                val time = when (advancedTime) {
                    "0" -> listOf(timeList[0] - 1, 0)
                    "1" -> listOf(timeList[0] - 1, 15)
                    "2" -> listOf(timeList[0] - 1, 30)
                    "3" -> listOf(timeList[0] - 1, 45)
                    else -> {
                        listOf(timeList[0] - 1, 0)
                    }
                }
                val hour = if (time[0] <= 10) "0${time[0]}"
                else "${time[0]}"
                val minute = if (time[1] <= 10) "0${time[1]}"
                else "${time[1]}"
                val alarmTime = "${hour}:${minute}"
                val alarmInterval = 0
                val alarm = AlarmDataBean(
                    0, alarmName, alarmTermDay, "", alarmTime,
                    queType!!, alarmInterval, true
                )

                val tableId =
                    item.itemWeek * 7 + charToInt(item.itemWeekDay[2]) + charToInt(item.itemTime[1])
                alarms.add(alarm)
                tables.add(tableId)
                itemIds.add(item.id)
                val info = InfoTemp(tableId, item.id)
                allInfoMap[alarm] = info
            }
        }
        thread {
            for (i in alarms.indices) {
                if (!judgeAlarmOff(alarms[i])) {
                    alarms[i].id = alarmDao.insertAlarm(alarms[i])
                    alarmList.add(alarms[i])
                    alarmIds.add(alarms[i].id)
                    alarmsTrue.add(alarms[i])
                } else {
                    val tableId = allInfoMap[alarms[i]]?.tableId
                    val itemId = allInfoMap[alarms[i]]?.itemId
                    tables.remove(tableId)
                    itemIds.remove(itemId)
//                    junkList.add(alarms[i])
                }
//                ChangeAlarm.alarmAddFlag = 1
//                ChangeAlarm.changedAlarm = i
            }
//            for (junk in junkList) {
//                alarms.remove(junk)
//            }
            for (j in alarmIds) {
                for (z in alarms) {
                    if (j == z.id) {
                        val match =
                            MatchInfoBean(j, allInfoMap[z]!!.tableId, allInfoMap[z]!!.itemId)
                        match.id = matchDao.insertInfo(match)
                        matchList.add(match)
                    }
                }
            }
            allInfoMap.clear()
        }
        GroupAlarm.addG(alarms)
        val intent = Intent(this, AlarmService::class.java)
        intent.putExtra("isGroup", true)
        startService(intent)
        for (changeA in alarms) {
            if (!judgeAlarmOff(changeA)) {
                ChangeAlarm.changedAlarm = changeA
                addRefresh()
            }
        }
    }
}
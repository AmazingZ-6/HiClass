package com.example.hiclass.alarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiclass.R
import com.example.hiclass.alarmList
import com.example.hiclass.alarm_single.SetAlarmSingle
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.utils.ChangeAlarm
import com.example.hiclass.utils.StatusUtil
import com.example.hiclass.utils.TypeSwitcher.charToInt
import kotlinx.android.synthetic.main.activity_alarm_display.*
import kotlinx.android.synthetic.main.item_add_base.*

class AlarmDisplay : AppCompatActivity() {

    private val alarmShow = mutableListOf<AlarmDataBean>()
    private lateinit var viewModel: AlarmDisplayViewModel
    private lateinit var adapter: AlarmDisplayAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_alarm_display)
        viewModel = ViewModelProvider(this).get(AlarmDisplayViewModel::class.java)
        initAlarmShow()
        initRecycleView()
        initOptions()

        viewModel.editFlag.observe(this, Observer {
            when (it) {
                0 -> {

                }
                1 -> {
                    val intent = Intent(this, SetAlarmSingle::class.java)
                    intent.putExtra("alarm_id", viewModel.editAlarmId)
                    intent.putExtra("isUpdate", true)
                    startActivity(intent)
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
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
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
        val intent = Intent(this, AlarmService::class.java)
        intent.putExtra("alarm_id", alarmId)
        startService(intent)
    }

    private fun cancelAlarm(alarmId: Long) {
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
}
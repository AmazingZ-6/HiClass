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
import com.example.hiclass.utils.StatusUtil
import com.example.hiclass.utils.TypeSwitcher.charToInt
import kotlinx.android.synthetic.main.activity_alarm_display.*
import kotlinx.android.synthetic.main.item_add_base.*

class AlarmDisplay : AppCompatActivity() {

    private val alarmShow = mutableListOf<AlarmDataBean>()
    private lateinit var viewModel: AlarmDisplayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_alarm_display)
        viewModel = ViewModelProvider(this).get(AlarmDisplayViewModel::class.java)
        initAlarmShow()
        initRecycleView()
        initOptions()

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
    }

    override fun onResume() {
        super.onResume()
        alarmShow.clear()
        initAlarmShow()
        initRecycleView()
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
                tempList.remove(temp)
            }
        }
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        alarm_show_recyclerView.layoutManager = layoutManager
        val adapter = AlarmDisplayAdapter(alarmShow, this, this)
        alarm_show_recyclerView.adapter = adapter
//        alarm_show_recyclerView.setItemViewCacheSize(10000)
    }

    private fun initOptions() {
        alarm_show_fab.setOnClickListener {
            val intent = Intent(this, SetAlarmSingle::class.java)
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
}
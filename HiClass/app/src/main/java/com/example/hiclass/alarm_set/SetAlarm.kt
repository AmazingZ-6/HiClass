package com.example.hiclass.alarm_set

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.setting.App
import com.example.hiclass.R
import com.example.hiclass.alarm.AlarmService
import com.example.hiclass.alarmList
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.matchList
import com.example.hiclass.utils.CalendarUtil
import com.example.hiclass.utils.ChangeAlarm
import com.example.hiclass.utils.StatusUtil
import com.example.hiclass.utils.TypeSwitcher
import kotlinx.android.synthetic.main.activity_set_alarm.*
import kotlinx.android.synthetic.main.activity_set_alarm_single.*

class SetAlarm : AppCompatActivity() {
    private lateinit var viewModel: SetAlarmViewModel
    private val hourList = arrayOfNulls<String>(24)
    private val minuteList = arrayOfNulls<String>(60)
    private var minute = ""
    private var hour = ""
    private var minuteT = 0
    private var hourT = 0
    private lateinit var alarm: AlarmDataBean
    private var isExited = false
    private var tableId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_set_alarm)
        viewModel = ViewModelProvider(this).get(SetAlarmViewModel::class.java)
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        alarm_set_que_next.typeface = font
        alarm_set_que_next.text = App.context.resources.getString(R.string.icon_next)
        alarm_set_repeat_next.typeface = font
        alarm_set_repeat_next.text = App.context.resources.getString(R.string.icon_next)
        viewModel.typeSelectedPosition.observe(this, Observer {
            when (it) {
                0 -> alarm_selected_que_type.text = "常识题库"
                1 -> alarm_selected_que_type.text = "考研英语词汇题库"
            }
        })

        viewModel.isFinished.observe(this, Observer {
            if (it) {
                if (alarm.alarmSwitch) {
                    val intent = Intent(this, AlarmService::class.java)
                    intent.putExtra("alarm_id", alarm.id)
                    startService(intent)
                }
                if (intent.getBooleanExtra("isAdd", false)) {
                    ChangeAlarm.alarmAddFlag = 1
                    ChangeAlarm.changedAlarm = alarm
                }
                if (intent.getBooleanExtra("isUpdate", false)) {
                    ChangeAlarm.alarmUpdateFlag = 1
                    ChangeAlarm.changedAlarm = alarm
                }
                finish()
            }
        })
        initInfo()
        initEvents()
        initOptions()
    }

    private fun initInfo() {
        tableId = intent.getIntExtra("table_id", -1)
        val alarmId = intent.getLongExtra("alarm_id", -1)
        if (tableId != -1 && !isExited) {
            for (i in matchList) {
                if (tableId == i.tableId) {
                    for (a in alarmList) {
                        if (a.id == i.alarmId) {
                            alarm = a
                            break
                        }

                    }
                    isExited = true
                    break
                }
            }
        }
        if (alarmId != -1L && !isExited) {
            for (i in alarmList) {
                if (i.id == alarmId) {
                    alarm = i
                    isExited = true
                    break
                }
            }
        }
        if (isExited) {
            alarm_set_term_day.text = alarm.alarmTermDay
            alarm_set_name.setText(alarm.alarmName)
            viewModel.selectedChange(alarm.alarmQueType)
            hourT = TypeSwitcher.charToInt(alarm.alarmTime[0]) * 10 + TypeSwitcher.charToInt(
                alarm.alarmTime[1]
            )
            minuteT = TypeSwitcher.charToInt(alarm.alarmTime[3]) * 10 + TypeSwitcher.charToInt(
                alarm.alarmTime[4]
            )
        } else {
            hourT = CalendarUtil.getNowTime()[0]
            minuteT = CalendarUtil.getNowTime()[1]
            val alarmTermDay = intent.getStringExtra("term_day")
            val alarmName = intent.getStringExtra("name")
            alarm_set_term_day.text = alarmTermDay.toString()
            alarm_set_name.setText(alarmName.toString())
            viewModel.selectedChange(0)
        }
        hour = if (hourT < 10) "0$hourT"
        else "$hourT"

        minute = if (minuteT < 10) "0$minuteT"
        else "$minuteT"
    }

    private fun initEvents() {

        for (i in 0..23) {
            if (i < 10) {
                hourList[i] = "0$i"
            } else {
                hourList[i] = "$i"
            }
        }

        for (j in 0..59) {
            if (j < 10) {
                minuteList[j] = "0$j"
            } else {
                minuteList[j] = "$j"
            }
        }

        alarm_set_hour.displayedValues = hourList
        alarm_set_minute.displayedValues = minuteList
        alarm_set_hour.maxValue = 23
        alarm_set_hour.minValue = 0
        alarm_set_hour.value = hourT
        alarm_set_minute.maxValue = 59
        alarm_set_minute.minValue = 0
        alarm_set_minute.value = minuteT
        alarm_set_minute.wrapSelectorWheel = true
        alarm_set_hour.wrapSelectorWheel = true

        alarm_set_hour.setOnValueChangedListener { picker, oldVal, newVal ->
            hour = hourList[newVal].toString()
        }
        alarm_set_minute.setOnValueChangedListener { picker, oldVal, newVal ->
            minute = minuteList[newVal].toString()
        }
    }

    private fun initOptions() {
        alarm_set_que.setOnClickListener {
            val queSelection = SelectQueFragment.newInstance()
            queSelection.isCancelable = false
            queSelection.show(supportFragmentManager, "Select_que_type")
        }

        save_set_alarm.setOnClickListener {
            if (isExited) {
                updateAlarm()
            } else {
                saveAlarm()
            }
        }
    }

    private fun saveAlarm() {
        val alarmName = alarm_set_name.text.toString().ifEmpty {
            "闹钟"
        }
        val alarmTermDay = alarm_set_term_day.text
        val alarmTime = "$hour:$minute"
        val alarmQueType = viewModel.typeSelectedPosition.value!!
        val alarmSwitch = alarm_set_switcher.isChecked
        alarm = AlarmDataBean(
            0, alarmName, alarmTermDay as String, "", alarmTime,
            alarmQueType, 0, alarmSwitch
        )
        viewModel.saveAlarm(alarm, tableId)
    }

    private fun updateAlarm() {

    }
}
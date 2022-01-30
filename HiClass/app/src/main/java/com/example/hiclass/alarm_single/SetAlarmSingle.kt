package com.example.hiclass.alarm_single

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.App
import com.example.hiclass.R
import com.example.hiclass.alarm.AlarmService
import com.example.hiclass.alarmList
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.utils.CalendarUtil.getNowTime
import com.example.hiclass.utils.StatusUtil
import com.example.hiclass.utils.TypeSwitcher.charToInt
import com.example.hiclass.utils.TypeSwitcher.intToWeekday
import kotlinx.android.synthetic.main.activity_set_alarm_single.*

class SetAlarmSingle : AppCompatActivity() {

    private lateinit var viewModel: SetSingleViewModel
    private var isExisted = false
    private val hourList = arrayOfNulls<String>(24)
    private val minuteList = arrayOfNulls<String>(60)
    private var minuteT = 0
    private var hourT = 0
    private var minute = ""
    private var hour = ""
    private lateinit var alarm: AlarmDataBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_set_alarm_single)
        viewModel = ViewModelProvider(this).get(SetSingleViewModel::class.java)
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        alarm_set_que_next_single.typeface = font
        alarm_set_que_next_single.text = App.context.resources.getString(R.string.icon_next)
        alarm_set_repeat_next_single.typeface = font
        alarm_set_repeat_next_single.text = App.context.resources.getString(R.string.icon_next)
        alarm_set_term_day_next_single.typeface = font
        alarm_set_term_day_next_single.text = App.context.resources.getString(R.string.icon_next)
        viewModel.isFinished.observe(this, Observer {
            if (it) {
                if (alarm.alarmSwitch) {
                    val intent = Intent(this, AlarmService::class.java)
                    intent.putExtra("alarm_id", alarm.id)
                    startService(intent)
                }
                finish()
            }
        })

        viewModel.typeSelectedPosition.observe(this, Observer {
            when (it) {
                0 -> {
                    alarm_selected_que_type_single.text = "常识题库"
                }
                1 -> {
                    alarm_selected_que_type_single.text = "考研英语词汇题库"
                }
            }
        })

        viewModel.weekdaySelectedPosition.observe(this, Observer {
            var temp = ""
            if (it.size == 7) {
                alarm_selected_weekday.text = "每天"
            } else {
                for (i in it) {
                    temp = temp + " " + intToWeekday(i)
                }
                alarm_selected_weekday.text = temp
            }
        })

        viewModel.isDayOut.observe(this, Observer {
            if (it) alarm_selected_weekday.text = intToWeekday(7)
            else alarm_selected_weekday.text = intToWeekday(8)
        })
        initInfo()
        initEvents()
        initOptions()
    }

    private fun initInfo() {
        val idT = intent.getLongExtra("alarm_id", -1)
        if (idT != -1L) {
            isExisted = true
            for (entity in alarmList) {
                if (idT == entity.id) {
                    for (i in entity.alarmWeekday.split(",")) {
                        viewModel.weekdaySelected(charToInt(i[0]))
                    }
//                    for (i in entity.alarmWeekday) {
//                        viewModel.weekdaySelected(i)
//                    }

                    viewModel.typeSelected(entity.alarmQueType)
                    hourT = charToInt(entity.alarmTime[0]) * 10 + charToInt(entity.alarmTime[1])
                    minuteT = charToInt(entity.alarmTime[3]) * 10 + charToInt(entity.alarmTime[4])
                    break
                }
            }
        } else {
            viewModel.weekdaySelected(8)
            viewModel.typeSelected(0)
            hourT = getNowTime()[0]
            minuteT = getNowTime()[1]
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

        alarm_set_hour_single.displayedValues = hourList
        alarm_set_minute_single.displayedValues = minuteList
        alarm_set_hour_single.maxValue = 23
        alarm_set_hour_single.minValue = 0
        alarm_set_hour_single.value = hourT
        alarm_set_minute_single.maxValue = 59
        alarm_set_minute_single.minValue = 0
        alarm_set_minute_single.value = minuteT
        alarm_set_minute_single.wrapSelectorWheel = true
        alarm_set_hour_single.wrapSelectorWheel = true

        alarm_set_hour_single.setOnValueChangedListener { picker, oldVal, newVal ->
            hour = hourList[newVal].toString()
            if (7 in viewModel.tempDayList || 8 in viewModel.tempDayList) {
                viewModel.judgeDay(hour, minute)
            }
        }

        alarm_set_minute_single.setOnValueChangedListener { picker, oldVal, newVal ->
            minute = minuteList[newVal].toString()
            if (7 in viewModel.tempDayList || 8 in viewModel.tempDayList) {
                viewModel.judgeDay(hour, minute)
            }
        }


    }

    private fun initOptions() {

        alarm_set_term_day_single.setOnClickListener {
            val termDaySelection = SelectTermDayFragment.newInstance()
            termDaySelection.isCancelable = false
            termDaySelection.show(supportFragmentManager, "select_term_day")
        }

        alarm_set_que_single.setOnClickListener {
            val queSelection = SelectQueSinFragment.newInstance()
            queSelection.isCancelable = false
            queSelection.show(supportFragmentManager, "Select_que_type")
        }

        btn_save_single_alarm.setOnClickListener {
            saveAlarm()
        }
    }

    private fun saveAlarm() {
        var alarmWeekday = ""
        val alarmName = alarm_set_name_single.text.toString()
        for (i in viewModel.weekdaySelectedPosition.value!!) {
            alarmWeekday += "$i,"
        }
        val alarmTime = "$hour:$minute"
        val alarmQueType = viewModel.typeSelectedPosition.value
        val alarmSwitch = alarm_set_switcher_single.isChecked
        alarm = AlarmDataBean(
            1, alarmName, "", alarmWeekday, alarmTime,
            alarmQueType!!, 0, alarmSwitch
        )
        viewModel.saveAlarm(alarm)
    }
}
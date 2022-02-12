package com.example.hiclass.alarm_set

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hiclass.setting.App
import com.example.hiclass.R
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_set_alarm.*

class SetAlarm : AppCompatActivity() {
    private val hourList = arrayOfNulls<String>(47)
    private val minuteList = arrayOfNulls<String>(119)
    private var minute = ""
    private var hour = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_set_alarm)
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        alarm_set_que_next.typeface = font
        alarm_set_que_next.text = App.context.resources.getString(R.string.icon_next)
        alarm_set_repeat_next.typeface = font
        alarm_set_repeat_next.text = App.context.resources.getString(R.string.icon_next)
        val alarmTermDay = intent.getStringExtra("term_day")
        val alarmName = intent.getStringExtra("name")
        alarm_set_term_day.text = alarmTermDay.toString()
        alarm_set_name.setText(alarmName.toString())
        initEvents()
        initOptions()
    }

    private fun initEvents() {

        for (a in 23 downTo 1) {
            if (a < 10) {
                hourList[23 - a] = "0$a"
            } else {
                hourList[23 - a] = "$a"
            }
        }
        for (i in 0..23) {
            if (i < 10) {
                hourList[23 + i] = "0$i"
            } else {
                hourList[23 + i] = "$i"
            }
        }

        for (b in 59 downTo 1) {
            if (b < 10) {
                minuteList[59 - b] = "0$b"
            } else {
                minuteList[59 - b] = "$b"
            }
        }

        for (j in 0..59) {
            if (j < 10) {
                minuteList[59 + j] = "0$j"
            } else {
                minuteList[59 + j] = "$j"
            }
        }

        alarm_set_hour.displayedValues = hourList
        alarm_set_minute.displayedValues = minuteList
        alarm_set_hour.maxValue = 46
        alarm_set_hour.minValue = 0
        alarm_set_hour.value = 23
        alarm_set_minute.maxValue = 118
        alarm_set_minute.minValue = 0
        alarm_set_minute.value = 59

        alarm_set_hour.setOnValueChangedListener { picker, oldVal, newVal ->
            hour = hourList[newVal].toString()
        }
        alarm_set_minute.setOnValueChangedListener { picker, oldVal, newVal ->
            minute = minuteList[newVal].toString()
        }


    }

    private fun initOptions(){
        alarm_set_que.setOnClickListener {
            val queSelection = SelectQueFragment.newInstance()
            queSelection.isCancelable = false
            queSelection.show(supportFragmentManager,"Select_que_type")
        }
    }
}
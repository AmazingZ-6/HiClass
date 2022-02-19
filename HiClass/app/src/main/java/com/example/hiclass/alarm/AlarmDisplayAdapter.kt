package com.example.hiclass.alarm

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.hiclass.setting.App
import com.example.hiclass.R
import com.example.hiclass.data_class.AlarmDataBean
import com.example.hiclass.data_class.ClickBean
import com.example.hiclass.utils.CalendarUtil
import com.example.hiclass.utils.TypeSwitcher
import com.example.hiclass.utils.TypeSwitcher.charToInt
import com.example.hiclass.utils.TypeSwitcher.intToWeekday

class AlarmDisplayAdapter(
    private val alarmShow: MutableList<AlarmDataBean>,
    owner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<AlarmDisplayAdapter.ViewHolder>() {

    private val viewModel = ViewModelProvider(owner).get(AlarmDisplayViewModel::class.java)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val visiView: LinearLayoutCompat =
            view.findViewById(R.id.whole_single_view)
        val goneView: LinearLayoutCompat =
            view.findViewById(R.id.alarm_show_single_gone)
        val timeShow: AppCompatTextView = view.findViewById(R.id.alarm_show_single_time)
        val typeIcon: AppCompatTextView = view.findViewById(R.id.alarm_show_single_type_icon)
        val typeName: AppCompatTextView = view.findViewById(R.id.alarm_show_single_type)
        val nameShow: AppCompatTextView = view.findViewById(R.id.alarm_show_single_name)
        val switcher: SwitchCompat = view.findViewById(R.id.alarm_show_switcher_single)
        val btnDel: AppCompatTextView = view.findViewById(R.id.alarm_single_delete)
        val btnEdit: AppCompatTextView = view.findViewById(R.id.alarm_single_edit)
    }


    override fun getItemCount() = alarmShow.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alarm_show_single, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.goneView.visibility = GONE
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarmShow[position]
        viewModel.clickedPos.observe(lifecycleOwner, Observer {
            if (it.alarmId == alarm.id) {
                if (it.state) {
                    holder.goneView.visibility = VISIBLE
                } else {
                    holder.goneView.visibility = GONE
                }
            } else {
                holder.goneView.visibility = GONE
            }
        })

        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        holder.btnDel.typeface = font
        holder.btnDel.text = App.context.resources.getString(R.string.icon_delete_edit)
        holder.btnEdit.typeface = font
        holder.btnEdit.text = App.context.resources.getString(R.string.icon_edit)
        holder.timeShow.text = alarm.alarmTime
        holder.switcher.isChecked = alarm.alarmSwitch

        if (alarm.alarmType == 0) {
            holder.typeIcon.typeface = font
            holder.typeIcon.text = App.context.resources.getString(R.string.icon_class_alarm)
            holder.typeName.text = "课表闹钟"
        } else {
            holder.typeIcon.typeface = font
            holder.typeIcon.text = App.context.resources.getString(R.string.icon_individualization)
            holder.typeName.text = "自定义闹钟"
        }

        val weekdayTemp = mutableListOf<Int>()
        var nameTemp = "   " + alarm.alarmName + " , " + "不重复 , "
        for (i in alarm.alarmWeekday.split(",")) {
            if (i.isNotEmpty()) {
                weekdayTemp.add(charToInt(i[0]))
                weekdayTemp.sort()
            }
        }
        if (alarm.alarmType == 1) {
            if (weekdayTemp.size == 7) {
                nameTemp += "每天"
            } else {
                for (j in weekdayTemp) {
                    nameTemp = if (j < 7) {
                        nameTemp + intToWeekday(j) + " "
                    } else {
                        val t = if (CalendarUtil.judgeDayOut(
                                charToInt(alarm.alarmTime[0]) * 10 + charToInt(alarm.alarmTime[1]),
                                charToInt(alarm.alarmTime[3]) * 10 + charToInt(alarm.alarmTime[4])
                            )
                        ) {
                            "明天"
                        } else {
                            "今天"
                        }
                        "$nameTemp$t "
                    }

                }
            }
        }
        if (alarm.alarmType == 0) {
            nameTemp =
                "   " + alarm.alarmName + " , " + TypeSwitcher.subStTermDay(alarm.alarmTermDay)[1] + "周 , " + TypeSwitcher.subStTermDay(
                    alarm.alarmTermDay
                )[0]
        }
        holder.nameShow.text = nameTemp
        holder.visiView.setOnClickListener {
            val state = holder.goneView.visibility == GONE
            val ckTemp = ClickBean(alarm.id, state)
            viewModel.click(ckTemp)
        }

        holder.btnEdit.setOnClickListener {
            if (alarm.alarmType == 0) {
                viewModel.editClassAlarm(alarm.id)
            } else {
                viewModel.editIndividualAlarm(alarm.id)
            }
        }

        holder.btnDel.setOnClickListener {
            viewModel.deleteAlarm(alarm)
//            alarmShow.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, alarmShow.size - position)  //回调onBindViewHolder
            alarmShow.removeAt(position)
        }

        holder.switcher.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    viewModel.startClock(alarm.id)
                }
                false -> {
                    viewModel.cancelClock(alarm.id)
                }
            }
        }

    }

}
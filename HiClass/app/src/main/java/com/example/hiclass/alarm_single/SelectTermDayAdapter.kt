package com.example.hiclass.alarm_single

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.hiclass.setting.App
import com.example.hiclass.R

class SelectTermDayAdapter(
    private val wList: List<String>, private val owner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<SelectTermDayAdapter.ViewHolder>() {

    private lateinit var viewModel: SetSingleViewModel

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: AppCompatTextView = view.findViewById(R.id.weekday_name)
        val iconView: AppCompatTextView = view.findViewById(R.id.weekday_icon)
        val lineView: LinearLayoutCompat = view.findViewById(R.id.weekday_select_single)
        val selectedView: AppCompatTextView = view.findViewById(R.id.weekday_select)

    }

    override fun getItemCount() = wList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weekday_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weekday = wList[position]
        viewModel = ViewModelProvider(owner).get(SetSingleViewModel::class.java)
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        holder.selectedView.typeface = font
        holder.selectedView.text = App.context.resources.getString(R.string.icon_load_ok)
        holder.nameView.text = weekday
        holder.iconView.typeface = font
        holder.iconView.text = App.context.resources.getString(R.string.icon_name)
        holder.lineView.setOnClickListener {
            viewModel.weekdaySelected(position)
        }
        viewModel.weekdaySelectedPosition.observe(lifecycleOwner, Observer {
            when(position in it){
                true -> {
                    holder.selectedView.visibility = VISIBLE
                }
                false ->{
                    holder.selectedView.visibility = INVISIBLE
                }
            }
        })
    }
}
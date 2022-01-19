package com.example.hiclass.item_add

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiclass.App
import com.example.hiclass.R

class ItemAddAdapter(private val addList: List<Int>) :
    RecyclerView.Adapter<ItemAddAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: AppCompatTextView = view.findViewById(R.id.recycle_name)
        val timeView: AppCompatTextView = view.findViewById(R.id.recycle_time)
        val weekView: AppCompatTextView = view.findViewById(R.id.recycle_week)
        val teacherView: AppCompatTextView = view.findViewById(R.id.recycle_teacher)
        val addressView: AppCompatTextView = view.findViewById(R.id.recycle_address)
        val remarkView: AppCompatTextView = view.findViewById(R.id.recycle_remark)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val add = addList[position]
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        holder.nameView.typeface = font
        holder.nameView.text = App.context.resources.getString(R.string.icon_name)
        holder.timeView.typeface = font
        holder.timeView.text = App.context.resources.getString(R.string.icon_clock)
        holder.weekView.typeface = font
        holder.weekView.text = App.context.resources.getString(R.string.icon_thing)
        holder.teacherView.typeface = font
        holder.teacherView.text = App.context.resources.getString(R.string.icon_user)
        holder.addressView.typeface = font
        holder.addressView.text = App.context.resources.getString(R.string.icon_address)
        holder.remarkView.typeface = font
        holder.remarkView.text = App.context.resources.getString(R.string.icon_remark)
    }


    override fun getItemCount() = addList.size

}
package com.example.hiclass.alarm_single

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
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

class SelectQueSinAdapter(
    private val queList: List<String>, private val owner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<SelectQueSinAdapter.ViewHolder>() {

    private lateinit var viewModel: SetSingleViewModel

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: AppCompatTextView = view.findViewById(R.id.que_name)
        val iconView: AppCompatTextView = view.findViewById(R.id.que_name_icon)
        val lineView: LinearLayoutCompat = view.findViewById(R.id.que_select_single)
        val selectedView: AppCompatTextView = view.findViewById(R.id.que_name_select)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.que_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewModel = ViewModelProvider(owner).get(SetSingleViewModel::class.java)
        val que = queList[position]
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        holder.selectedView.typeface = font
        holder.selectedView.text = App.context.resources.getString(R.string.icon_load_ok)
        holder.nameView.text = que
        holder.iconView.typeface = font
        holder.iconView.text = App.context.resources.getString(R.string.icon_name)
        holder.lineView.setOnClickListener {
            viewModel.typeSelected(position)
        }
        viewModel.typeSelectedPosition.observe(lifecycleOwner, Observer {
            if (viewModel.typeSelectedPosition.value != position) {
                holder.selectedView.visibility = View.INVISIBLE
            } else {
                holder.selectedView.visibility = View.VISIBLE
            }
        })
    }

    override fun getItemCount() = queList.size
}
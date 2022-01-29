package com.example.hiclass.item_add

import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.example.hiclass.App
import com.example.hiclass.R
import com.example.hiclass.data_class.ItemEditBean
import com.example.hiclass.utils.TypeSwitcher

class ItemAddAdapter(
    private val addList: List<Int>, private val supportManager: FragmentManager,
    private val owner: ViewModelStoreOwner, private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<ItemAddAdapter.ViewHolder>() {

    private val viewModel = ViewModelProvider(owner).get(ItemAddViewModel::class.java)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: AppCompatTextView = view.findViewById(R.id.recycle_name)
        val nameEdit: AppCompatEditText = view.findViewById(R.id.et_name)
        val timeView: AppCompatTextView = view.findViewById(R.id.recycle_time)
        val weekView: AppCompatTextView = view.findViewById(R.id.recycle_week)
        val teacherView: AppCompatTextView = view.findViewById(R.id.recycle_teacher)
        val teacherEdit: AppCompatEditText = view.findViewById(R.id.et_teacher)
        val addressView: AppCompatTextView = view.findViewById(R.id.recycle_address)
        val addressEdit: AppCompatEditText = view.findViewById(R.id.et_address)
        val remarkView: AppCompatTextView = view.findViewById(R.id.recycle_remark)
        val remarkEdit: AppCompatEditText = view.findViewById(R.id.et_remark)
        val timeSelection: AppCompatTextView = view.findViewById(R.id.et_time)
        val weekSelection: AppCompatTextView = view.findViewById(R.id.et_week)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_detail, parent, false)
        val viewHolder = ViewHolder(view)
        val position = viewHolder.adapterPosition
        viewHolder.timeSelection.setOnClickListener {
            val timeSelection = SelectTimeFragment.newInstance(position)
            timeSelection.isCancelable = false
            timeSelection.show(supportManager, "select_time")
        }
        viewHolder.weekSelection.setOnClickListener {
            val weekSelection = SelectWeekFragment.newInstance(position)
            weekSelection.isCancelable = false
            weekSelection.show(supportManager, "select_week")
        }
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
        holder.timeSelection.setOnClickListener {
            val timeSelection = SelectTimeFragment.newInstance(position)
            timeSelection.isCancelable = false
            timeSelection.show(supportManager, "select_time")
        }
        holder.weekSelection.setOnClickListener {
            val weekSelection = SelectWeekFragment.newInstance(position)
            weekSelection.isCancelable = false
            weekSelection.show(supportManager, "select_week")
        }
        val mutableList = MutableLiveData<ArrayList<Int>>()
        val initLiveData = arrayListOf(0)
        mutableList.value = initLiveData
        val initLiveStr = MutableLiveData<String>()
        initLiveStr.value = ""
        val itemAdd = ItemEditBean(
            mutableList, initLiveStr, "",
            "", "", "", ""
        )
        viewModel.editList.add(position, itemAdd)
        viewModel.nameViewGroup.add(position, holder.nameEdit)
        viewModel.teacherViewGroup.add(position, holder.teacherEdit)
        viewModel.addressViewGroup.add(position, holder.addressEdit)
        viewModel.remarkViewGroup.add(position, holder.remarkEdit)

        viewModel.editList[position].itemWeekList.observe(lifecycleOwner, Observer {
            val temp = viewModel.editList[position].itemWeekList.value
            var str = "周数   "
            if (temp != null) {
                if (temp.size != 20) {
                    for (i in temp) {
                        if (i != 0) {
                            str = "$str$i "
                        }
                    }
                } else {
                    str = "$str 1 - 20"
                }

            }
            holder.weekSelection.hint = str
        })
        viewModel.editList[position].itemWeekDay.observe(lifecycleOwner, Observer {
            val temp = viewModel.editList[position].itemWeekDay.value.toString()
            val str = "时间   "
            if (temp.isNotEmpty()) {
                holder.timeSelection.hint =
                    str + temp.substring(0, 2) +
                            TypeSwitcher.charToChinese(temp[2]) +
                            "   " + temp.substring(3)
            }
        })
    }


    override fun getItemCount() = addList.size


}
package com.example.hiclass.item_add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.R
import com.example.hiclass.utils.TypeSwitcher
import kotlinx.android.synthetic.main.fragment_select_time.*


class SelectTimeFragment : DialogFragment() {

    var position = -1
    private val viewModel by activityViewModels<ItemAddViewModel>()
    private val dayList = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    private val nodeList = arrayOfNulls<String>(12)
    var day = 1
    var start = 1
    var end = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(activity as ItemAdd).get(ItemAddViewModel::class.java)
        initNodeList()
        wp_day.displayedValues = dayList
        wp_start.displayedValues = nodeList
        wp_end.displayedValues = nodeList
        initEvent()
    }

    private fun initNodeList() {
        for (i in 1..12) {
            nodeList[i - 1] = "第 $i 节"
        }
    }

    private fun initEvent() {
        wp_day.minValue = 0
        wp_day.maxValue = dayList.size - 1
        if (day < 1) day = 1
        if (day > 7) day = 7
        wp_day.value = day - 1

        wp_start.minValue = 0
        wp_start.maxValue = nodeList.size - 1
        if (start < 1) start = 1
        wp_start.value = start - 1

        wp_end.minValue = 0
        wp_end.maxValue = nodeList.size - 1
        if (start < 1) start = 1
        wp_end.value = end - 1

        wp_day.setOnValueChangedListener { _, _, newVal ->
            day = newVal + 1
        }

        wp_start.setOnValueChangedListener { _, _, newVal ->
            start = newVal + 1
            if (end < start) {
                wp_end.smoothScrollToValue(start - 1, false)
                end = start
            }
        }

        wp_end.setOnValueChangedListener { _, _, newVal ->
            end = newVal + 1
            if (end < start) {
                wp_end.smoothScrollToValue(start - 1, false)
                end = start
            }
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_save.setOnClickListener {
            viewModel.editList[position].itemWeekDay.value = "星期$day" + "第$start" + "-" +"$end" + "节"
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: Int) =
            SelectTimeFragment().apply {
                arguments = Bundle().apply {
                    putInt("position", arg)
                }
            }
    }
}
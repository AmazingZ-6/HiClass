package com.example.hiclass.alarm_single

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiclass.R
import kotlinx.android.synthetic.main.fragment_select_term_day.*


class SelectTermDayFragment : DialogFragment() {

    private lateinit var dayList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_term_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initDayInfo()
        initEvents()
    }

    private fun initDayInfo() {
        dayList = arrayListOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    }

    private fun initEvents() {
        val adapter =
            SelectTermDayAdapter(dayList, activity as SetAlarmSingle, activity as SetAlarmSingle)
        rv_select_term_day.adapter = adapter
        val layoutManager = LinearLayoutManager(context)
        rv_select_term_day.layoutManager = layoutManager
        btn_cancel_term_day.setOnClickListener {
            dismiss()
        }
        btn_save_term_day.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SelectTermDayFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
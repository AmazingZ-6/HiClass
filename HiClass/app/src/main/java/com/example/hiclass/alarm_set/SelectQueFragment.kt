package com.example.hiclass.alarm_set

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiclass.R
import kotlinx.android.synthetic.main.fragment_select_que.*

class SelectQueFragment : DialogFragment() {


    private lateinit var viewModel: SetAlarmViewModel
    private lateinit var queName: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        viewModel = ViewModelProvider(activity as SetAlarm).get(SetAlarmViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_que, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initQueInfo()
        initEvents()
    }

    private fun initQueInfo() {
        val prefs = activity?.getSharedPreferences("resource_settings", Context.MODE_PRIVATE)
        val isEngLoaded = prefs?.getBoolean("english_1000", false)
        if (isEngLoaded == true) {
            queName = arrayListOf("无(默认常识题库)", "英语单词题库")
        }
    }

    private fun initEvents() {
        val adapter = SelectQueAdapter(queName, activity as SetAlarm, activity as SetAlarm)
        rv_select_que.adapter = adapter
        val layoutManager = LinearLayoutManager(context)
        rv_select_que.layoutManager = layoutManager
        btn_cancel_que_type.setOnClickListener {
            dismiss()
        }
        btn_save_que_type.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SelectQueFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
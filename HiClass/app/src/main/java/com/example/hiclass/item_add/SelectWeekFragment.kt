package com.example.hiclass.item_add

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.BaseDialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.hiclass.R
import com.example.hiclass.widget.SelectedRecyclerView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_select_week.*
import splitties.resources.styledColor


class SelectWeekFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_select_week
    var position = -1
    private lateinit var viewModel: ItemAddViewModel
    private val liveData = MutableLiveData<ArrayList<Int>>()
    private val result = ArrayList<Int>()
    private var colorSurface: Int = Color.BLACK


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
        viewModel = ViewModelProvider(activity as ItemAdd).get(ItemAddViewModel::class.java)
        colorSurface = requireContext().styledColor(R.attr.colorOnSurface)
        liveData.observe(this, Observer {
            if (it?.size == viewModel.maxWeek) {
                tv_all.setTextColor(Color.WHITE)
                tv_all.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.select_textview_bg)
            }
            if (it?.size != viewModel.maxWeek) {
                tv_all.setTextColor(colorSurface)
                tv_all.background = null
            }
            val flag = viewModel.judgeType(it!!)
            if (flag == 1) {
                tv_type1.setTextColor(Color.WHITE)
                tv_type1.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.select_textview_bg)
            }
            if (flag != 1) {
                tv_type1.setTextColor(colorSurface)
                tv_type1.background = null
            }
            if (flag == 2) {
                tv_type2.setTextColor(Color.WHITE)
                tv_type2.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.select_textview_bg)
            }
            if (flag != 2) {
                tv_type2.setTextColor(colorSurface)
                tv_type2.background = null
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveData.value = viewModel.editList[position].itemWeekList.value
        result.addAll(liveData.value!!)
        showWeeks()
        initEvent()
    }

    private fun showWeeks() {
        val adapter = SelectWeekAdapter(R.layout.item_select_week, viewModel.maxWeek, result)
        rv_week.adapter = adapter
        rv_week.layoutManager = StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL)
        var prePos = -1
        adapter.bindToRecyclerView(rv_week)
        rv_week.positionChangedListener = object : SelectedRecyclerView.PositionChangedListener {
            override fun changeState(pos: Int, isDown: Boolean) {
                if (prePos != pos || isDown) {
                    if (pos in 0 until viewModel.maxWeek) {
                        if (!result.contains(pos + 1)) {
                            result.add(pos + 1)
                            adapter.getViewByPosition(pos, R.id.tv_num) //wrong
                                ?.setBackgroundResource(R.drawable.week_selected_bg)
                            (adapter.getViewByPosition(pos, R.id.tv_num) as AppCompatTextView)
                                .setTextColor(Color.WHITE)
                        } else {
                            result.remove(pos + 1)
                            adapter.getViewByPosition(pos, R.id.tv_num)?.background = null
                            (adapter.getViewByPosition(pos, R.id.tv_num) as AppCompatTextView)
                                .setTextColor(colorSurface)
                        }
                        liveData.value = result
                    }
                    if (prePos != pos) {
                        prePos = pos
                    }
                }
            }
        }
    }


    private fun initEvent() {
        tv_all.setOnClickListener {
            if (tv_all.background == null) {
                result.clear()
                for (i in 1..viewModel.maxWeek) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            } else {
                result.clear()
                showWeeks()
                liveData.value = result
            }
        }

        tv_type1.setOnClickListener {
            if (tv_type1.background == null) {
                result.clear()
                for (i in 1..viewModel.maxWeek step 2) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            }
        }

        tv_type2.setOnClickListener {
            if (tv_type2.background == null) {
                result.clear()
                for (i in 2..viewModel.maxWeek step 2) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            }
        }

        btn_cancel_week.setOnClickListener {
            dismiss()
        }

        btn_save_week.setOnClickListener {
            if (result.size == 0) {
                activity?.let { it1 -> Toasty.error(it1.applicationContext, "请至少选择一周").show() }
            } else {
                viewModel.editList[position].itemWeekList.value = result
                dismiss()
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(arg: Int) =
            SelectWeekFragment().apply {
                arguments = Bundle().apply {
                    putInt("position", arg)
                }
            }
    }
}
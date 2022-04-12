package com.example.hiclass.schedule.apply

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.R
import com.example.hiclass.schedule.AutoStartUtil
import com.example.hiclass.schedule.apply.ApplyOverLayUtil.applyOverLay
import com.example.hiclass.schedule.apply.ApplyOverLayUtil.checkPermission
import com.example.hiclass.schedule.ScheduleMain
import com.example.hiclass.schedule.ScheduleViewModel
import com.example.hiclass.setting.App
import com.example.hiclass.utils.ApplyStorageUtil
import kotlinx.android.synthetic.main.fragment_apply_powers.*


class ApplyPowersFragment : DialogFragment() {

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_apply_powers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(activity as ScheduleMain).get(ScheduleViewModel::class.java)
        viewModel.isApply.observe(this, Observer {
            if (it) {
                apply_overlay_icon.text = App.context.resources.getString(R.string.icon_load_ok)
            }
        })
//        viewModel.isApplyStorage.observe(this, Observer {
//            if (it){
//                apply_storage_icon.text = App.context.resources.getString(R.string.icon_load_ok)
//            }
//        })
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        apply_auto_icon.typeface = font
        apply_overlay_icon.typeface = font
        apply_storage_icon.typeface = font
        apply_overlay_icon.text = if (checkPermission(activity as ScheduleMain)) {
            App.context.resources.getString(R.string.icon_load_ok)
        } else {
            App.context.resources.getString(R.string.icon_next)
        }
        apply_overlay.setOnClickListener {
            if (viewModel.isApply.value != true) {
                applyOverLay(activity as ScheduleMain)
            }
        }
        apply_auto_start.setOnClickListener {
            AutoStartUtil.jumpStartInterface(activity as ScheduleMain)
        }
        apply_storage_icon.text = if (ApplyStorageUtil.checkPermission(activity as ScheduleMain)) {
            App.context.resources.getString(R.string.icon_load_ok)
        } else {
            App.context.resources.getString(R.string.icon_next)
        }
        apply_storage.setOnClickListener {
            ApplyStorageUtil.applyPower(activity as ScheduleMain)
            apply_storage_icon.text = App.context.resources.getString(R.string.icon_load_ok)
        }
        btn_ignore_apply.setOnClickListener {
            val editor = (activity as ScheduleMain).getSharedPreferences(
                "apply_settings",
                AppCompatActivity.MODE_PRIVATE
            ).edit()
            editor.putBoolean("ignore_apply", true)
            editor.apply()
            dismiss()
        }
        btn_pass_apply.setOnClickListener {
            dismiss()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            ApplyPowersFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
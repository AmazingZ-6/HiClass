package com.example.hiclass.item_edit


import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.setting.App
import com.example.hiclass.R
import com.example.hiclass.schedule.ScheduleMain
import com.example.hiclass.schedule.ScheduleViewModel
import com.example.hiclass.utils.StatusUtil
import com.example.hiclass.widget.colorpicker.ColorPickerFragment
import kotlinx.android.synthetic.main.activity_item_edit.*

class ItemEdit : AppCompatActivity(), ColorPickerFragment.ColorPickerDialogListener {

    private lateinit var viewModel: ItemEditViewModel
    private lateinit var mainViewModel: ScheduleViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_item_edit)
//        delete_progressbar.visibility = GONE
        viewModel = ViewModelProvider(this).get(ItemEditViewModel::class.java)
        mainViewModel =
            ViewModelProvider(ScheduleMain.supplyOwner()).get(ScheduleViewModel::class.java)
        viewModel.idi = intent.getLongExtra("item_id", -1)
        viewModel.weeki = intent.getIntExtra("item_week", -1)

        viewModel.getItem()

        val timeView: TextView = findViewById(R.id.item_edit_time)
        timeView.text = viewModel.itemi?.getTimeString3()

        val nameView: EditText = findViewById(R.id.item_edit_name)
        nameView.hint = "请输入名称..."
        nameView.setText(viewModel.name)

        val addressView: EditText = findViewById(R.id.item_edit_address)
        addressView.hint = "请输入地点..."
        addressView.setText(viewModel.address)

        val teacherView: EditText = findViewById(R.id.item_edit_teacher)
        teacherView.hint = "请输入备注或老师..."
        teacherView.setText(viewModel.teacher)

        item_edit_color.setTextColor(viewModel.bgColor)

        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        recycle_edit_name.typeface = font
        recycle_edit_name.text = App.context.resources.getString(R.string.icon_name)
        recycle_edit_address.typeface = font
        recycle_edit_address.text = App.context.resources.getString(R.string.icon_address)
        recycle_edit_teacher.typeface = font
        recycle_edit_teacher.text = App.context.resources.getString(R.string.icon_user)
        recycle_edit_remark.typeface = font
        recycle_edit_remark.text = App.context.resources.getString(R.string.icon_remark)
        recycle_edit_color.typeface = font
        recycle_edit_color.text = App.context.resources.getString(R.string.icon_color)
        item_edit_ok.typeface = font
        item_edit_ok.text = App.context.resources.getString(R.string.icon_save)
        item_edit_delete.typeface = font
        item_edit_delete.text = App.context.resources.getString(R.string.icon_delete_edit)
        item_edit_ok.isClickable = true
        item_edit_delete.isClickable = true

        val selectItems = arrayOf("删除该事项", "删除全部同名称事项")

        item_edit_ok.setOnClickListener {
            viewModel.nameEdit = nameView.text.toString()
            viewModel.addressEdit = addressView.text.toString()
            viewModel.teacherEdit = teacherView.text.toString()
            viewModel.saveEditInfo()
            finish()
        }

        item_edit_delete.setOnClickListener {
            var selection = 0
            AlertDialog.Builder(this).apply {
                setTitle("请选择删除范围")
                setSingleChoiceItems(
                    selectItems, 0
                ) { _, which -> selection = which }
                setCancelable(false)
                setPositiveButton("确认") { _, _ ->
                    when (selection) {
                        0 -> {
                            viewModel.deleteInfo()
                            mainViewModel.deleteFlag()
                            finish()
                        }
                        1 -> {
                            viewModel.deleteBatchInfo()
                            mainViewModel.deleteBatchFlag()
                            finish()
//                            delete_progressbar.visibility = VISIBLE
//                            Timer().schedule(object :TimerTask(){
//                                override fun run() {
//                                    finish()
//                                }
//
//                            },1000)
                        }
                    }
                }

                setNegativeButton("取消") { _, _ ->

                }
            }.show()

        }

        ll_edit_color.setOnClickListener {
//            var colorT = 0
//            when(viewModel.bgColor){
//                R.color.colorTrans1 -> colorT = -855785341
//                R.color.colorTrans2 -> colorT = -855931004
//                R.color.colorTrans3 -> colorT = -865631275
//            }
            buildColorPickerDialogBuilder(viewModel.bgColor, 0)
        }

    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle("是否放弃编辑")
            setMessage("正在编辑的事项将不会得到保存")
            setCancelable(false)
            setPositiveButton("是") { dialog, which ->
                finish()
            }

            setNegativeButton("否") { dialog, which ->

            }
        }.show()
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        viewModel.bgColor = color
        item_edit_color.setTextColor(viewModel.bgColor)
    }

    private fun buildColorPickerDialogBuilder(color: Int, id: Int) {
        ColorPickerFragment.newBuilder()
            .setShowAlphaSlider(true)
            .setColor(color)
            .setDialogId(id)
            .show(this)
    }


}
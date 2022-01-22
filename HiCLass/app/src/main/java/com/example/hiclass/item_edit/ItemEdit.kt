package com.example.hiclass.item_edit


import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.App
import com.example.hiclass.R
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_item_edit.*

class ItemEdit : AppCompatActivity() {

    private lateinit var viewModel : ItemEditViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this,true,R.color.little_white)
        setContentView(R.layout.activity_item_edit)
        viewModel = ViewModelProvider(this).get(ItemEditViewModel::class.java)

        viewModel.idi = intent.getLongExtra("item_id",-1)
        viewModel.weeki = intent.getIntExtra("item_week",-1)

        viewModel.getItem()

        val timeView: TextView = findViewById(R.id.item_edit_time)
        timeView.text = viewModel.itemi?.getTimeString1()

        val nameView: EditText = findViewById(R.id.item_edit_name)
        nameView.hint = "请输入名称..."
        nameView.setText(viewModel.name)

        val addressView: EditText = findViewById(R.id.item_edit_address)
        addressView.hint = "请输入地点..."
        addressView.setText(viewModel.address)

        val teacherView: EditText = findViewById(R.id.item_edit_teacher)
        teacherView.hint = "请输入备注或老师..."
        teacherView.setText(viewModel.teacher)

        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        recycle_edit_name.typeface = font
        recycle_edit_name.text = App.context.resources.getString(R.string.icon_name)
        recycle_edit_address.typeface = font
        recycle_edit_address.text = App.context.resources.getString(R.string.icon_address)
        recycle_edit_teacher.typeface = font
        recycle_edit_teacher.text = App.context.resources.getString(R.string.icon_user)
        recycle_edit_remark.typeface = font
        recycle_edit_remark.text = App.context.resources.getString(R.string.icon_remark)




//        val btnOk: Button = findViewById(R.id.item_ok)
//        val btnDelete: Button = findViewById(R.id.item_delete)
//
//        btnOk.setOnClickListener {
//            viewModel.nameEdit = nameView.text.toString()
//            viewModel.addressEdit = addressView.text.toString()
//            viewModel.teacherEdit = teacherView.text.toString()
//            viewModel.saveEditInfo()
//            finish()
//        }
//
//        btnDelete.setOnClickListener {
//            viewModel.deleteInfo()
//            finish()
//        }

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


}
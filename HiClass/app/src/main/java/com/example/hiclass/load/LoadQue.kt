package com.example.hiclass.load

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.App
import com.example.hiclass.AppDatabase
import com.example.hiclass.R
import com.example.hiclass.data_class.ResourceBean
import com.example.hiclass.resourceDao
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_load_que.*
import kotlin.concurrent.thread

class LoadQue : AppCompatActivity() {

    private lateinit var queItem: ResourceBean
    private lateinit var viewModel: LoadQueViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resourceDao = AppDatabase.getDatabase(this).resourceDao()
        StatusUtil.setStatusBarMode(this,true,R.color.little_white)
        setContentView(R.layout.activity_load_que)
        viewModel = ViewModelProvider(this).get(LoadQueViewModel::class.java)
        val prefs = getSharedPreferences("resource_settings", Context.MODE_PRIVATE)
        val isEnglishLoad = prefs.getBoolean("english_1000", false)
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        load_que_btn.typeface = font
        if (isEnglishLoad) {
            load_que_btn.text = App.context.resources.getString(R.string.icon_load_ok)
            load_que_btn.isClickable = false
        } else {
            load_que_btn.text = App.context.resources.getString(R.string.icon_load)
            load_que_btn.setOnClickListener {
                AlertDialog.Builder(this).apply {
                    setTitle("开始下载")
                    setMessage("请勿在下载过程中退出程序！")
                    setCancelable(false)
                    setPositiveButton("是") { dialog, which ->
                        thread {
                            viewModel.loadStateUpdate(1)
                            LoadResource.loadInfo("0001")
                            viewModel.loadStateUpdate(2)
                        }
                    }

                    setNegativeButton("否") { dialog, which ->

                    }
                }.show()
            }
        }

        viewModel.loadFlag.observe(this, Observer {
            when (it) {
                1 -> {
                    load_que_btn.text = App.context.resources.getString(R.string.icon_loading)
                }
                2 -> {
                    load_que_btn.text = App.context.resources.getString(R.string.icon_load_ok)
                    val editor = getSharedPreferences("resource_settings",Context.MODE_PRIVATE).edit()
                    editor.putBoolean("english_1000",true)
                    editor.apply()
                    load_que_btn.isClickable = false
                }
            }
        })

    }
}
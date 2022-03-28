package com.example.hiclass.item_add

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiclass.R
import com.example.hiclass.schedule.ScheduleMain
import com.example.hiclass.schedule.ScheduleViewModel
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.item_add_base.*
import java.util.*
import kotlin.collections.ArrayList


class ItemAdd : AppCompatActivity() {


    private lateinit var viewModel: ItemAddViewModel
    private lateinit var mainViewModel: ScheduleViewModel
    private val addList = ArrayList<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.item_add_base)
        add_progressbar.visibility = GONE
        viewModel = ViewModelProvider(this).get(ItemAddViewModel::class.java)
        mainViewModel =
            ViewModelProvider(ScheduleMain.supplyOwner()).get(ScheduleViewModel::class.java)
        initAdds()
        val layoutManager = LinearLayoutManager(this)
        item_add_recyclerView.layoutManager = layoutManager
        val adapter = ItemAddAdapter(addList, supportFragmentManager, this, this, this)
        item_add_recyclerView.adapter = adapter
        recycle_fab.setOnClickListener {
            addList.add(1)
            adapter.notifyItemInserted(addList.size - 1)
            item_add_recyclerView.scrollToPosition(addList.size - 1)
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar_item_add)
        setSupportActionBar(toolbar)
        viewModel.isFinished.observe(this, androidx.lifecycle.Observer {
            finish()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_add_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_sure -> {
                AlertDialog.Builder(this).apply {
                    setTitle("是否保存")
                    setMessage("点击右下方悬浮按钮可以进行更加快捷方便的批量保存噢")
                    setCancelable(false)
                    setPositiveButton("是") { _, _ ->
                        add_progressbar.visibility = VISIBLE
                        viewModel.saveAddItemList()
//                        Timer().schedule(object : TimerTask() {
//                            override fun run() {
//                                finish()
//                            }
//
//                        }, 1000)
                    }

                    setNegativeButton("否") { dialog, which ->

                    }
                }.show()
            }

        }
        return true
    }


    private fun initAdds() {
        addList.add(1)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle("是否放弃编辑")
            setMessage("正在编辑的事项不会得到保存噢")
            setCancelable(false)
            setPositiveButton("是") { dialog, which ->
                finish()
            }

            setNegativeButton("否") { dialog, which ->

            }
        }.show()
    }


}
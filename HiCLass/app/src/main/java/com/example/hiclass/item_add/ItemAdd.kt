package com.example.hiclass.item_add

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiclass.R
import com.example.hiclass.utils.StatusUtil
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.item_add_base.*
import kotlinx.android.synthetic.main.item_add_detail.*

class ItemAdd : AppCompatActivity() {


    private lateinit var viewModel: ItemAddViewModel
    private val addList = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this,true,R.color.little_white)
        setContentView(R.layout.item_add_base)
        viewModel = ViewModelProvider(this).get(ItemAddViewModel::class.java)
        initAdds()
        val layoutManager = LinearLayoutManager(this)
        item_add_recyclerView.layoutManager = layoutManager
        val adapter = ItemAddAdapter(addList, supportFragmentManager, this,this)
        item_add_recyclerView.adapter = adapter
        recycle_fab.setOnClickListener {
            addList.add(1)
            adapter.notifyItemInserted(addList.size - 1)
            item_add_recyclerView.scrollToPosition(addList.size - 1)
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar_item_add)
        setSupportActionBar(toolbar)
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
                    setPositiveButton("是") { dialog, which ->
                        viewModel.saveAddItemList()
                        finish()
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
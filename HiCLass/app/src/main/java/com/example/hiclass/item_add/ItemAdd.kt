package com.example.hiclass.item_add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiclass.R
import kotlinx.android.synthetic.main.item_add_base.*
import kotlinx.android.synthetic.main.item_add_detail.*

class ItemAdd : AppCompatActivity() {

    private lateinit var viewModel: ItemAddViewModel
    private val addList = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_add_base)
        viewModel = ViewModelProvider(this).get(ItemAddViewModel::class.java)
        initAdds()
        val layoutManager = LinearLayoutManager(this)
        item_add_recyclerView.layoutManager = layoutManager
        val adapter = ItemAddAdapter(addList, supportFragmentManager, this)
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
                viewModel.saveAddItemList()
                finish()
            }
        }
        return true
    }

    private fun initAdds() {
        addList.add(1)
    }


}
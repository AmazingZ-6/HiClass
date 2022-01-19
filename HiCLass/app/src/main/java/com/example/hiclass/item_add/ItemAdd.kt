package com.example.hiclass.item_add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiclass.R
import kotlinx.android.synthetic.main.item_add_base.*
import kotlinx.android.synthetic.main.item_add_detail.*

class ItemAdd : AppCompatActivity() {

    private val addList = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_add_base)
        initAdds()
        val layoutManager = LinearLayoutManager(this)
        item_add_recyclerView.layoutManager = layoutManager
        val adapter = ItemAddAdapter(addList)
        item_add_recyclerView.adapter = adapter
        recycle_fab.setOnClickListener {
            addList.add(1)
            adapter.notifyItemInserted(addList.size - 1)
            item_add_recyclerView.scrollToPosition(addList.size - 1)
        }
        item_add_recyclerView.isNestedScrollingEnabled = true
    }

    private fun initAdds() {
        addList.add(1)
    }
}
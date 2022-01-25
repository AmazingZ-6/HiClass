package com.example.hiclass.load

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hiclass.AppDatabase
import com.example.hiclass.R
import com.example.hiclass.data_class.ResourceBean
import com.example.hiclass.resourceDao
import kotlinx.android.synthetic.main.activity_load_que.*
import kotlin.concurrent.thread

class LoadQue : AppCompatActivity() {

    private lateinit var queItem:ResourceBean
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resourceDao = AppDatabase.getDatabase(this).resourceDao()
        var temp = ""
        setContentView(R.layout.activity_load_que)
        btn_load_que.setOnClickListener {
            LoadResource.loadInfo("0001")
        }
        btn_get_que.setOnClickListener {
            thread {
                val random: Int = (0..949).random()
                queItem = resourceDao.getRandomQue(random.toLong(),"english")
                temp = "${queItem.content}#${queItem.A}#${queItem.B}#${queItem.C}#${queItem.D}#${queItem.correct}"

            }
        }
        random_que.text = temp
        random_que.text = temp
        random_que.text = temp
    }
}
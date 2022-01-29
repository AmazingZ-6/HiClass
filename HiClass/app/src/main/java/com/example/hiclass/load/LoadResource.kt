package com.example.hiclass.load

import android.content.Context
import com.example.hiclass.AppDatabase
import com.example.hiclass.dao.ResourceDao
import com.example.hiclass.data_class.ResourceBean
import com.example.hiclass.resourceDao
import com.example.hiclass.utils.ConnectTcp
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object LoadResource {


    fun loadInfo(type: String) {

        thread {
            val info = ConnectTcp.load(type)
            if (info == null) {

            } else {
                infoDeal(info, type)
            }
        }
    }

    private fun infoDeal(info: String, type: String) {
        var t = ""
        when (type) {
            "0001" -> {
                t = "english"
            }
        }
        val spilt = info.split(",")
        for (i in spilt.indices step 6) {
            val content = spilt[i].substring(3, spilt[i].length - 1)
            val a = spilt[i + 1].substring(3, spilt[i + 1].length - 1)
            val b = spilt[i + 2].substring(3, spilt[i + 2].length - 1)
            val c = spilt[i + 3].substring(3, spilt[i + 3].length - 1)
            val d = spilt[i + 4].substring(3, spilt[i + 4].length - 1)
            val correct = spilt[i + 5][2].toString()
            val resource = ResourceBean(t, content, a, b, c, d, correct)
            val temp = resourceDao.insertQue(resource)
        }
    }

}
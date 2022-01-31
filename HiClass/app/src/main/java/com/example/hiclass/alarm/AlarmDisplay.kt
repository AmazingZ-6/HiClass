package com.example.hiclass.alarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.R
import com.example.hiclass.utils.StatusUtil

class AlarmDisplay : AppCompatActivity() {

    private lateinit var viewModel: AlarmDisplayViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_alarm_display)
        viewModel = ViewModelProvider(this).get(AlarmDisplayViewModel::class.java)
    }
}
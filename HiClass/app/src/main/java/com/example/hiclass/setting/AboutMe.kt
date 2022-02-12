package com.example.hiclass.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hiclass.R
import com.example.hiclass.utils.StatusUtil

class AboutMe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_about_me)
    }
}
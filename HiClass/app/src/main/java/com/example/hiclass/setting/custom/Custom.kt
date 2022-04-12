package com.example.hiclass.setting.custom

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hiclass.R
import com.example.hiclass.setting.App
import com.example.hiclass.setting.bg_select.BackImageSelect
import com.example.hiclass.utils.ActivityController
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_custom.*

class Custom : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_custom)
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        val layoutList = listOf<androidx.appcompat.widget.LinearLayoutCompat>(
            custom_answer,
            custom_data, custom_bg_select, custom_color_select
        )
        val nextList = listOf<androidx.appcompat.widget.AppCompatTextView>(
            custom_bg_select_icon,
            custom_color_select_icon, custom_data_icon, custom_answer_icon, custom_clock_icon
        )
        for (i in nextList) {
            i.typeface = font
            i.text = App.context.resources.getString(R.string.icon_next)
        }
        custom_answer.setOnClickListener {
            Toast.makeText(this, "该功能将在后续版本上线！", Toast.LENGTH_SHORT).show()
        }
        custom_data.setOnClickListener {
            Toast.makeText(this, "该功能将在后续版本上线！", Toast.LENGTH_SHORT).show()
        }
        custom_color_select.setOnClickListener {
            Toast.makeText(this, "该功能将在后续版本上线！", Toast.LENGTH_SHORT).show()
        }
        custom_clock.setOnClickListener {
            Toast.makeText(this, "该功能将在后续版本上线！", Toast.LENGTH_SHORT).show()
        }
        custom_bg_select.setOnClickListener {
            ActivityController.addActivity(this)
            val intent = Intent(this, BackImageSelect::class.java)
            startActivity(intent)
        }

    }
}
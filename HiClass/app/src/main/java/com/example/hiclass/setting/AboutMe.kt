package com.example.hiclass.setting

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.hiclass.R
import com.example.hiclass.utils.FastBlurUtility.fastblur
import com.example.hiclass.utils.MakeStatusBarTransparent
import kotlinx.android.synthetic.main.activity_about_me.*


class AboutMe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        MakeStatusBarTransparent.makeStatusBarTransparent(this)
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_about_me)
        val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
        val isInitBg = sharedPre.getInt("init_bg", R.color.little_white)
        val icon = sharedPre.getString("background", "")
        if (isInitBg >= 0){

        }else{
            val decode = Base64.decode(icon!!.toByteArray(), 1)
            val bmp = BitmapFactory.decodeByteArray(decode, 0, decode.size)
            val newBmp = fastblur(bmp, 100)
            about_me_main.background = BitmapDrawable(resources, newBmp)
        }
    }
}
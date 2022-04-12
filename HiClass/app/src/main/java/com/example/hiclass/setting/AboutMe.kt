package com.example.hiclass.setting

import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.hiclass.R
import com.example.hiclass.utils.FastBlurUtility.fastblur
import com.example.hiclass.utils.MakeStatusBarTransparent
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_about_me.*


class AboutMe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_about_me)
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        about_comment_icon.typeface = font
        about_comment_icon.text = App.context.resources.getString(R.string.icon_next)
        about_thank_icon.typeface = font
        about_thank_icon.text = App.context.resources.getString(R.string.icon_next)
        about_user_read_icon.typeface = font
        about_user_read_icon.text = App.context.resources.getString(R.string.icon_next)
    }
}
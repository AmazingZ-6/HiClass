package com.example.hiclass.setting.bg_select

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hiclass.R
import com.example.hiclass.data_class.ImageInfoBean
import com.example.hiclass.schedule.ScheduleMain
import com.example.hiclass.utils.ActivityController
import com.example.hiclass.utils.FastBlurUtility
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_back_image_select.*
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread

class BackImageSelect : AppCompatActivity() {

    private val imageList = ArrayList<ImageInfoBean>()
    private lateinit var viewModel: BackImageSelectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_back_image_select)
        viewModel = ViewModelProvider(this).get(BackImageSelectViewModel::class.java)
        val toolbar: Toolbar = findViewById(R.id.bg_toolbar)
        setSupportActionBar(toolbar)
        initImageInfo()
        initAdapter()
        initOption()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let {
                        val bitmap = getBitmapForUri(it)
                        if (bitmap != null) {
                            confirmOption(bitmap)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.select_bg, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_confirm -> {
                val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
                val editor = sharedPre.edit()
                if (viewModel.selectedImage.value != null) {
                    editor.putInt("init_bg", viewModel.selectedImage.value!!.imageId)
                }
                editor.apply()
                ActivityController.finishActivity()
                val intent = Intent(this, ScheduleMain::class.java)
                startActivity(intent)
                finish()
            }
        }
        return true
    }

    override fun onBackPressed() {
        ActivityController.clearActivity()
        finish()
    }


    private fun initImageInfo() {
        imageList.add(ImageInfoBean(R.drawable.bg_1, "玉雪飞龙"))
        imageList.add(ImageInfoBean(R.drawable.bg_2, "云海之上"))
        imageList.add(ImageInfoBean(R.drawable.bg_3, "镜中世界"))
        imageList.add(ImageInfoBean(R.drawable.bg_4, "雪压青松"))
        imageList.add(ImageInfoBean(R.drawable.bg_5, "慵懒假日"))
        imageList.add(ImageInfoBean(R.drawable.bg_6, "深邃林海"))

    }

    private fun initAdapter() {
        val layoutManager = GridLayoutManager(this, 2)
        rv_bg_select.layoutManager = layoutManager
        val adapter = BackImageSelectAdapter(imageList, this, this)
        rv_bg_select.adapter = adapter
    }

    private fun initOption() {
        select_from_user.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        select_default.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("是否恢复默认主题")
                setMessage("确认后将返回主界面")
                setCancelable(false)
                setPositiveButton("确认") { dialog, which ->
                    val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
                    val editor = sharedPre.edit()
                    editor.putInt("init_bg", R.color.little_white)
                    editor.apply()
                    ActivityController.finishActivity()
                    val intent = Intent(context, ScheduleMain::class.java)
                    startActivity(intent)
                    finish()
                }

                setNegativeButton("取消") { dialog, which ->

                }
            }.show()
        }
    }

    private fun getBitmapForUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    private fun saveBackImage(bit: Bitmap) {
        val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
        val editor = sharedPre.edit()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bit.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val backImg: String =
            (Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT))
        editor.putString("background", backImg)
        editor.putInt("init_bg", -1)
        editor.apply()
        saveBlurBackImage(bit)
    }

    private fun saveBlurBackImage(bmp: Bitmap){
        thread {
            val newBmp = FastBlurUtility.fastblur(bmp, 120)
            val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
            val editor = sharedPre.edit()
            val byteArrayOutputStream = ByteArrayOutputStream()
            newBmp.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val backImg: String =
                (Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT))
            editor.putString("background_blur", backImg)
            editor.apply()
        }
    }

    private fun confirmOption(bit: Bitmap) {
        AlertDialog.Builder(this).apply {
            setTitle("设置成功")
            setMessage("即将返回主界面")
            setCancelable(false)
            setPositiveButton("确认") { dialog, which ->
                saveBackImage(bit)
                ActivityController.finishActivity()
                val intent = Intent(context, ScheduleMain::class.java)
                startActivity(intent)
                finish()
            }

        }.show()
    }
}
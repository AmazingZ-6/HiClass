package com.example.hiclass.setting

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.hiclass.R
import com.example.hiclass.data_class.ImageInfoBean
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_back_image_select.*
import java.io.ByteArrayOutputStream

class BackImageSelect : AppCompatActivity() {

    private val imageList = ArrayList<ImageInfoBean>()
    private lateinit var viewModel: BackImageSelectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_back_image_select)
        viewModel = ViewModelProvider(this).get(BackImageSelectViewModel::class.java)
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
            }
        }
        return true
    }


    private fun initImageInfo() {
        repeat(2) {
            imageList.add(ImageInfoBean(R.drawable.bg_1, "玉雪飞龙"))
            imageList.add(ImageInfoBean(R.drawable.bg_1, "阿斯蒂芬"))
            imageList.add(ImageInfoBean(R.drawable.bg_1, "爱神的箭"))
            imageList.add(ImageInfoBean(R.drawable.bg_1, "陕西境内"))
            imageList.add(ImageInfoBean(R.drawable.bg_1, "去问人体"))
            imageList.add(ImageInfoBean(R.drawable.bg_1, "宣布擦拭"))
        }

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
    }

    private fun confirmOption(bit: Bitmap) {
        AlertDialog.Builder(this).apply {
            setTitle("设置成功")
            setMessage("是否返回主界面")
            setCancelable(false)
            setPositiveButton("是") { dialog, which ->
                saveBackImage(bit)
                finish()
            }

            setNegativeButton("否") { dialog, which ->

            }
        }.show()
    }
}
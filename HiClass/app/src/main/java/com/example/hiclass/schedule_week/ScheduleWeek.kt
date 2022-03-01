package com.example.hiclass.schedule_week

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.GetTcpInfo
import com.example.hiclass.R
import com.example.hiclass.itemDao
import com.example.hiclass.load.LoadQue
import com.example.hiclass.schedule.apply.ApplyPowersFragment
import com.example.hiclass.setting.AboutMe
import com.example.hiclass.setting.bg_select.BackImageSelect
import com.example.hiclass.setting.SettingsActivity
import com.example.hiclass.utils.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_schedule_week.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.thread

class ScheduleWeek : AppCompatActivity() {

    private lateinit var headerImage: ImageView
    private lateinit var viewModel: ScheduleWeekViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatus()
        setContentView(R.layout.activity_schedule_week)
        getBackImage()
        getHeaderImage()
//        applyPowers()
        viewModel = ViewModelProvider(this).get(ScheduleWeekViewModel::class.java)
        val toolbar: Toolbar = findViewById(R.id.toolbar_main_week)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_fragment_week)
        val navView: NavigationView = findViewById(R.id.nav_view_week)
        val headerView = navView.inflateHeaderView(R.layout.nav_header_main)
        toolbar.title = CalendarUtil.getTodayDate()
        val dateT = CalendarUtil.getWeekDate()
        val t0 = "${dateT[0][1]}日"
        val t1 = "${dateT[0][2]}日"
        val t2 = "${dateT[0][3]}日"
        val t3 = "${dateT[0][4]}日"
        val t4 = "${dateT[0][5]}日"
        val t5 = "${dateT[0][6]}日"
        val t6 = "${dateT[0][0]}日"
        val tm = "${dateT[1][0]}"
        date_1_week.text = t0
        date_2_week.text = t1
        date_3_week.text = t2
        date_4_week.text = t3
        date_5_week.text = t4
        date_6_week.text = t5
        date_7_week.text = t6
        date_month_week.text = tm
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_baseline_subject_24)
        }
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_login -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("是否切换登录")
                        setMessage("所有数据将被清空！")
                        setCancelable(false)
                        setPositiveButton("是") { dialog, which ->
                            thread {
                                itemDao.clearAllItems()
                            }
                            val intent = Intent(      //注意在这里杀掉服务进程
                                context,
                                GetTcpInfo::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("isReLogin", "true")
                            startActivity(intent)
                            android.os.Process.killProcess(android.os.Process.myPid())
                        }

                        setNegativeButton("否") { dialog, which ->

                        }
                    }.show()
                    true
                }
                R.id.nav_select_bg -> {
                    ActivityController.addActivity(this)
                    val intent = Intent(this, BackImageSelect::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_setting -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_about_me -> {
                    val intent = Intent(this, AboutMe::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_load -> {
                    val intent = Intent(this, LoadQue::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
        val imageHeader = headerView.findViewById<ImageView>(R.id.image_header)
        headerImage = imageHeader
        val usernameHeader = headerView.findViewById<TextView>(R.id.text_header)
        imageHeader.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        createWeekView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let {
                        val bitmap = getBitmapForUri(it)
                        headerImage.setImageBitmap(bitmap)
                        if (bitmap != null) {
                            saveHeaderImage(bitmap)
                        }
                    }
                }
            }

            2 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && Settings.canDrawOverlays(this)
                ) {
                    val editor = getSharedPreferences("apply_settings", MODE_PRIVATE).edit()
                    editor.putBoolean("apply_power", true)
                    editor.apply()
                    viewModel.applyHasFinished()
                } else {
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_LONG).show()
                }
            }


        }
    }

    private fun getBitmapForUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    private fun saveHeaderImage(bit: Bitmap) {
        val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
        val editor = sharedPre.edit()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bit.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val headImg: String =
            (Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT))
        editor.putString("header_icon", headImg)
        editor.apply()
    }

    private fun getHeaderImage() {
        var bitmap: Bitmap? = null
        val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
        val icon = sharedPre.getString("header_icon", "")
        if (icon !== "") {
            val decode = Base64.decode(icon!!.toByteArray(), 1)
            bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
            headerImage.setImageBitmap(bitmap)
        }
    }

    private fun initStatus() {
        val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
        val isInitBg = sharedPre.getInt("init_bg", R.color.little_white)
        if (isInitBg == R.color.little_white) {
            StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        } else {
            MakeStatusBarTransparent.makeStatusBarTransparent(this)
        }
    }

    private fun getBackImage() {
        var bitmap: Bitmap? = null
        val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
        val icon = sharedPre.getString("background", "")
        val isInitBg = sharedPre.getInt("init_bg", R.color.little_white)
        if (isInitBg >= 0) {
            val bg = findViewById<AppBarLayout>(R.id.main_view)
            bg.setBackgroundResource(isInitBg)
        } else {
            if (icon !== "") {
                val decode = Base64.decode(icon!!.toByteArray(), 1)
                bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
                val bg = findViewById<AppBarLayout>(R.id.main_view)
                val newBitmap = BitMapScale.imageCompress(
                    bitmap, ViewUtil.getScreenHeight(this),
                    ViewUtil.getScreenWidth(this)
                )
                bg.background = BitmapDrawable(resources, newBitmap)
            }
        }
    }

    private fun applyPowers() {
        val sp = getSharedPreferences("apply_settings", MODE_PRIVATE)
        val isApply = Settings.canDrawOverlays(this)
        val isIgnored = sp.getBoolean("ignore_apply", false)
        if (!isApply && !isIgnored) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    val applyPowersFragment = ApplyPowersFragment.newInstance()
                    applyPowersFragment.isCancelable = false
                    applyPowersFragment.show(supportFragmentManager, "apply_power")
                }
            }, 1000)
        }
    }

    private fun createWeekView(){

    }
}
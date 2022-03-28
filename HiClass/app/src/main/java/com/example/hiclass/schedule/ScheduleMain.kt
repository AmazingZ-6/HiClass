package com.example.hiclass.schedule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager.widget.ViewPager
import com.example.hiclass.*
import com.example.hiclass.alarm.AlarmDisplay
import com.example.hiclass.item_add.ItemAdd
import com.example.hiclass.load.LoadQue
import com.example.hiclass.setting.ring_select.RingSelect
import com.example.hiclass.schedule.apply.ApplyPowersFragment
import com.example.hiclass.setting.AboutMe
import com.example.hiclass.setting.bg_select.BackImageSelect
import com.example.hiclass.setting.SettingsActivity
import com.example.hiclass.utils.ActivityController.addActivity
import com.example.hiclass.utils.BitMapScale
import com.example.hiclass.utils.CalendarUtil
import com.example.hiclass.utils.CalendarUtil.getBoldDay
import com.example.hiclass.utils.MakeStatusBarTransparent
import com.example.hiclass.utils.StatusUtil
import com.example.hiclass.utils.ViewUtil.getScreenHeight
import com.example.hiclass.utils.ViewUtil.getScreenWidth
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.view_pager.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.thread


private lateinit var mainOwner: ViewModelStoreOwner

class ScheduleMain : AppCompatActivity() {

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var headerImage: ImageView
    private val bgMap = mutableMapOf<Int, Int>()
    private lateinit var dateView: List<TextView>
    private lateinit var weekdayView: List<TextView>


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        mainOwner = this
        super.onCreate(savedInstanceState)
        initStatus()
//        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
//        MakeStatusBarTransparent.makeStatusBarTransparent(this)
        setContentView(R.layout.view_pager)
        getBackImage()
        dateView =
            listOf<TextView>(date_7, date_1, date_2, date_3, date_4, date_5, date_6)
        weekdayView = listOf<TextView>(
            weekday_7, weekday_1, weekday_2, weekday_3,
            weekday_4, weekday_5, weekday_6
        )
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_fragment)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navView.inflateHeaderView(R.layout.nav_header_main)
        viewPager.adapter = sectionsPagerAdapter
        val boldNow = getBoldDay()
        val weekNow = if (boldNow[0] != -1) boldNow[0] + 1 else 1
        val dayNow = boldNow[1]
        viewPager.currentItem = weekNow - 1
        val titleTemp = "第${weekNow}周"
        toolbar.title = titleTemp
//        val titleTemp2 = CalendarUtil.getTodayDate()
//        toolbar.subtitle = titleTemp2
        val t0 = CalendarUtil.getDate(weekNow, 1).split(".")[0]
        val t1 = CalendarUtil.getDate(weekNow, 1).split(".")[1] + "日"
        val t2 = CalendarUtil.getDate(weekNow, 2).split(".")[1] + "日"
        val t3 = CalendarUtil.getDate(weekNow, 3).split(".")[1] + "日"
        val t4 = CalendarUtil.getDate(weekNow, 4).split(".")[1] + "日"
        val t5 = CalendarUtil.getDate(weekNow, 5).split(".")[1] + "日"
        val t6 = CalendarUtil.getDate(weekNow, 6).split(".")[1] + "日"
        val t7 = CalendarUtil.getDate(weekNow, 7).split(".")[1] + "日"
        date_month.text = t0
        date_1.text = t1
        date_2.text = t2
        date_3.text = t3
        date_4.text = t4
        date_5.text = t5
        date_6.text = t6
        date_7.text = t7
        if (dayNow != -1) {
            dateView[dayNow].setTextColor(getColor(R.color.black))
            weekdayView[dayNow].setTextColor(getColor(R.color.black))
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.updatePosition(position + 1)
                viewModel.updateDate(position + 1)
                viewModel.updateBold(position + 1)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        viewModel.position.observe(this, Observer {
            toolbar.title = "第${viewModel.position.value}周"
        })

        viewModel.dateShowIndex.observe(this, Observer {
            val t01 = CalendarUtil.getDate(it, 1).split(".")[0]
            val t11 = CalendarUtil.getDate(it, 1).split(".")[1] + "日"
            val t21 = CalendarUtil.getDate(it, 2).split(".")[1] + "日"
            val t31 = CalendarUtil.getDate(it, 3).split(".")[1] + "日"
            val t41 = CalendarUtil.getDate(it, 4).split(".")[1] + "日"
            val t51 = CalendarUtil.getDate(it, 5).split(".")[1] + "日"
            val t61 = CalendarUtil.getDate(it, 6).split(".")[1] + "日"
            val t71 = CalendarUtil.getDate(it, 7).split(".")[1] + "日"
            date_month.text = t01
            date_1.text = t11
            date_2.text = t21
            date_3.text = t31
            date_4.text = t41
            date_5.text = t51
            date_6.text = t61
            date_7.text = t71
        })

        viewModel.updateBold.observe(this, Observer {
            if (viewPager.currentItem == weekNow - 1) {
                dateView[dayNow].setTextColor(getColor(R.color.black))
                weekdayView[dayNow].setTextColor(getColor(R.color.black))
            } else {
                dateView[dayNow].setTextColor(getColor(R.color.no_bold))
                weekdayView[dayNow].setTextColor(getColor(R.color.no_bold))
            }
        })

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
                    addActivity(this)
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
                R.id.nav_select_ring -> {
                    val intent = Intent(this, RingSelect::class.java)
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
        getHeaderImage()
        applyPowers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                val intent = Intent(this, ItemAdd::class.java)
                startActivity(intent)
            }
            R.id.menu_clock -> {
                val intent = Intent(this, AlarmDisplay::class.java)
                startActivity(intent)
            }
//            R.id.menu_mode ->{
//                modeChange()
//            }
        }
        return true
    }

    override fun onBackPressed() {
//        android.os.Process.killProcess(android.os.Process.myPid())
//        viewModel.exit()
        finish()
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
                    bitmap, getScreenHeight(this),
                    getScreenWidth(this)
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

    private fun initStatus() {
        val sharedPre = getSharedPreferences("user_data", MODE_PRIVATE)
        val isInitBg = sharedPre.getInt("init_bg", R.color.little_white)
        if (isInitBg == R.color.little_white) {
            StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        } else {
            MakeStatusBarTransparent.makeStatusBarTransparent(this)
        }
        bgMap[0] = R.color.little_white
        bgMap[1] = R.drawable.bg_1_by
    }

    private fun modeChange() {

    }


    companion object {
        fun supplyOwner(): ViewModelStoreOwner {
            return mainOwner
        }
    }

}
package com.example.hiclass.schedule

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager.widget.ViewPager
import com.example.hiclass.*
import com.example.hiclass.dao.ItemDao
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.item_add.ItemAdd
import com.example.hiclass.utils.ChangeItem.AddItemList
import com.example.hiclass.utils.StatusUtil
import com.example.hiclass.utils.TypeSwitcher
import com.google.android.material.navigation.NavigationView
import java.io.File
import kotlin.concurrent.thread



private lateinit var mainOwner: ViewModelStoreOwner

class ScheduleMain : AppCompatActivity() {

    private lateinit var viewModel: ScheduleViewModel

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        mainOwner = this
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this,true,R.color.little_white)
        setContentView(R.layout.view_pager)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_fragment)
        val navView: NavigationView = findViewById(R.id.nav_view)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.currentItem = 15
        val titleTemp = "第16周"
        toolbar.title = titleTemp
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.updatePosition(position + 1)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        viewModel.position.observe(this, Observer {
            toolbar.title = "第${viewModel.position.value}周"
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
//                        R.id.nav_timer -> {
//                            val intent = Intent(activity, AlarmEdit::class.java)
//                            startActivity(intent)
//                            true
//                        }
//                        R.id.nav_about_me -> {
//                            val intent = Intent(activity, AboutMe::class.java)
//                            startActivity(intent)
//                            true
//                        }

                else -> false
            }
        }
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
        }
        return true
    }

    override fun onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    companion object {
        fun supplyOwner() : ViewModelStoreOwner{
            return mainOwner
        }
    }

}
package com.example.hiclass.setting.ring_select

import android.content.Intent
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.R
import com.example.hiclass.data_class.ClickBean2
import com.example.hiclass.schedule.ScheduleMain
import com.example.hiclass.setting.App
import com.example.hiclass.utils.ActivityController
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_ring_select.*

class RingSelect : AppCompatActivity() {
    private lateinit var viewModel: RingSelectViewModel

    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_ring_select)
        val toolbar = findViewById<Toolbar>(R.id.ring_toolbar)
        setSupportActionBar(toolbar)
        select_ring_from_user.setOnClickListener {
            val intent = Intent(this, RingFromUser::class.java)
            startActivity(intent)
        }
        viewModel = ViewModelProvider(this).get(RingSelectViewModel::class.java)
        val clickList = listOf<androidx.appcompat.widget.AppCompatTextView>(
            start_ring_1, start_ring_2,
            start_ring_3, start_ring_4, start_ring_5, start_ring_6
        )
        val selectList = listOf<androidx.appcompat.widget.AppCompatTextView>(
            select_ring_1, select_ring_2, select_ring_3, select_ring_4, select_ring_5, select_ring_6
        )
        val clickBeanList = listOf(
            ClickBean2(0, false),
            ClickBean2(1, false),
            ClickBean2(2, false),
            ClickBean2(3, false),
            ClickBean2(4, false),
            ClickBean2(5, false)
        )
        val lineList = listOf<androidx.appcompat.widget.LinearLayoutCompat>(
            select_line_1, select_line_2, select_line_3, select_line_4, select_line_5, select_line_6
        )
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")

        for (i in clickList) {
            i.typeface = font
            i.text = App.context.resources.getString(R.string.icon_play)
        }

        for (i in selectList) {
            i.typeface = font
            i.text = App.context.resources.getString(R.string.icon_load_ok)
        }

        for (i in clickList.indices) {
            clickList[i].setOnClickListener {
                viewModel.updateClick(clickBeanList[i])
            }
        }

        for (i in lineList.indices) {
            lineList[i].setOnClickListener {
                viewModel.updateSelect(i)
            }
        }

        viewModel.selectedPos.observe(this, Observer {
            for (i in lineList.indices) {
                if (i == it) {
                    selectList[i].visibility = VISIBLE
                } else {
                    selectList[i].visibility = INVISIBLE
                }
            }
        })

        viewModel.clickedPosition.observe(this, Observer {
            for (i in clickList.indices) {
                if (i != it.pos) {
                    clickList[i].text = App.context.resources.getString(R.string.icon_play)
                    clickBeanList[i].state = false
                    selectList[i].visibility = INVISIBLE
                } else {
                    selectList[i].visibility = VISIBLE
                    if (it.state) {
                        clickList[i].text =
                            App.context.resources.getString(R.string.icon_playing)
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.stop()
                            mediaPlayer.reset()
                            initMediaPlayer(it.pos)
                            mediaPlayer.start()
                            mediaPlayer.isLooping = true
                        } else {
                            initMediaPlayer(it.pos)
                            mediaPlayer.start()
                            mediaPlayer.isLooping = true
                        }
                    } else {
                        clickList[i].text = App.context.resources.getString(R.string.icon_play)
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        mediaPlayer.stop()
        mediaPlayer.release()
        finish()
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
                if (viewModel.selectedPos.value != null) {
                    editor.putInt("ring", viewModel.selectedPos.value!!)
                    editor.putBoolean("is_local_ring", false)
                }
                editor.apply()
                mediaPlayer.stop()
                mediaPlayer.release()
                finish()
            }
        }
        return true
    }

    private fun initMediaPlayer(pos: Int) {
        val assetManager = assets
        val musicList = listOf<String>(
            "default.wav", "robot.wav",
            "dushen.wav", "chongfenghao.wav",
            "jianqiang.wav", "youyang.wav"
        )
        val fd = assetManager.openFd(musicList[pos])
        mediaPlayer.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mediaPlayer.prepare()
        mediaPlayer.setVolume(0.7f, 0.7f)
    }


}
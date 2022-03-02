package com.example.hiclass.setting.ring_select

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hiclass.R
import com.example.hiclass.data_class.ClickBean2
import com.example.hiclass.data_class.MusicBean
import com.example.hiclass.utils.GetUserMusic
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_ring_from_user.*

class RingFromUser : AppCompatActivity() {

    private lateinit var musicList: List<MusicBean>
    private var mediaPlayer = MediaPlayer()
    private lateinit var viewModel: RFUSViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_ring_from_user)
        viewModel = ViewModelProvider(this).get(RFUSViewModel::class.java)
        val toolbar: Toolbar = findViewById(R.id.ring_from_user_toolbar)
        setSupportActionBar(toolbar)
        initObserve()
        getUserInfo()
        initAdapter()
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
                if (viewModel.clickedPos.value != null) {
                    editor.putString(
                        "local_ring",
                        musicList[viewModel.clickedPos.value!!.pos].fileUrl
                    )
                    editor.putBoolean("is_local_ring", true)
                }
                editor.apply()
                mediaPlayer.stop()
                mediaPlayer.release()
                finish()
            }
        }
        return true
    }

    override fun onBackPressed() {
        mediaPlayer.stop()
        mediaPlayer.release()
        finish()
    }

    private fun initObserve() {
        viewModel.clickedPos.observe(this, Observer {
            if (it.state) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                initMediaPlayer(it.pos)
                mediaPlayer.start()
            } else {
                mediaPlayer.stop()
                mediaPlayer.reset()
            }
        })
    }

    private fun getUserInfo() {
        musicList = GetUserMusic.getAllSongs(this)
    }

    private fun initAdapter() {
        val cList = mutableListOf<ClickBean2>()
        for (i in musicList.indices) {
            val c = ClickBean2(i, false)
            cList.add(c)
        }
        val layoutManager = LinearLayoutManager(this)
        ring_from_user_ry.layoutManager = layoutManager
        val adapter = RFUSAdapter(musicList, this, this, cList)
        ring_from_user_ry.adapter = adapter
    }

    private fun initMediaPlayer(pos: Int) {
        val path = musicList[pos].fileUrl
        mediaPlayer.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepare()
        mediaPlayer.setVolume(0.7f, 0.7f)
    }
}
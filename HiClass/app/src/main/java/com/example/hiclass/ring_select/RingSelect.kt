package com.example.hiclass.ring_select

import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.R
import com.example.hiclass.setting.App
import com.example.hiclass.utils.StatusUtil
import kotlinx.android.synthetic.main.activity_ring_select.*

class RingSelect : AppCompatActivity() {
    private lateinit var viewModel: RingSelectViewModel

    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_ring_select)
        viewModel = ViewModelProvider(this).get(RingSelectViewModel::class.java)
        initMediaPlayer()
        val clickList = listOf<androidx.appcompat.widget.AppCompatTextView>(
            start_ring_1, start_ring_2,
            start_ring_3, start_ring_4, start_ring_5, start_ring_6
        )
        val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
        for (i in clickList) {
            i.typeface = font
            i.text = App.context.resources.getString(R.string.icon_play)
        }
        for (i in clickList.indices) {
            clickList[i].setOnClickListener {
                viewModel.updateClick(i)
            }
        }
        viewModel.clickedPosition.observe(this, Observer {
            for (i in clickList.indices) {
                if (i != it) {
                    clickList[i].text = App.context.resources.getString(R.string.icon_play)
                } else {
                    if (mediaPlayer.isPlaying) {
                        clickList[i].text = App.context.resources.getString(R.string.icon_play)
                        mediaPlayer.stop()
                    } else {
                        clickList[i].text = App.context.resources.getString(R.string.icon_playing)
//                        mediaPlayer.stop()
//                        mediaPlayer.release()
                        updateMediaPlayer(it)
                        mediaPlayer.start()
                        mediaPlayer.isLooping = true
                    }
                }
            }
        })
    }

    private fun initMediaPlayer() {
        val assetManager = assets
        val fd = assetManager.openFd("default.wav")
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

    private fun updateMediaPlayer(pos: Int){
        val assetManager = assets
        val musicList = listOf<String>(
            "default.wav", "robot.wav",
            "dushen.wav", "chongfenghao.wav",
            "jianqiang.wav", "youyang.wav"
        )
        val fd = assetManager.openFd(musicList[pos])
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
    }
}
package com.example.hiclass.setting.ring_select

import android.graphics.Typeface
import com.example.hiclass.setting.bg_select.BackImageSelectViewModel


import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.hiclass.R
import com.example.hiclass.data_class.ClickBean2
import com.example.hiclass.data_class.MusicBean
import com.example.hiclass.setting.App

class RFUSAdapter(
    private val musicInfo: List<MusicBean>,
    private val owner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner,
    private val clickList: List<ClickBean2>
) :
    RecyclerView.Adapter<RFUSAdapter.ViewHolder>() {

    private lateinit var viewModel: RFUSViewModel
    private val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val line: androidx.appcompat.widget.LinearLayoutCompat =
            view.findViewById(R.id.select_fu_line)
        val musicName: androidx.appcompat.widget.AppCompatTextView =
            view.findViewById(R.id.ring_fu_name)
        val musicSelect: androidx.appcompat.widget.AppCompatTextView =
            view.findViewById(R.id.select_ring_fu)
        val musicPlayer: AppCompatTextView = view.findViewById(R.id.start_ring_fu)
    }

    override fun getItemCount(): Int = musicInfo.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ring_from_user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        viewModel = ViewModelProvider(owner).get(RFUSViewModel::class.java)
        val music = musicInfo[position]
        holder.musicName.text = music.title
        holder.musicSelect.typeface = font
        holder.musicPlayer.typeface = font
        holder.musicPlayer.text = App.context.resources.getString(R.string.icon_play)
        holder.musicSelect.text = App.context.resources.getString(R.string.icon_load_ok)
        holder.line.setOnClickListener {
            viewModel.updateClick(clickList[position])
        }
        viewModel.clickedPos.observe(lifecycleOwner, Observer {
            if (it.pos != position) {
                holder.musicSelect.visibility = INVISIBLE
                holder.musicPlayer.text = App.context.resources.getString(R.string.icon_play)
                clickList[position].state = false
            } else {
                holder.musicSelect.visibility = VISIBLE
                if (it.state){
                    holder.musicPlayer.text = App.context.resources.getString(R.string.icon_playing)
                }else{
                    holder.musicPlayer.text = App.context.resources.getString(R.string.icon_play)
                }
            }
        })
    }
}
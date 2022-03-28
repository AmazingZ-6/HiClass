package com.example.hiclass.setting.bg_select

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.hiclass.R
import com.example.hiclass.data_class.ImageInfoBean
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BackImageSelectAdapter(
    private val imageInfo: List<ImageInfoBean>,
    private val owner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<BackImageSelectAdapter.ViewHolder>() {

    private lateinit var viewModel: BackImageSelectViewModel

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bgImage: AppCompatImageView = view.findViewById(R.id.image_view)
        val bgName: AppCompatTextView = view.findViewById(R.id.image_name)
        val isChecked: FloatingActionButton = view.findViewById(R.id.fab_selected_bg)
    }

    override fun getItemCount(): Int = imageInfo.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.back_image_select_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        viewModel = ViewModelProvider(owner).get(BackImageSelectViewModel::class.java)
        val image = imageInfo[position]
        holder.bgImage.setImageResource(image.imageId)
//        holder.bgName.text = image.name
        viewModel.selectedImage.observe(lifecycleOwner, Observer {
            if (it.imageId != image.imageId) {
                holder.isChecked.visibility = GONE
            } else {
                holder.isChecked.visibility = VISIBLE
            }
        })
        holder.bgImage.setOnClickListener {
            viewModel.selectedUpdate(image)
        }
    }
}
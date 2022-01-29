package com.example.hiclass.item_add

import android.graphics.Color
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.hiclass.R
import com.example.hiclass.widget.SquareTextView
import splitties.resources.styledColor

class SelectWeekAdapter(layoutResId: Int, max: Int, private val intData: List<Int>) :
    BaseQuickAdapter<Int, BaseViewHolder>(layoutResId, (1..max).toMutableList()) {

    override fun convert(helper: BaseViewHolder, item: Int?) {
        if (item == null) return
        helper.setText(R.id.tv_num, "$item")
        if (intData.contains(item)) {
            helper.setTextColor(R.id.tv_num, Color.WHITE)
            helper.setBackgroundRes(R.id.tv_num, R.drawable.week_selected_bg)
        } else {
            val v = helper.getView<SquareTextView>(R.id.tv_num)
            v.setTextColor(v.styledColor(R.attr.colorOnSurface))
            v.background = null
        }
    }

}
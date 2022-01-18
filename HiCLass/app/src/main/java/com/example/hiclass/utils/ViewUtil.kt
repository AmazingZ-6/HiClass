package com.example.hiclass.utils

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.example.hiclass.R

object ViewUtil {
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getDayView(v: View, day: Int): RelativeLayout {
        var dayId = 0
        when (day) {
            1 -> dayId = R.id.monday
            2 -> dayId = R.id.tuesday
            3 -> dayId = R.id.wednesday
            4 -> dayId = R.id.thursday
            5 -> dayId = R.id.friday
            6 -> dayId = R.id.saturday
            7 -> dayId = R.id.weekday
        }
        return v.findViewById(dayId)
    }
}
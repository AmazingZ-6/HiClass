package com.example.hiclass.utils

import android.app.Activity

object ActivityController {
    private val activities = ArrayList<Activity>()

    fun addActivity(activity: Activity) {
        var flag = false
        for (i in activities) {
            if (i == activity) {
                flag = true
                break
            }
        }
        if (!flag) activities.add(activity)
    }


    fun finishActivity() {
        for (a in activities) {
            a.finish()
        }
        activities.clear()
    }

    fun clearActivity() {
        activities.clear()
    }
}
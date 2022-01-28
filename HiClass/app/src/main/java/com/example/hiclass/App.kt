package com.example.hiclass

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import java.util.logging.Handler

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}
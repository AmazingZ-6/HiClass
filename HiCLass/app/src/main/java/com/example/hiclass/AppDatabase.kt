package com.example.hiclass

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hiclass.dao.ItemDao
import com.example.hiclass.dao.ResourceDao
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.data_class.ResourceBean


@Database(version = 1, entities = [ItemDataBean::class, ResourceBean::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun resourceDao(): ResourceDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "app_database"
            ).build().apply {
                instance = this
            }
        }
    }
}
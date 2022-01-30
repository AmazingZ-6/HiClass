package com.example.hiclass.data_class

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class ResourceBean(
    var type: String,
    var content: String,
    var A: String,
    var B: String,
    var C: String,
    var D: String,
    var correct: String
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

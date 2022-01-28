package com.example.hiclass.data_class

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ResourceBean(
    var type:String,
    var content: String,
    var A: String,
    var B: String,
    var C: String,
    var D: String,
    var correct: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

//    fun getCorrect(){
//
//    }
}

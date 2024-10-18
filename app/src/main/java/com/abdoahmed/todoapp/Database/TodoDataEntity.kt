package com.abdoahmed.todoapp.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TodoData")
data class TodoDataEntity(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    var Title:String,
    var Content:String,
    var username:String,
    var startTime:String,
    var EndTime:String,
    var CompleteState:Int,
    var isActivated:Int

)

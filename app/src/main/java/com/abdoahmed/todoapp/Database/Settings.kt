package com.abdoahmed.todoapp.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")

data class Settings(
    @PrimaryKey
    val username:String,

    val deleteState:Boolean,
    val updateState:Boolean

)

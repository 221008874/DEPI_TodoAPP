package com.abdoahmed.todoapp.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserData")
data class UserAuthanticationDataEntity(
    @PrimaryKey
     var username :String,

     var password:String

)

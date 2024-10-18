package com.abdoahmed.todoapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserAuthanticationDataEntity::class,TodoDataEntity::class,Settings::class], version = 1)

abstract class __DatabaseBuilder__:RoomDatabase()  {


    abstract fun userDatabaseAccessObject():DatabaseAccessOpject

    companion object{


        private var __instance__:__DatabaseBuilder__?=null


        fun getDatabaseInstance(context: Context):__DatabaseBuilder__{
            return __instance__?: synchronized(this){
                val instance=Room.databaseBuilder(context.applicationContext,
                    __DatabaseBuilder__::class.java,
                    "TodoAppDp").build()
                __instance__=instance
                instance
            }
        }






    }




}
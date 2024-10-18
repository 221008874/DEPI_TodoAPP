package com.abdoahmed.todoapp.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DatabaseAccessOpject {

    @Insert(onConflict=OnConflictStrategy.IGNORE)
    suspend fun __insertNewUserData__(userAuthanticationDataEntity: UserAuthanticationDataEntity):Long

    @Query("select * from userdata where username=:username")
    suspend fun __RetrieveSpecificUserData__(username:String):UserAuthanticationDataEntity?



    @Insert
    suspend fun __insertNewTodo__(todoDataEntity: TodoDataEntity)


    @Query("select * from tododata where username=:username")
    suspend fun __retrieveAllTodoS__(username: String):List<TodoDataEntity>

    @Query("DELETE FROM tododata WHERE id=:id")
    suspend fun __DeleteTodo(id:Int)


    @Query("select * from tododata WHERE id=:id")
    suspend fun __SelectTodoOnId(id:Int?):TodoDataEntity

    @Query("UPDATE  tododata  SET EndTime=:endTime, startTime=:startTime, Content =:cont,Title =:Title   WHERE id=:id")
    suspend fun __UpdateTodo(cont:String,Title:String,id: Int,startTime:String,endTime:String)


    @Insert()
    suspend fun __insertDafaultSettings__(settings: Settings)

    @Query("select * from settings WHERE username=:username")
    suspend fun __retrieveSettings(username: String):Settings

    @Query("UPDATE  settings  SET deleteState=:deleteState  WHERE username=:username")
    suspend fun __UpdateDeleteState(deleteState:Boolean,username: String)


    @Query("UPDATE  settings  SET updateState=:updateState  WHERE username=:username")
    suspend fun __changeUpdateState(updateState:Boolean,username: String)


    @Query("UPDATE  tododata  SET CompleteState =:state WHERE id=:id")
    suspend fun __UpdateTodoCompleteState(id: Int ,state:Int)



    @Query("UPDATE TodoData SET isActivated = :isActive WHERE id = :todoId")
    suspend fun updateAlarmStatus(todoId: Int, isActive: Int)
}
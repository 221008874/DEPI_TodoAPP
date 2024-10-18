package com.abdoahmed.todoapp.Database.Dao_Implementation

import android.content.Context
import android.util.Log
import com.abdoahmed.todoapp.Database.DatabaseAccessOpject
import com.abdoahmed.todoapp.Database.Settings
import com.abdoahmed.todoapp.Database.TodoDataEntity
import com.abdoahmed.todoapp.Database.UserAuthanticationDataEntity
import com.abdoahmed.todoapp.Database.__DatabaseBuilder__

class implementation (private val dp : __DatabaseBuilder__):DatabaseAccessOpject{


    override suspend fun __insertNewUserData__(userAuthanticationDataEntity: UserAuthanticationDataEntity):Long{

        if (dp.userDatabaseAccessObject().__RetrieveSpecificUserData__(userAuthanticationDataEntity.username)!=null){
            return 0L
        }
        else{
            dp.userDatabaseAccessObject().__insertNewUserData__(userAuthanticationDataEntity)
            return 1L
        }
    }


    override suspend fun __RetrieveSpecificUserData__(username: String): UserAuthanticationDataEntity? {
        return dp.userDatabaseAccessObject().__RetrieveSpecificUserData__(username)
    }






    override suspend fun __insertNewTodo__(todoDataEntity: TodoDataEntity) {
        dp.userDatabaseAccessObject().__insertNewTodo__(todoDataEntity)
    }

    override suspend fun __retrieveAllTodoS__(username: String): List<TodoDataEntity>{
         return dp.userDatabaseAccessObject().__retrieveAllTodoS__(username)
    }

    override suspend fun __DeleteTodo(id: Int) {
        dp.userDatabaseAccessObject().__DeleteTodo(id)
    }

    override suspend fun __SelectTodoOnId(id: Int?) :TodoDataEntity{
       return dp.userDatabaseAccessObject().__SelectTodoOnId(id)
    }

    override suspend fun __UpdateTodo(cont: String, Title: String, id: Int,startTime:String,endTiem:String) {
        dp.userDatabaseAccessObject().__UpdateTodo(cont,Title,id,startTime,endTiem)
        Log.d("test Update","Update executed")
    }

    override suspend fun __insertDafaultSettings__(settings: Settings) {
        dp.userDatabaseAccessObject().__insertDafaultSettings__(settings)
    }

    override suspend fun __retrieveSettings(username: String): Settings {
       return dp.userDatabaseAccessObject().__retrieveSettings(username)

    }

    override suspend fun __UpdateDeleteState(deleteState: Boolean, username: String) {
        dp.userDatabaseAccessObject().__UpdateDeleteState(deleteState,username)
    }

    override suspend fun __changeUpdateState(updateState: Boolean, username: String) {
        dp.userDatabaseAccessObject().__changeUpdateState(updateState,username)
    }

    override suspend fun __UpdateTodoCompleteState(id: Int, state: Int) {
        dp.userDatabaseAccessObject().__UpdateTodoCompleteState(id,state)
    }

    override suspend fun updateAlarmStatus(todoId: Int, isActive:Int) {
        dp.userDatabaseAccessObject().updateAlarmStatus(todoId,isActive)
    }


}
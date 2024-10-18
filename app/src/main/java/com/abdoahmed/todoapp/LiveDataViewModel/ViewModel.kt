package com.abdoahmed.todoapp.LiveDataViewModel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation
import com.abdoahmed.todoapp.Database.TodoDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommonViewModel(private val repository: implementation) : ViewModel() {

    private val _todoListData: MutableLiveData<List<TodoDataEntity>> = MutableLiveData()
    val todoListData: LiveData<List<TodoDataEntity>> get() = _todoListData

    val username: MutableLiveData<String> = MutableLiveData()

    val isDeleteDisabled: MutableLiveData<Boolean> = MutableLiveData()


    fun getTodoListFromDP() {
        val currentUsername = username.value ?: return
        // Fetch the list from the repository and post it to LiveData
        viewModelScope.launch(Dispatchers.IO) {
            val todos = repository.__retrieveAllTodoS__(currentUsername)
            _todoListData.postValue(todos)
        }
    }

    fun addTodo(todo: TodoDataEntity) {
        viewModelScope.launch (Dispatchers.IO){
            repository.__insertNewTodo__(todo)
            // Refresh the todo list after insertion
            getTodoListFromDP()
        }
    }




    fun deleteTodo(todoId: Int) {
        viewModelScope.launch (Dispatchers.IO){
            repository.__DeleteTodo(todoId)
            // Refresh the todo list after deletion
            getTodoListFromDP()
        }
    }

    fun updateUsername(newUsername: String) {
        username.value = newUsername
        Log.i("username view model test","${username.value}")
    }

    fun getUsername():String {

        return username.value.toString()

    }

}




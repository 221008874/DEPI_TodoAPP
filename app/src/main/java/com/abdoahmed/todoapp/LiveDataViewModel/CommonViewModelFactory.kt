package com.abdoahmed.todoapp.LiveDataViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdoahmed.todoapp.Database.Dao_Implementation.implementation

class CommonViewModelFactory(private val repository: implementation) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommonViewModel::class.java)) {
            return CommonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
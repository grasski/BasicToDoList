package com.example.basictodolist.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basictodolist.db.Group
import com.example.basictodolist.db.GroupWithTasks
import com.example.basictodolist.db.Task
import com.example.basictodolist.db.generateRandomColors
import com.example.basictodolist.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    val allTasks: StateFlow<List<Task>?> = repository.allTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    val groupWithTasks: StateFlow<List<GroupWithTasks>?> = repository.groupWithTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)


    private val _selectedGroupId = MutableStateFlow<Int?>(null)
    val selectedGroupId: StateFlow<Int?> = _selectedGroupId.asStateFlow()
    fun setSelectedGroup(id: Int?){
        _selectedGroupId.update { id }
    }

    private val _workingTaskId = MutableStateFlow<Int?>(null)
    val workingTaskId = _workingTaskId.asStateFlow()

    fun createUpdateTask(task: Task) {
        _workingTaskId.value?.let {
            task.id = it
        }

        if (task.taskText.replace(" ", "").isNotEmpty()){
            viewModelScope.launch {
                val id = repository.upsertTask(task)

                _workingTaskId.value?.let {} ?: _workingTaskId.update { id.toInt() }
            }
        } else{
            deleteTask(task)
        }
    }
    fun deleteTask(task: Task){
        viewModelScope.launch {
            repository.deleteTask(task)
        }
        _workingTaskId.update { null }
    }

    fun updateTaskStatus(task: Task){
        viewModelScope.launch {
            repository.upsertTask(task)
        }
    }

    fun setWorkingTaskId(id: Int?){
        _workingTaskId.update { id }
    }


    fun createUpdateGroup(group: Group) {
        viewModelScope.launch {
            repository.upsertGroup(group)
        }
    }
    fun deleteGroup(group: Group){
        viewModelScope.launch {
            repository.deleteGroup(group)
        }
    }
    fun changeColor(id: Int){
        val colors = generateRandomColors(3)
        viewModelScope.launch {
            repository.updateGroupColor(id, colors)
        }
    }
}
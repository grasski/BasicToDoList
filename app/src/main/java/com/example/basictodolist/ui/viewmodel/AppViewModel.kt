package com.example.basictodolist.ui.viewmodel


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
        if (task.taskText.isNotBlank()){
            viewModelScope.launch {
                repository.upsertTask(task)
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
    fun finishOrResetAllGroupTasks(tasks: List<Task>?, status: Boolean){
        viewModelScope.launch {
            tasks?.forEach {
                it.finished = status
                updateTaskStatus(it)
            }
        }
    }

    fun setWorkingTaskId(id: Int?){
        _workingTaskId.update { id }
    }


    suspend fun createUpdateGroup(group: Group): Long? {
        var id: Long? = null
        viewModelScope.launch {
            id = repository.upsertGroup(group)
        }.join()
        return id
    }
    fun deleteGroup(group: Group, tasks: List<Task>?){
        viewModelScope.launch {
            tasks?.forEach {
                deleteTask(it)
            }
            setSelectedGroup(null)
        }.invokeOnCompletion {
            viewModelScope.launch {
                repository.deleteGroup(group)
            }
        }
    }
    fun changeColor(id: Int){
        val colors = generateRandomColors(3)
        viewModelScope.launch {
            repository.updateGroupColor(id, colors)
        }
    }
}

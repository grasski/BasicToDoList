package com.example.basictodolist.repository

import androidx.annotation.WorkerThread
import androidx.compose.ui.graphics.Color
import com.example.basictodolist.db.DatabaseDao
import com.example.basictodolist.db.Group
import com.example.basictodolist.db.GroupWithTasks
import com.example.basictodolist.db.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


class AppRepository(private val dbDao: DatabaseDao) {

    val allTasks: Flow<List<Task>> = dbDao.getAllTasks()

    val groupWithTasks: Flow<List<GroupWithTasks>> = dbDao.getGroupWithTasks()


    @WorkerThread
    suspend fun upsertTask(task: Task) = dbDao.upsertTask(task)
    @WorkerThread
    suspend fun deleteTask(task: Task) = dbDao.deleteTask(task)


    @WorkerThread
    suspend fun upsertGroup(group: Group): Long = dbDao.upsertGroup(group)
    @WorkerThread
    suspend fun deleteGroup(group: Group) = dbDao.deleteGroup(group)

    @WorkerThread
    suspend fun updateGroupColor(id: Int, colors: List<Color>) = dbDao.updateGroupColor(id, colors)


}
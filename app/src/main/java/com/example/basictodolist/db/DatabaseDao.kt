package com.example.basictodolist.db

import androidx.compose.ui.graphics.Color
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.basictodolist.Constants.GROUP_TABLE_NAME
import com.example.basictodolist.Constants.TASK_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {

    @Upsert
    suspend fun upsertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM $TASK_TABLE_NAME")
    fun getAllTasks(): Flow<List<Task>>



    @Upsert
    suspend fun upsertGroup(group: Group): Long

    @Delete
    suspend fun deleteGroup(group: Group)

    @Query("SELECT * FROM $GROUP_TABLE_NAME")
    fun getAllGroups(): Flow<List<Group>>

    @Query("UPDATE $GROUP_TABLE_NAME SET colors=:colors WHERE id=:id")
    suspend fun updateGroupColor(id: Int, colors: List<Color>)


    @Query("SELECT * FROM $GROUP_TABLE_NAME")
    fun getGroupWithTasks(): Flow<List<GroupWithTasks>>
}
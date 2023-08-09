package com.example.basictodolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.basictodolist.Constants.TASK_TABLE_NAME

@Entity(
    tableName = TASK_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"]
        )
    ]
)
data class Task(
    var taskText: String = "",

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var finished: Boolean = false,
    var category: String? = null,

    @ColumnInfo(index = true)
    var groupId: Int? = null
)

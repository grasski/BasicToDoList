package com.example.basictodolist.db

import androidx.compose.ui.graphics.Color
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.basictodolist.Constants.GROUP_TABLE_NAME
import kotlin.random.Random

@Entity(
    tableName = GROUP_TABLE_NAME
)
data class Group(
    var name: String = "",
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var colors: List<Color> = generateRandomColors(3)
)


data class GroupWithTasks(
    @Embedded
    val group: Group,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val tasks: List<Task>
)


fun generateRandomColors(count: Int): List<Color> {
    val colors = mutableListOf<Color>()
    val minValue = 0.0f
    val maxValue = 0.7f

    repeat(count) {
        val r = minValue + Random.nextFloat() * (maxValue - minValue)
        val g = minValue + Random.nextFloat() * (maxValue - minValue)
        val b = minValue + Random.nextFloat() * (maxValue - minValue)

        val color = Color(r,g,b)
        colors.add(color)
    }

    return colors
}
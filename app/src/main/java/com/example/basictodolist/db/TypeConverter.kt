package com.example.basictodolist.db

import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class TypeConverter {

    @TypeConverter
    fun fromColorList(colors: List<Color>): String {
        return Gson().toJson(colors)
    }
    @TypeConverter
    fun toColorList(colorsString: String): List<Color> {
        return try {
            val type = object : TypeToken<List<Color>>() {}.type
            Gson().fromJson(colorsString, type)
        } catch(e: Exception) {
            emptyList()
        }
    }

}
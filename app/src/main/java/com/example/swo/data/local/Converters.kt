package com.example.swo.data.local

import androidx.room.TypeConverter
import com.example.swo.domain.model.TimelineEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromTimelineList(value: List<TimelineEvent>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toTimelineList(value: String): List<TimelineEvent> {
        val listType = object : TypeToken<List<TimelineEvent>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }
}

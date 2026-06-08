package com.example.swo.data.categories.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val color: String,
    val createdAt: Long = System.currentTimeMillis()
)

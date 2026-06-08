package com.example.swo.data.reports.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swo.model.Report

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val data: String,
    val createdAt: String
)

fun ReportEntity.toDomain() = Report(id, title, type, data, createdAt)
fun Report.toEntity() = ReportEntity(id, title, type, data, createdAt)

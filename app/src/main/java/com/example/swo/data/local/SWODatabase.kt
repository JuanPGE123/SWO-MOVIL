package com.example.swo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.swo.data.categories.local.CategoryDao
import com.example.swo.data.categories.local.CategoryEntity
import com.example.swo.data.chatbot.local.ChatDao
import com.example.swo.data.chatbot.local.ChatMessageEntity
import com.example.swo.data.comentarios.local.ComentarioDao
import com.example.swo.data.comentarios.local.ComentarioEntity
import com.example.swo.data.incidents.local.IncidentDao
import com.example.swo.data.incidents.local.IncidentEntity
import com.example.swo.data.notificaciones.local.NotificacionDao
import com.example.swo.data.notificaciones.local.NotificacionEntity
import com.example.swo.data.projects.local.ProjectDao
import com.example.swo.data.projects.local.ProjectEntity
import com.example.swo.data.reports.local.ReportDao
import com.example.swo.data.reports.local.ReportEntity
import com.example.swo.data.users.local.UserDao
import com.example.swo.data.users.local.UserEntity

@Database(
    entities = [
        IncidentEntity::class,
        ProjectEntity::class,
        UserEntity::class,
        ReportEntity::class,
        ChatMessageEntity::class,
        CategoryEntity::class,
        ComentarioEntity::class,
        NotificacionEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SWODatabase : RoomDatabase() {
    abstract fun incidentDao(): IncidentDao
    abstract fun projectDao(): ProjectDao
    abstract fun userDao(): UserDao
    abstract fun reportDao(): ReportDao
    abstract fun chatDao(): ChatDao
    abstract fun categoryDao(): CategoryDao
    abstract fun comentarioDao(): ComentarioDao
    abstract fun notificacionDao(): NotificacionDao

    companion object {
        const val DATABASE_NAME = "swo_db"

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS comentarios (
                        idComentario TEXT NOT NULL PRIMARY KEY,
                        texto TEXT NOT NULL,
                        fecha INTEGER NOT NULL,
                        idIncidencia TEXT NOT NULL,
                        idUsuario TEXT NOT NULL,
                        nombreUsuario TEXT NOT NULL,
                        esPublico INTEGER NOT NULL DEFAULT 1
                    )"""
                )
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS notificaciones (
                        idNotificacion TEXT NOT NULL PRIMARY KEY,
                        mensaje TEXT NOT NULL,
                        fecha INTEGER NOT NULL,
                        leida INTEGER NOT NULL DEFAULT 0,
                        idUsuario TEXT NOT NULL,
                        idIncidencia TEXT,
                        tipo TEXT NOT NULL DEFAULT 'GENERAL'
                    )"""
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN password TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS categories (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        color TEXT NOT NULL,
                        createdAt INTEGER NOT NULL DEFAULT 0
                    )"""
                )
                db.execSQL(
                    "ALTER TABLE incidents ADD COLUMN categoryId TEXT"
                )
            }
        }
    }
}

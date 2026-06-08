package com.example.swo.data.local

import android.util.Log
import com.example.swo.data.categories.local.CategoryDao
import com.example.swo.data.categories.local.CategoryEntity
import com.example.swo.data.chatbot.local.ChatDao
import com.example.swo.data.chatbot.local.ChatMessageEntity
import com.example.swo.data.incidents.local.IncidentDao
import com.example.swo.data.incidents.local.IncidentEntity
import com.example.swo.data.projects.local.ProjectDao
import com.example.swo.data.projects.local.ProjectEntity
import com.example.swo.data.users.local.UserDao
import com.example.swo.data.users.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val userDao: UserDao,
    private val incidentDao: IncidentDao,
    private val projectDao: ProjectDao,
    private val chatDao: ChatDao,
    private val categoryDao: CategoryDao
) {
    suspend fun seedDatabase() {
        withContext(Dispatchers.IO) {
            try {
                // Solo sembrar si la BD está vacía
                if (userDao.getCount() > 0) {
                    Log.d("DatabaseSeeder", "BD ya inicializada, omitiendo seed.")
                    return@withContext
                }
                // --- USUARIOS ---
                val users = listOf(
                    UserEntity(id = "U1", name = "Juan Pablo Giraldo", email = "admin@swo.com", password = "admin123", role = "ADMIN", isActive = true, avatarUrl = null),
                    UserEntity(id = "U2", name = "María Fernanda López", email = "mfernanda@swo.com", password = "tech123", role = "TECHNICIAN", isActive = true, avatarUrl = null),
                    UserEntity(id = "U3", name = "Carlos Ramírez", email = "carlos@swo.com", password = "client123", role = "CLIENT", isActive = true, avatarUrl = null),
                    UserEntity(id = "U4", name = "Ana Sofía Torres", email = "ana@swo.com", password = "tech123", role = "TECHNICIAN", isActive = true, avatarUrl = null),
                    UserEntity(id = "U5", name = "Pedro Jiménez", email = "pedro@swo.com", password = "client123", role = "CLIENT", isActive = false, avatarUrl = null)
                )
                userDao.insertUsers(users)

                // --- CATEGORÍAS ---
                val categories = listOf(
                    CategoryEntity(id = "CAT1", name = "Hardware", description = "Fallos físicos de equipos", color = "#F59E0B"),
                    CategoryEntity(id = "CAT2", name = "Software", description = "Errores en aplicaciones", color = "#3B82F6"),
                    CategoryEntity(id = "CAT3", name = "Red / Conectividad", description = "Problemas de red e internet", color = "#10B981"),
                    CategoryEntity(id = "CAT4", name = "Seguridad", description = "Incidentes de seguridad informática", color = "#EF4444"),
                    CategoryEntity(id = "CAT5", name = "Base de Datos", description = "Fallos en BD y datos", color = "#8B5CF6"),
                    CategoryEntity(id = "CAT6", name = "Acceso / Autenticación", description = "Problemas de login y permisos", color = "#F97316")
                )
                categoryDao.insertCategories(categories)

                // --- PROYECTOS ---
                val projects = listOf(
                    ProjectEntity(id = "P1", name = "Portal Corporativo", description = "Sistema web principal de la empresa", status = "ACTIVE", activeIncidentsCount = 4, assignedEngineers = "[\"U2\", \"U4\"]", isSynced = true),
                    ProjectEntity(id = "P2", name = "App Móvil SWO", description = "Aplicación Android para service desk", status = "ACTIVE", activeIncidentsCount = 2, assignedEngineers = "[\"U2\"]", isSynced = true),
                    ProjectEntity(id = "P3", name = "Infraestructura Cloud", description = "Migración a servicios en la nube", status = "ACTIVE", activeIncidentsCount = 1, assignedEngineers = "[\"U4\"]", isSynced = true),
                    ProjectEntity(id = "P4", name = "ERP Financiero", description = "Sistema de gestión financiera", status = "INACTIVE", activeIncidentsCount = 0, assignedEngineers = "[\"U2\", \"U4\"]", isSynced = true)
                )
                projectDao.insertProjects(projects)

                // --- INCIDENTES (con datos ricos para reportes reales) ---
                val now = System.currentTimeMillis()
                val day = 86_400_000L

                val incidents = listOf(
                    IncidentEntity(id = "I001", title = "Fallo de inicio de sesión en portal", description = "Los usuarios no pueden acceder al portal corporativo. Error 401 en todos los intentos.", status = "OPEN", priority = "CRITICAL", reportedBy = "U3", projectName = "Portal Corporativo", createdAt = "2026-06-01T08:30:00Z", assignedTo = "U2", ciudad = "Bogotá", entorno = "Producción", categoryId = "CAT6", lastModified = now - day, isSynced = true),
                    IncidentEntity(id = "I002", title = "Consultas SQL lentas en reportes", description = "La generación de reportes tarda más de 5 minutos. Timeout frecuente en producción.", status = "IN_PROGRESS", priority = "HIGH", reportedBy = "U1", projectName = "Portal Corporativo", createdAt = "2026-05-31T14:30:00Z", assignedTo = "U4", ciudad = "Medellín", entorno = "Producción", categoryId = "CAT5", lastModified = now - 2 * day, isSynced = true),
                    IncidentEntity(id = "I003", title = "Error tipográfico en footer", description = "Hay un error ortográfico en el pie de página de la home.", status = "RESOLVED", priority = "LOW", reportedBy = "U3", projectName = "Portal Corporativo", createdAt = "2026-05-28T09:15:00Z", assignedTo = "U2", solutionNotes = "Corregido el texto en el template HTML.", categoryId = "CAT2", lastModified = now - 4 * day, isSynced = true),
                    IncidentEntity(id = "I004", title = "App Android no abre en Samsung S22", description = "La aplicación falla al iniciar en dispositivos Samsung Galaxy S22 con Android 14.", status = "IN_PROGRESS", priority = "HIGH", reportedBy = "U3", projectName = "App Móvil SWO", createdAt = "2026-05-30T11:00:00Z", assignedTo = "U2", ciudad = "Cali", entorno = "Producción", categoryId = "CAT2", lastModified = now - 2 * day, isSynced = true),
                    IncidentEntity(id = "I005", title = "Caída de red en sede principal", description = "Pérdida total de conectividad en la sede central. Afecta a 50+ usuarios.", status = "RESOLVED", priority = "CRITICAL", reportedBy = "U1", projectName = "Infraestructura Cloud", createdAt = "2026-05-27T07:45:00Z", assignedTo = "U4", solutionNotes = "Se reemplazó el switch de core defectuoso.", ciudad = "Bogotá", entorno = "Producción", categoryId = "CAT3", lastModified = now - 5 * day, isSynced = true),
                    IncidentEntity(id = "I006", title = "Intento de acceso no autorizado", description = "Múltiples intentos de fuerza bruta detectados en el sistema de autenticación.", status = "RESOLVED", priority = "CRITICAL", reportedBy = "U1", projectName = "Portal Corporativo", createdAt = "2026-05-25T22:10:00Z", assignedTo = "U4", solutionNotes = "Se bloquearon las IPs y se habilitó 2FA obligatorio.", entorno = "Producción", categoryId = "CAT4", lastModified = now - 7 * day, isSynced = true),
                    IncidentEntity(id = "I007", title = "Impresora de red sin conexión", description = "La impresora del departamento contable no responde en red.", status = "RESOLVED", priority = "MEDIUM", reportedBy = "U5", projectName = "Infraestructura Cloud", createdAt = "2026-05-24T10:30:00Z", assignedTo = "U2", solutionNotes = "Se reinstalaron los drivers y se reasignó la IP.", ciudad = "Bogotá", entorno = "Oficina", categoryId = "CAT1", lastModified = now - 8 * day, isSynced = true),
                    IncidentEntity(id = "I008", title = "Módulo de nómina no calcula horas extra", description = "El ERP financiero no suma correctamente las horas extra en el cálculo de nómina.", status = "OPEN", priority = "HIGH", reportedBy = "U3", projectName = "ERP Financiero", createdAt = "2026-06-01T09:00:00Z", assignedTo = null, ciudad = "Medellín", entorno = "Producción", categoryId = "CAT2", lastModified = now, isSynced = true),
                    IncidentEntity(id = "I009", title = "Certificado SSL expirado", description = "El certificado SSL del dominio expiró y los navegadores muestran advertencia de seguridad.", status = "RESOLVED", priority = "CRITICAL", reportedBy = "U1", projectName = "Portal Corporativo", createdAt = "2026-05-20T06:00:00Z", assignedTo = "U4", solutionNotes = "Certificado renovado por 2 años.", entorno = "Producción", categoryId = "CAT4", lastModified = now - 12 * day, isSynced = true),
                    IncidentEntity(id = "I010", title = "Backup automático fallido", description = "El job de backup nocturno no se ejecutó durante 3 días consecutivos.", status = "RESOLVED", priority = "HIGH", reportedBy = "U4", projectName = "Infraestructura Cloud", createdAt = "2026-05-18T08:00:00Z", assignedTo = "U4", solutionNotes = "Se corrigió la tarea programada y se verificó el espacio en disco.", entorno = "Producción", categoryId = "CAT5", lastModified = now - 14 * day, isSynced = true),
                    IncidentEntity(id = "I011", title = "Error 500 en módulo de reportes", description = "Al generar reporte de ventas mensual el servidor responde con error 500.", status = "OPEN", priority = "HIGH", reportedBy = "U3", projectName = "Portal Corporativo", createdAt = "2026-06-02T07:00:00Z", assignedTo = "U2", entorno = "Producción", categoryId = "CAT2", lastModified = now, isSynced = true),
                    IncidentEntity(id = "I012", title = "Notificaciones push no llegan", description = "Los usuarios de la app móvil reportan que no reciben notificaciones push desde hace 2 días.", status = "IN_PROGRESS", priority = "MEDIUM", reportedBy = "U3", projectName = "App Móvil SWO", createdAt = "2026-06-01T15:00:00Z", assignedTo = "U2", entorno = "Producción", categoryId = "CAT2", lastModified = now - day, isSynced = true)
                )
                incidentDao.insertIncidents(incidents)

                // --- CHATBOT: mensaje de bienvenida ---
                val welcomeMsg = ChatMessageEntity(
                    id = "BOT_WELCOME",
                    senderId = "bot",
                    text = "¡Hola! Soy el Asistente Virtual de SWO 👋 Estoy aquí para ayudarte con la gestión de incidencias, proyectos y usuarios. ¿En qué puedo ayudarte hoy?",
                    timestamp = System.currentTimeMillis() - 1000,
                    isFromBot = true
                )
                chatDao.insertMessage(welcomeMsg)

                Log.d("DatabaseSeeder", "BD inicializada: ${users.size} usuarios, ${categories.size} categorías, ${projects.size} proyectos, ${incidents.size} incidentes.")
            } catch (e: Exception) {
                Log.e("DatabaseSeeder", "Error al inicializar la BD", e)
            }
        }
    }
}

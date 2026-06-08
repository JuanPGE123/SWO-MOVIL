# Configuración de Servidor y Acceso a Datos — SWO Mobile

Documentación técnica sobre la configuración de la URL del backend, los clientes HTTP, la base de datos local Room y los DAOs disponibles.

---

## Índice

1. [Configuración del servidor backend](#configuración-del-servidor-backend)
2. [Cliente HTTP (Retrofit + OkHttp)](#cliente-http-retrofit--okhttp)
3. [APIs REST disponibles](#apis-rest-disponibles)
4. [Base de datos local (Room)](#base-de-datos-local-room)
5. [DAOs y operaciones disponibles](#daos-y-operaciones-disponibles)
6. [Estrategia offline-first](#estrategia-offline-first)
7. [Inyección de dependencias (Hilt)](#inyección-de-dependencias-hilt)

---

## Configuración del servidor backend

### URL base por ambiente

La URL del servidor se configura en `app/build.gradle.kts` mediante `BuildConfig`. No está hardcodeada en el código fuente.

```kotlin
// app/build.gradle.kts
buildTypes {
    debug {
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/v1/\"")
        applicationIdSuffix = ".debug"
        versionNameSuffix = "-debug"
    }
    release {
        buildConfigField("String", "BASE_URL", "\"https://api.swo-servicedesk.com/v1/\"")
        isMinifyEnabled = false
    }
}
```

| Ambiente | URL base | Notas |
|---|---|---|
| `debug` | `http://10.0.2.2:8080/api/v1/` | `10.0.2.2` = localhost del PC host desde el emulador Android |
| `release` | `https://api.swo-servicedesk.com/v1/` | Servidor de producción |

### Cambiar la URL para dispositivo físico

En un dispositivo físico, `10.0.2.2` no funciona. Reemplazar la IP del emulador por la IP de la máquina en la red local:

```kotlin
debug {
    // Ejemplo: PC host con IP 192.168.1.105
    buildConfigField("String", "BASE_URL", "\"http://192.168.1.105:8080/api/v1/\"")
}
```

Para obtener la IP del PC en Windows:
```powershell
ipconfig
# Buscar "Dirección IPv4" en el adaptador de red activo
```

### Acceso en el código

```kotlin
// NetworkModule.kt
fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)   // <-- proviene del BuildConfig generado
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}
```

---

## Cliente HTTP (Retrofit + OkHttp)

### Versiones

| Librería | Versión | Artifacto |
|---|---|---|
| Retrofit | 2.9.0 | `com.squareup.retrofit2:retrofit` |
| Retrofit Gson Converter | 2.9.0 | `com.squareup.retrofit2:converter-gson` |
| OkHttp | 4.12.0 | `com.squareup.okhttp3:okhttp` |
| OkHttp Logging Interceptor | 4.12.0 | `com.squareup.okhttp3:logging-interceptor` |

### Configuración completa

**Archivo:** `app/src/main/java/com/example/swo/core/di/NetworkModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY  // Loguea headers + body en debug
        }
    }

    @Provides @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}
```

> **Nota de seguridad:** En producción considerar reducir el nivel de logging a `HttpLoggingInterceptor.Level.NONE` o controlarlo por `BuildConfig.DEBUG`.

---

## APIs REST disponibles

Todas las interfaces heredan de Retrofit y son provistas por `NetworkModule`.

### IncidentApi

**Archivo:** `app/src/main/java/com/example/swo/data/incidents/remote/IncidentApi.kt`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/incidents` | Listar todas las incidencias |
| `GET` | `/incidents/{id}` | Obtener incidencia por ID |
| `POST` | `/incidents` | Crear nueva incidencia |
| `PUT` | `/incidents/{id}` | Actualizar incidencia |

### ProjectApi

**Archivo:** `app/src/main/java/com/example/swo/data/projects/remote/ProjectApi.kt`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/projects` | Listar todos los proyectos |
| `POST` | `/projects` | Crear proyecto |
| `PUT` | `/projects/{id}` | Actualizar proyecto |

### UserApi

**Archivo:** `app/src/main/java/com/example/swo/data/users/remote/UserApi.kt`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/users` | Listar usuarios |
| `POST` | `/users` | Crear usuario |
| `PUT` | `/users/{id}` | Actualizar usuario |

### ChatbotApi

**Archivo:** `app/src/main/java/com/example/swo/data/chatbot/remote/ChatbotApi.kt`

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/chatbot/message` | Enviar mensaje al chatbot |

---

## Base de datos local (Room)

### Versión actual

**Versión de Room:** `2.7.0-alpha11`  
**Versión de la base de datos:** `5`  
**Archivo de base de datos:** `SWODatabase.kt`

### Entidades registradas

| Entidad | Tabla en SQLite | Módulo |
|---|---|---|
| `IncidentEntity` | `incidents` | Incidencias |
| `UserEntity` | `users` | Usuarios |
| `ProjectEntity` | `projects` | Proyectos |
| `CategoryEntity` | `categories` | Categorías |
| `ReportEntity` | `reports` | Reportes |
| `ChatMessageEntity` | `chat_messages` | Chatbot |
| `ComentarioEntity` | `comentarios` | Comentarios |
| `NotificacionEntity` | `notificaciones` | Notificaciones |

### Historial de migraciones

```kotlin
// SWODatabase.kt
val MIGRATION_4_5 = Migration(4, 5) { database ->
    database.execSQL("""
        CREATE TABLE IF NOT EXISTS comentarios (
            idComentario TEXT NOT NULL PRIMARY KEY,
            texto TEXT NOT NULL,
            fecha INTEGER NOT NULL,
            idIncidencia TEXT NOT NULL,
            idUsuario TEXT NOT NULL,
            nombreUsuario TEXT NOT NULL,
            esPublico INTEGER NOT NULL DEFAULT 1
        )
    """)
    database.execSQL("""
        CREATE TABLE IF NOT EXISTS notificaciones (
            idNotificacion TEXT NOT NULL PRIMARY KEY,
            mensaje TEXT NOT NULL,
            fecha INTEGER NOT NULL,
            leida INTEGER NOT NULL DEFAULT 0,
            idUsuario TEXT NOT NULL,
            idIncidencia TEXT,
            tipo TEXT NOT NULL DEFAULT 'GENERAL'
        )
    """)
}
```

| Versión | Cambios |
|---|---|
| 1 → 2 | Estructura inicial de incidencias, usuarios, proyectos |
| 2 → 3 | Agregadas categorías y reportes |
| 3 → 4 | Agregado módulo de chatbot |
| 4 → 5 | Agregadas tablas `comentarios` y `notificaciones` |

### Inicialización con datos de prueba

El `DatabaseSeeder` se ejecuta al primer arranque y carga datos en las tablas principales.

**Archivo:** `app/src/main/java/com/example/swo/data/local/DatabaseSeeder.kt`

| Entidad | Registros de prueba |
|---|---|
| Usuarios | 5 (1 admin, 2 técnicos, 2 clientes) |
| Categorías | 6 (Hardware, Software, Red, Impresoras, Periféricos, Servidores) |
| Proyectos | 4 proyectos activos |
| Incidencias | 12 incidencias de ejemplo |

---

## DAOs y operaciones disponibles

### IncidentDao

**Archivo:** `app/src/main/java/com/example/swo/data/incidents/local/IncidentDao.kt`

```kotlin
@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT COUNT(*) FROM incidents")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE status = 'OPEN'")
    fun getOpenCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE status = 'IN_PROGRESS'")
    fun getInProgressCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE status = 'RESOLVED'")
    fun getResolvedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE status = 'CANCELLED'")
    fun getCancelledCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE priority = 'CRITICAL'")
    fun getCriticalCount(): Flow<Int>

    @Query("SELECT * FROM incidents WHERE status IN ('OPEN','IN_PROGRESS') ORDER BY createdAt DESC LIMIT 5")
    fun getRecentActiveIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT projectName, COUNT(*) as count FROM incidents GROUP BY projectName")
    fun getIncidentsByProject(): Flow<List<ProjectIncidentCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incidents: List<IncidentEntity>)

    @Delete
    suspend fun delete(incident: IncidentEntity)
}
```

### UserDao

**Archivo:** `app/src/main/java/com/example/swo/data/users/local/UserDao.kt`

| Operación | Descripción |
|---|---|
| `getAllUsers(): Flow<List<UserEntity>>` | Todos los usuarios como Flow reactivo |
| `getUserByCredentials(email, password)` | Buscar usuario para login |
| `getUserById(id)` | Buscar usuario por ID |
| `insertAll(users)` | Insertar/reemplazar múltiples usuarios |
| `delete(user)` | Eliminar un usuario |

### ProjectDao

**Archivo:** `app/src/main/java/com/example/swo/data/projects/local/ProjectDao.kt`

| Operación | Descripción |
|---|---|
| `getAllProjects(): Flow<List<ProjectEntity>>` | Todos los proyectos |
| `getCount(): Flow<Int>` | Conteo total de proyectos |
| `insertAll(projects)` | Insertar/reemplazar proyectos |

### ComentarioDao

**Archivo:** `app/src/main/java/com/example/swo/data/comentarios/local/ComentarioDao.kt`

| Operación | Descripción |
|---|---|
| `getByIncidencia(idIncidencia)` | Comentarios de una incidencia |
| `getPublicosByIncidencia(idIncidencia)` | Solo comentarios públicos |
| `insert(comentario)` | Insertar comentario |
| `delete(comentario)` | Eliminar comentario |
| `countByIncidencia(idIncidencia)` | Contar comentarios de una incidencia |

### NotificacionDao

**Archivo:** `app/src/main/java/com/example/swo/data/notificaciones/local/NotificacionDao.kt`

| Operación | Descripción |
|---|---|
| `getByUsuario(idUsuario)` | Notificaciones de un usuario |
| `getNoLeidas(idUsuario)` | Solo notificaciones no leídas |
| `countNoLeidas(idUsuario): Flow<Int>` | Badge de notificaciones |
| `marcarLeida(idNotificacion)` | Marcar una como leída |
| `marcarTodasLeidas(idUsuario)` | Marcar todas como leídas |
| `deleteLeidas(idUsuario)` | Limpiar notificaciones leídas |

---

## Estrategia offline-first

La app funciona sin conexión gracias a los datos locales de Room. Los repositorios siguen este patrón:

```
┌────────────────────────────────────┐
│            ViewModel               │
└──────────────┬─────────────────────┘
               │
┌──────────────▼─────────────────────┐
│          Repository                │
│  1. Lee de Room (inmediato)        │
│  2. Intenta sincronizar con API    │
│  3. Si hay red → actualiza Room    │
│  4. Room emite el nuevo valor      │
└──────────────┬─────────────────────┘
               │
       ┌───────┴───────┐
       │               │
┌──────▼──────┐ ┌──────▼──────┐
│  Room (DAO) │ │ Retrofit API│
│  Offline    │ │  Online     │
└─────────────┘ └─────────────┘
```

El `SyncWorker` (WorkManager) ejecuta sincronización en background de forma periódica cuando hay conectividad.

---

## Inyección de dependencias (Hilt)

### DatabaseModule

**Archivo:** `app/src/main/java/com/example/swo/core/di/DatabaseModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): SWODatabase {
        return Room.databaseBuilder(ctx, SWODatabase::class.java, "swo_database")
            .addMigrations(MIGRATION_4_5)
            .build()
    }

    @Provides fun provideIncidentDao(db: SWODatabase) = db.incidentDao()
    @Provides fun provideUserDao(db: SWODatabase)     = db.userDao()
    @Provides fun provideProjectDao(db: SWODatabase)  = db.projectDao()
    @Provides fun provideCategoryDao(db: SWODatabase) = db.categoryDao()
    @Provides fun provideReportDao(db: SWODatabase)   = db.reportDao()
    @Provides fun provideChatDao(db: SWODatabase)     = db.chatMessageDao()
    @Provides fun provideComentarioDao(db: SWODatabase)    = db.comentarioDao()
    @Provides fun provideNotificacionDao(db: SWODatabase)  = db.notificacionDao()
}
```

### NetworkModule

**Archivo:** `app/src/main/java/com/example/swo/core/di/NetworkModule.kt`

Provee: `HttpLoggingInterceptor` → `OkHttpClient` → `Retrofit` → `IncidentApi`, `ProjectApi`, `UserApi`, `ChatbotApi`, `IncidentService`

Todos con scope `@Singleton` para reutilización a nivel de aplicación.

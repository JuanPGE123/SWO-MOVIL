# SWO Mobile — Service Desk Android App

Aplicación Android para el **Sistema de Gestión de Incidencias SWO** (SENA). Permite registrar, consultar y gestionar incidencias de soporte técnico desde dispositivos móviles.

---

## Tabla de contenido

1. [Tecnologías y librerías](#tecnologías-y-librerías)
2. [Arquitectura](#arquitectura)
3. [Estructura de paquetes](#estructura-de-paquetes)
4. [Ambiente de desarrollo](#ambiente-de-desarrollo)
5. [Ambiente de pruebas](#ambiente-de-pruebas)
6. [Configuración del servidor backend](#configuración-del-servidor-backend)
7. [Ejecución de pruebas unitarias](#ejecución-de-pruebas-unitarias)
8. [Módulos implementados](#módulos-implementados)
9. [Repositorio](#repositorio)

---

## Tecnologías y librerías

### Lenguaje y plataforma
| Tecnología | Versión |
|---|---|
| Kotlin | 2.2.10 |
| Android SDK (compileSdk) | 35 (Android 15) |
| minSdk | 24 (Android 7.0) |

### Frameworks y librerías principales

#### Capa de Presentación (UI)
| Librería | Uso |
|---|---|
| Jetpack Compose BOM 2024.09 | Framework declarativo de UI |
| Material 3 | Componentes de diseño |
| Navigation Compose 2.8.3 | Navegación entre pantallas |
| Lottie Compose 6.4 | Animaciones vectoriales |
| Coil Compose 2.6 | Carga asíncrona de imágenes |
| MPAndroidChart v3.1.0 | Gráficos para reportes |

#### Capa de Negocio / Inyección de Dependencias
| Librería | Uso |
|---|---|
| Hilt 2.59.2 | Inyección de dependencias (DI) |
| Lifecycle ViewModel Compose 2.8.6 | ViewModels con Compose |
| Coroutines + Flow | Programación asíncrona reactiva |

#### Capa de Datos
| Librería | Uso |
|---|---|
| Room 2.7 | Base de datos local SQLite |
| Retrofit 2.9 | Cliente HTTP REST |
| OkHttp 4.12 | Interceptores y logging HTTP |
| DataStore Preferences | Almacenamiento clave-valor |
| WorkManager 2.9.1 | Sincronización en background |

#### Seguridad y Notificaciones
| Librería | Uso |
|---|---|
| AndroidX Biometric 1.2 | Autenticación biométrica (huella) |
| Security Crypto 1.1 | Cifrado de datos sensibles |
| Firebase Cloud Messaging | Notificaciones push |

---

## Arquitectura

El proyecto sigue **Clean Architecture** con el patrón **MVVM**:

```
┌─────────────────────────────────────────┐
│           CAPA DE PRESENTACIÓN          │
│  Compose Screens + ViewModels (MVVM)    │
├─────────────────────────────────────────┤
│           CAPA DE DOMINIO               │
│  Models + Interfaces de Repositorio     │
│  Use Cases                              │
├─────────────────────────────────────────┤
│           CAPA DE DATOS                 │
│  Room (local) + Retrofit (remoto)       │
│  Implementaciones de Repositorios       │
└─────────────────────────────────────────┘
```

**Patrones aplicados:**
- **Repository Pattern** — desacopla la fuente de datos de la lógica de negocio
- **Dependency Injection (Hilt)** — inversión de control en todos los módulos
- **Use Cases** — encapsulan operaciones de negocio individuales
- **StateFlow + collectAsState** — estado reactivo en la UI

---

## Estructura de paquetes

```
com.example.swo/
├── core/
│   ├── di/              # Módulos Hilt (DatabaseModule, NetworkModule, RepositoryModule)
│   ├── designsystem/    # Componentes UI reutilizables (SWOCard, etc.)
│   ├── notifications/   # Firebase Messaging Service
│   ├── security/        # BiometricAuthManager, SecurityManager
│   ├── sync/            # SyncWorker (WorkManager)
│   └── utils/           # FormValidator y utilidades generales
│
├── data/
│   ├── categories/      # Entidad, DAO y repositorio de categorías
│   ├── chatbot/         # Entidad, DAO, API y repositorio del chatbot
│   ├── comentarios/     # Entidad y DAO de comentarios de incidencias
│   ├── incidents/       # Entidad, DAO, API y repositorio de incidencias
│   ├── local/           # SWODatabase, Converters, DatabaseSeeder
│   ├── notificaciones/  # Entidad y DAO de notificaciones
│   ├── projects/        # Entidad, DAO, API y repositorio de proyectos
│   ├── reports/         # Entidad y DAO de reportes
│   └── users/           # Entidad, DAO, API y repositorio de usuarios
│
├── domain/
│   ├── incidents/       # IncidentRepository (interfaz) + GetIncidentsUseCase
│   ├── model/           # Modelos de dominio: Incident, User, Project, Comentario, Notificacion
│   ├── projects/        # ProjectRepository (interfaz)
│   └── users/           # UserRepository (interfaz)
│
└── ui/
    ├── chatbot/         # ChatbotScreen + ChatViewModel
    ├── components/      # Componentes visuales compartidos (Shimmer, Avatar, Glow)
    ├── dashboard/       # DashboardScreen + DashboardViewModel + SettingsScreen
    ├── incidents/       # IncidentFormScreen + IncidentFormViewModel
    ├── navigation/      # NavGraph + Screen (rutas)
    ├── projects/        # ProjectFormScreen + ProjectViewModel
    ├── reports/         # ReportsScreen + ReportsViewModel
    ├── screens/         # LoginScreen, IncidentsScreen, IncidentDetailScreen, ProjectsScreen
    ├── theme/           # Color, Theme, Type, Typography
    └── users/           # UsersScreen + UserFormScreen + UserViewModel
```

---

## Ambiente de desarrollo

### Requisitos previos

| Herramienta | Versión recomendada |
|---|---|
| Android Studio | Ladybug (2024.2.1) o superior |
| JDK | 17 (incluido en Android Studio) |
| Android SDK | API 35 (Android 15) |
| Gradle | 9.2.1 (gestionado por el wrapper) |
| Git | 2.x o superior |

### Pasos para configurar el entorno

```bash
# 1. Clonar el repositorio
git clone https://github.com/JuanPGE123/SWO-MOVIL.git
cd SWO-MOVIL

# 2. Abrir en Android Studio
#    File → Open → seleccionar la carpeta SWO-MOVIL

# 3. Sincronizar Gradle
#    Android Studio: File → Sync Project with Gradle Files
#    o en terminal:
./gradlew build

# 4. Ejecutar la app (emulador o dispositivo físico)
./gradlew :app:installDebug
```

### Variables de configuración

El archivo `local.properties` **no se sube al repositorio**. Si se requiere una URL personalizada para desarrollo, editar `app/build.gradle.kts`:

```kotlin
buildTypes {
    debug {
        // Cambiar por la IP del servidor de backend local:
        buildConfigField("String", "BASE_URL", "\"http://192.168.1.X:8080/api/v1/\"")
    }
}
```

> **Nota:** En el emulador de Android, `10.0.2.2` apunta al `localhost` del PC host. En dispositivo físico usar la IP de la máquina dentro de la red local.

---

## Ambiente de pruebas

### Usuarios de prueba (precargados en BD local)

| Correo | Contraseña | Rol |
|---|---|---|
| admin@swo.com | admin123 | Administrador |
| mfernanda@swo.com | tech123 | Técnico |
| carlos@swo.com | client123 | Cliente |

La base de datos se inicializa automáticamente con datos de prueba al primer arranque (`DatabaseSeeder`). Incluye 5 usuarios, 6 categorías, 4 proyectos y 12 incidencias de ejemplo.

### Pruebas unitarias

Las pruebas se encuentran en `app/src/test/java/com/example/swo/`:

| Clase de prueba | Módulo cubierto |
|---|---|
| `FormValidatorTest` | Validaciones de formularios (19 pruebas) |
| `LoginViewModelTest` | Autenticación y manejo de estados (7 pruebas) |
| `IncidentsViewModelTest` | Carga y filtrado de incidencias (5 pruebas) |
| `DashboardViewModelTest` | Estadísticas del dashboard (5 pruebas) |
| `ReportsViewModelTest` | Reportes y tasa de resolución (8 pruebas) |
| `ComentarioTest` | Mapeos del modelo Comentario (4 pruebas) |
| `NotificacionTest` | Mapeos y tipos de Notificacion (7 pruebas) |

**Total: 55 pruebas unitarias**

### Ejecutar las pruebas

```bash
# Todas las pruebas unitarias (JVM, sin emulador)
./gradlew :app:test

# Con reporte HTML
./gradlew :app:testDebugUnitTest

# El reporte se genera en:
# app/build/reports/tests/testDebugUnitTest/index.html
```

### Dependencias de testing

```kotlin
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.12")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
testImplementation("app.cash.turbine:turbine:1.2.0")
```

---

## Configuración del servidor backend

El backend Spring Boot expone una API REST. La URL base se configura por ambiente:

| Ambiente | URL base |
|---|---|
| **Debug** (emulador) | `http://10.0.2.2:8080/api/v1/` |
| **Release** (producción) | `https://api.swo-servicedesk.com/v1/` |

Para ejecutar el backend localmente:

```bash
# En el repositorio del backend (Spring Boot)
cd backend/
./mvnw spring-boot:run
# Servidor levantado en http://localhost:8080
```

El `DatabaseSeeder` provee datos locales para que la app funcione en modo offline sin necesidad del backend activo.

---

## Módulos implementados

| Módulo | Pantallas | ViewModel | Pruebas |
|---|---|---|---|
| Autenticación | LoginScreen | LoginViewModel | ✅ LoginViewModelTest |
| Dashboard | DashboardScreen | DashboardViewModel | ✅ DashboardViewModelTest |
| Incidencias | IncidentsScreen, IncidentDetailScreen, IncidentFormScreen | IncidentFormViewModel | ✅ IncidentsViewModelTest |
| Proyectos | ProjectsScreen, ProjectFormScreen | ProjectViewModel | — |
| Usuarios | UsersScreen, UserFormScreen | UserViewModel | — |
| Reportes | ReportsScreen | ReportsViewModel | ✅ ReportsViewModelTest |
| ChatBot | ChatbotScreen | ChatViewModel | — |
| Configuración | SettingsScreen | — | — |

---

## Repositorio

- **GitHub:** https://github.com/JuanPGE123/SWO-MOVIL.git
- **Control de versiones:** Git
- **Rama principal:** `main`

```bash
# Clonar
git clone https://github.com/JuanPGE123/SWO-MOVIL.git

# Ver historial
git log --oneline

# Crear rama de feature
git checkout -b feature/nombre-del-modulo
```

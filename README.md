# SWO Mobile — Service Desk Android App

Aplicación Android para el **Sistema de Gestión de Incidencias SWO** (SENA). Permite registrar, consultar y gestionar incidencias de soporte técnico desde dispositivos móviles.

---

## Tabla de contenido

1. [Repositorio y documentación](#repositorio-y-documentación)
2. [Stack tecnológico](#stack-tecnológico)
3. [Librerías por capa](#librerías-por-capa)
4. [Arquitectura](#arquitectura)
5. [Estructura de paquetes](#estructura-de-paquetes)
6. [Inicio rápido](#inicio-rápido)
7. [Módulos implementados](#módulos-implementados)

---

## Repositorio y documentación

| Recurso | Enlace |
|---|---|
| **Repositorio GitHub** | https://github.com/JuanPGE123/SWO-MOVIL.git |
| **Pruebas unitarias** | [docs/pruebas-unitarias.md](docs/pruebas-unitarias.md) |
| **Configuración servidor y datos** | [docs/configuracion-servidor-datos.md](docs/configuracion-servidor-datos.md) |
| **Ambientes de desarrollo y pruebas** | [docs/ambientes.md](docs/ambientes.md) |

---

## Stack tecnológico

| Componente | Tecnología | Versión |
|---|---|---|
| Lenguaje | Kotlin | 2.2.10 |
| Android SDK (compileSdk) | Android 15 | API 35 |
| SDK mínimo (minSdk) | Android 7.0 | API 24 |
| Build system | Gradle | 9.2.1 |
| Plugin AGP | Android Gradle Plugin | 9.2.1 |

---

## Librerías por capa

### Capa de Presentación (UI)

Responsable de renderizar la interfaz de usuario de forma declarativa.

| Librería | Versión | Uso | Artifacto |
|---|---|---|---|
| Jetpack Compose BOM | 2024.09.00 | Framework declarativo de UI | `androidx.compose:compose-bom` |
| Compose UI | (vía BOM) | Componentes base de Compose | `androidx.compose.ui:ui` |
| Compose Material 3 | (vía BOM) | Componentes de diseño Material You | `androidx.compose.material3:material3` |
| Material Icons Extended | (vía BOM) | Biblioteca completa de iconos | `androidx.compose.material:material-icons-extended` |
| Navigation Compose | 2.8.3 | Navegación entre pantallas | `androidx.navigation:navigation-compose` |
| Lifecycle ViewModel Compose | 2.8.6 | Integración ViewModel con Compose | `androidx.lifecycle:lifecycle-viewmodel-compose` |
| Hilt Navigation Compose | 1.3.0 | `hiltViewModel()` en Compose | `androidx.hilt:hilt-navigation-compose` |
| Lottie Compose | 6.4.0 | Animaciones vectoriales (JSON) | `com.airbnb.android:lottie-compose` |
| Coil Compose | 2.6.0 | Carga asíncrona de imágenes | `io.coil-kt:coil-compose` |
| MPAndroidChart | v3.1.0 | Gráficos de barras/líneas/torta para reportes | `com.github.PhilJay:MPAndroidChart` |
| Core SplashScreen | 1.0.1 | Pantalla de splash nativa API 31+ | `androidx.core:core-splashscreen` |

### Capa de Dominio y Negocio

Lógica de negocio, casos de uso y gestión de estado.

| Librería | Versión | Uso | Artifacto |
|---|---|---|---|
| Hilt Android | 2.59.2 | Inyección de dependencias (DI) | `com.google.dagger:hilt-android` |
| Hilt Android Compiler | 2.59.2 | Procesador de anotaciones Hilt | `com.google.dagger:hilt-android-compiler` |
| Lifecycle Runtime KTX | 2.6.1 | `lifecycleScope`, `repeatOnLifecycle` | `androidx.lifecycle:lifecycle-runtime-ktx` |
| Activity Compose | 1.8.0 | `ComponentActivity` con Compose | `androidx.activity:activity-compose` |
| Core KTX | 1.10.1 | Extensiones Kotlin para Android | `androidx.core:core-ktx` |
| Kotlinx Serialization | 1.6.3 | Serialización JSON nativa de Kotlin | `org.jetbrains.kotlinx:kotlinx-serialization-json` |
| Gson | 2.10.1 | Conversión JSON para Retrofit | `com.google.code.gson:gson` |

### Capa de Datos

Acceso a datos locales y remotos.

| Librería | Versión | Uso | Artifacto |
|---|---|---|---|
| Room Runtime | 2.7.0-alpha11 | ORM para SQLite local | `androidx.room:room-runtime` |
| Room KTX | 2.7.0-alpha11 | Coroutines y Flow para Room | `androidx.room:room-ktx` |
| Room Compiler (KSP) | 2.7.0-alpha11 | Generador de código Room | `androidx.room:room-compiler` |
| Retrofit | 2.9.0 | Cliente HTTP REST | `com.squareup.retrofit2:retrofit` |
| Retrofit Gson Converter | 2.9.0 | Deserialización JSON en Retrofit | `com.squareup.retrofit2:converter-gson` |
| OkHttp | 4.12.0 | Cliente HTTP base + interceptores | `com.squareup.okhttp3:okhttp` |
| OkHttp Logging Interceptor | 4.12.0 | Logging de requests/responses HTTP | `com.squareup.okhttp3:logging-interceptor` |
| DataStore Preferences | 1.0.0 | Almacenamiento clave-valor async | `androidx.datastore:datastore-preferences` |
| WorkManager KTX | 2.9.1 | Sincronización en background | `androidx.work:work-runtime-ktx` |
| Hilt Work | 1.3.0 | Inyección de dependencias en Workers | `androidx.hilt:hilt-work` |

### Seguridad y Notificaciones

| Librería | Versión | Uso | Artifacto |
|---|---|---|---|
| AndroidX Biometric | 1.2.0-alpha05 | Autenticación biométrica (huella dactilar) | `androidx.biometric:biometric` |
| Security Crypto | 1.1.0-alpha06 | Cifrado de datos en SharedPreferences | `androidx.security:security-crypto` |
| Firebase BOM | 33.1.0 | Gestión centralizada de versiones Firebase | `com.google.firebase:firebase-bom` |
| Firebase Cloud Messaging | (vía BOM) | Notificaciones push | `com.google.firebase:firebase-messaging-ktx` |
| AppCompat | 1.7.0 | Compatibilidad con versiones anteriores | `androidx.appcompat:appcompat` |

### Herramientas de Testing

| Librería | Versión | Uso | Artifacto |
|---|---|---|---|
| JUnit 4 | 4.13.2 | Framework base de pruebas unitarias | `junit:junit` |
| MockK | 1.13.12 | Mocking de dependencias en Kotlin | `io.mockk:mockk` |
| kotlinx-coroutines-test | 1.9.0 | Pruebas de ViewModels con coroutines | `org.jetbrains.kotlinx:kotlinx-coroutines-test` |
| Turbine | 1.2.0 | Testing de Flow y StateFlow | `app.cash.turbine:turbine` |
| AndroidX JUnit | 1.1.5 | JUnit para pruebas instrumentadas | `androidx.test.ext:junit` |
| Espresso Core | 3.5.1 | UI testing instrumentado | `androidx.test.espresso:espresso-core` |

### Plugins de Gradle

| Plugin | Versión | Uso |
|---|---|---|
| Android Application | 9.2.1 | Plugin base de aplicación Android |
| Kotlin Compose | 2.2.10 | Compilador de Compose |
| Hilt | 2.59.2 | Plugin de inyección de dependencias |
| Kotlin Serialization | 2.2.10 | Plugin de serialización Kotlin |
| KSP | 2.3.8 | Kotlin Symbol Processing (Room, Hilt) |

---

## Arquitectura

El proyecto sigue **Clean Architecture** con el patrón **MVVM**:

```
┌─────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN            │
│  Compose Screens + ViewModels (MVVM)    │
│  StateFlow → collectAsState             │
├─────────────────────────────────────────┤
│           CAPA DE DOMINIO               │
│  Modelos de negocio                     │
│  Interfaces de Repositorio              │
│  Use Cases                              │
├─────────────────────────────────────────┤
│           CAPA DE DATOS                 │
│  Room (offline) + Retrofit (online)     │
│  Implementaciones de Repositorios       │
│  WorkManager (sync en background)       │
└─────────────────────────────────────────┘
```

**Patrones aplicados:**
- **Repository Pattern** — desacopla la fuente de datos de la lógica de negocio
- **Dependency Injection (Hilt)** — inversión de control en todos los módulos
- **Use Cases** — encapsulan una operación de negocio por clase
- **StateFlow + collectAsState** — estado reactivo unidireccional (UDF)
- **Offline-First** — Room como fuente de verdad, sincronización diferida

---

## Estructura de paquetes

```
com.example.swo/
├── core/
│   ├── di/              # Módulos Hilt: DatabaseModule, NetworkModule, RepositoryModule
│   ├── designsystem/    # Componentes UI reutilizables (SWOCard, etc.)
│   ├── notifications/   # FirebaseMessagingService
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
│   ├── model/           # Modelos: Incident, User, Project, Comentario, Notificacion
│   ├── projects/        # ProjectRepository (interfaz)
│   └── users/           # UserRepository (interfaz)
│
└── ui/
    ├── chatbot/         # ChatbotScreen + ChatViewModel
    ├── components/      # Componentes compartidos: Shimmer, Avatar, Glow effects
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

## Inicio rápido

```bash
# 1. Clonar el repositorio
git clone https://github.com/JuanPGE123/SWO-MOVIL.git
cd SWO-MOVIL

# 2. Abrir en Android Studio: File → Open → carpeta SWO-MOVIL

# 3. Compilar e instalar en emulador (variante debug)
./gradlew :app:installDebug

# 4. Ejecutar todas las pruebas unitarias
./gradlew :app:testDebugUnitTest
```

**Credenciales de prueba (precargadas en BD local):**

| Correo | Contraseña | Rol |
|---|---|---|
| admin@swo.com | admin123 | Administrador |
| mfernanda@swo.com | tech123 | Técnico |
| carlos@swo.com | client123 | Cliente |

---

## Módulos implementados

| Módulo | Pantallas | ViewModel | Pruebas unitarias |
|---|---|---|---|
| Autenticación | LoginScreen | LoginViewModel | ✅ LoginViewModelTest (7) |
| Dashboard | DashboardScreen, SettingsScreen | DashboardViewModel | ✅ DashboardViewModelTest (5) |
| Incidencias | IncidentsScreen, IncidentDetailScreen, IncidentFormScreen | IncidentFormViewModel | ✅ IncidentsViewModelTest (5) |
| Proyectos | ProjectsScreen, ProjectFormScreen | ProjectViewModel | — |
| Usuarios | UsersScreen, UserFormScreen | UserViewModel | — |
| Reportes | ReportsScreen | ReportsViewModel | ✅ ReportsViewModelTest (8) |
| ChatBot | ChatbotScreen | ChatViewModel | — |
| Validaciones | (transversal) | — | ✅ FormValidatorTest (19) |
| Comentarios | (dominio) | — | ✅ ComentarioTest (4) |
| Notificaciones | (dominio) | — | ✅ NotificacionTest (7) |

**Total pruebas unitarias: 55**

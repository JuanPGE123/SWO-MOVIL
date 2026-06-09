# Desarrollar módulos móvil según requerimientos del proyecto

**Evidencia:** GA8-220501096-AA2-EV02  
**Programa:** Análisis y Desarrollo de Software  
**Proyecto:** SWO — Sistema de Gestión de Incidencias (Service Desk)  
**Repositorio:** https://github.com/JuanPGE123/SWO-MOVIL.git  
**Aprendiz:** Juan Pablo Giraldo E.  
**Fecha:** Junio 2026

---

## Tabla de contenido

1. [Introducción](#introducción)
2. [Objetivos](#objetivos)
3. [Marco tecnológico](#marco-tecnológico)
4. [Módulos desarrollados](#módulos-desarrollados)
5. [Arquitectura implementada](#arquitectura-implementada)
6. [Gestión del repositorio](#gestión-del-repositorio)
7. [Pruebas unitarias](#pruebas-unitarias)
8. [Configuración de ambientes](#configuración-de-ambientes)
9. [Componentes reutilizables y patrones aplicados](#componentes-reutilizables-y-patrones-aplicados)
10. [Conclusiones](#conclusiones)
11. [Referencias](#referencias)

---

## Introducción

El presente documento corresponde a la evidencia **GA8-220501096-AA2-EV02** de la actividad de aprendizaje *Desarrollar módulos móvil según requerimientos del proyecto*, perteneciente a la Guía 8 del programa de formación **Análisis y Desarrollo de Software** del SENA.

El proyecto **SWO Mobile** es una aplicación Android nativa desarrollada en **Kotlin** con **Jetpack Compose** que implementa el módulo móvil del Sistema de Gestión de Incidencias SWO (Service Desk). La aplicación permite a usuarios de tres roles distintos — Administrador, Técnico y Cliente — registrar, consultar, gestionar y hacer seguimiento a incidencias de soporte técnico desde dispositivos móviles Android.

El desarrollo se llevó a cabo siguiendo los principios de **Clean Architecture** con el patrón **MVVM (Model-View-ViewModel)**, garantizando separación de responsabilidades, mantenibilidad y testabilidad del código. Se aplicaron buenas prácticas de ingeniería de software como inyección de dependencias, patrones de diseño, programación reactiva y pruebas unitarias automatizadas.

La aplicación está diseñada con una estrategia **offline-first**: los datos se almacenan localmente en una base de datos SQLite mediante Room, y se sincronizan con el servidor backend cuando hay conectividad disponible. Esto garantiza que la aplicación sea funcional incluso sin conexión a internet.

---

## Objetivos

### Objetivo General

Desarrollar los módulos de la aplicación móvil Android del Sistema de Gestión de Incidencias SWO, cumpliendo con los requerimientos funcionales, técnicos y de calidad establecidos en el proyecto formativo del SENA.

### Objetivos Específicos

1. **Implementar los módulos funcionales** de la aplicación móvil: Dashboard, Incidencias, Proyectos, Usuarios, Reportes y ChatBot, siguiendo las historias de usuario y requerimientos del proyecto.

2. **Aplicar Clean Architecture con MVVM** para garantizar una separación clara de responsabilidades entre las capas de presentación, dominio y datos.

3. **Configurar el sistema de control de versiones** con Git, estableciendo un repositorio en GitHub con las convenciones de commits adecuadas y un `.gitignore` completo para proyectos Android.

4. **Documentar las librerías y frameworks** utilizados en cada capa del proyecto, justificando su elección y especificando las versiones empleadas.

5. **Documentar y configurar los ambientes** de desarrollo y pruebas, incluyendo la configuración del servidor backend por variante de build (debug/release).

6. **Diseñar e implementar pruebas unitarias** para cada módulo principal de la aplicación, alcanzando cobertura en las capas de dominio y presentación.

7. **Implementar componentes de UI reutilizables** y un sistema de diseño coherente basado en Material Design 3.

---

## Marco tecnológico

La selección de tecnologías se basó en las recomendaciones oficiales de Google para el desarrollo Android moderno (Android Jetpack), los estándares del sector y la compatibilidad con el ecosistema Kotlin.

### Lenguaje y plataforma

| Componente | Tecnología | Versión | Justificación |
|---|---|---|---|
| Lenguaje principal | Kotlin | 2.2.10 | Lenguaje oficial de Android desde 2019; null-safety, coroutines y expresividad |
| SDK mínimo | Android 7.0 | API 24 | Cubre más del 94% de dispositivos Android activos en Colombia |
| SDK objetivo | Android 15 | API 35 | Última versión estable de Android con soporte completo a Material 3 |
| Build system | Gradle | 9.2.1 | Sistema de construcción estándar del ecosistema Android |

### Capa de Presentación

| Librería | Versión | Justificación de uso |
|---|---|---|
| Jetpack Compose BOM | 2024.09.00 | Framework declarativo de UI recomendado por Google; elimina la necesidad de XML |
| Material 3 | (vía BOM) | Implementa el sistema de diseño Material You con soporte a temas dinámicos |
| Navigation Compose | 2.8.3 | Navegación type-safe entre pantallas con soporte a deep links y back stack |
| Lifecycle ViewModel Compose | 2.8.6 | Integra ViewModels con el ciclo de vida de Compose de forma segura |
| Hilt Navigation Compose | 1.3.0 | Provee ViewModels inyectados por Hilt directamente en composables |
| Lottie Compose | 6.4.0 | Reproducción de animaciones vectoriales JSON para mejor UX |
| Coil Compose | 2.6.0 | Carga de imágenes asíncrona optimizada para Compose |
| MPAndroidChart | v3.1.0 | Gráficos estadísticos (barras, torta, líneas) para el módulo de Reportes |

### Capa de Dominio y Negocio

| Librería | Versión | Justificación de uso |
|---|---|---|
| Hilt Android | 2.59.2 | Inyección de dependencias estándar en Android; reduce acoplamiento |
| Coroutines + Flow | (Kotlin stdlib) | Programación asíncrona reactiva sin callbacks anidados |
| KSP | 2.3.8 | Procesador de símbolos Kotlin más rápido que KAPT para Room y Hilt |

### Capa de Datos

| Librería | Versión | Justificación de uso |
|---|---|---|
| Room | 2.7.0 | ORM oficial de Android; abstrae SQLite con seguridad de tipos en tiempo de compilación |
| Retrofit | 2.9.0 | Cliente HTTP REST más utilizado en Android; integración nativa con Kotlin |
| OkHttp | 4.12.0 | Cliente HTTP base con interceptores para logging y autenticación |
| DataStore Preferences | 1.0.0 | Reemplaza SharedPreferences con soporte a coroutines |
| WorkManager | 2.9.1 | Sincronización en background con garantías de ejecución |

### Seguridad

| Librería | Versión | Justificación de uso |
|---|---|---|
| AndroidX Biometric | 1.2.0 | Autenticación por huella dactilar compatible con API 24+ |
| Security Crypto | 1.1.0 | Cifrado de datos sensibles en almacenamiento local |
| Firebase Cloud Messaging | (BOM 33.1) | Notificaciones push desde el servidor backend |

### Testing

| Librería | Versión | Justificación de uso |
|---|---|---|
| JUnit 4 | 4.13.2 | Framework de pruebas estándar en el ecosistema Java/Kotlin |
| MockK | 1.13.12 | Librería de mocking idiomática para Kotlin; soporte a suspend functions |
| kotlinx-coroutines-test | 1.9.0 | Control de dispatchers y tiempo en pruebas de coroutines |
| Turbine | 1.2.0 | Verificación de emisiones de Flow y StateFlow con API fluida |

---

## Módulos desarrollados

### 1. Módulo de Autenticación

Gestiona el proceso de inicio de sesión con validación de credenciales, normalización de correo electrónico y manejo de estados de carga y error.

**Componentes:**
- `LoginScreen` — Pantalla de login con campos validados en tiempo real
- `LoginViewModel` — Lógica de autenticación con `UserRepository`
- `FormValidator` — Validaciones reutilizables (correo, contraseña, nombre)

**Funcionalidades:**
- Validación de campos vacíos antes de consultar el repositorio
- Normalización del correo (trim + lowercase) para evitar duplicados
- Manejo de cuenta inactiva con mensaje informativo
- Estado reactivo con `StateFlow` para UI sin parpadeos

---

### 2. Módulo de Dashboard

Panel principal de la aplicación que muestra estadísticas en tiempo real del sistema de incidencias.

**Componentes:**
- `DashboardScreen` — Pantalla con cards de estadísticas y lista de incidencias recientes
- `DashboardViewModel` — Combina 7 flows de DAOs con `combine()`
- `SettingsScreen` — Configuraciones de la cuenta y la aplicación

**Funcionalidades:**
- Conteo de incidencias por estado: Total, Abiertas, En progreso, Resueltas
- Conteo de proyectos activos y usuarios registrados
- Lista de las 5 incidencias activas más recientes
- Actualización reactiva: la UI se actualiza automáticamente cuando cambian los datos en Room

---

### 3. Módulo de Incidencias

Módulo central de la aplicación para la gestión completa del ciclo de vida de una incidencia.

**Componentes:**
- `IncidentsScreen` — Listado de incidencias con filtros por estado
- `IncidentDetailScreen` — Detalle completo de una incidencia con comentarios
- `IncidentFormScreen` — Formulario de creación/edición de incidencia
- `IncidentsViewModel` — Carga de incidencias con `GetIncidentsUseCase`
- `IncidentFormViewModel` — Gestión del formulario con validación

**Funcionalidades:**
- Listado con filtrado por estado (OPEN, IN_PROGRESS, RESOLVED, CANCELLED)
- Formulario con selección de proyecto, categoría, prioridad y descripción
- Estados de incidencia: Abierta → En progreso → Resuelta / Cancelada
- Integración con `Comentario` para trazabilidad de la incidencia

**Modelo de dominio `Incident`:**
```
Incident(id, title, description, status, priority, reportedBy, projectName, createdAt)
```

---

### 4. Módulo de Proyectos

Gestión de los proyectos a los que se asocian las incidencias.

**Componentes:**
- `ProjectsScreen` — Listado de proyectos activos
- `ProjectFormScreen` — Formulario de creación/edición de proyecto
- `ProjectViewModel` — Lógica de negocio con `ProjectRepository`

**Funcionalidades:**
- Creación y edición de proyectos con nombre, descripción y estado
- Validación con `FormValidator.validateProjectName()` y `validateDescription()`
- Integración con incidencias para filtrar por proyecto en reportes

---

### 5. Módulo de Usuarios

Administración de los usuarios del sistema, restringido al rol Administrador.

**Componentes:**
- `UsersScreen` — Listado de usuarios con rol y estado
- `UserFormScreen` — Formulario de creación/edición de usuario
- `UserViewModel` — Gestión con `UserRepository`

**Roles implementados:**
| Rol | Acceso |
|---|---|
| `ADMIN` | Acceso completo a todos los módulos |
| `TECHNICIAN` | Gestionar incidencias asignadas, ver reportes |
| `CLIENT` | Crear incidencias, ver sus propias incidencias |

---

### 6. Módulo de Reportes

Visualización estadística del estado del sistema mediante gráficos interactivos.

**Componentes:**
- `ReportsScreen` — Pantalla con gráficos de MPAndroidChart
- `ReportsViewModel` — Agrega datos de `IncidentDao` y `CategoryDao`

**Visualizaciones:**
- Gráfico de torta: distribución de incidencias por estado
- Gráfico de barras: incidencias por proyecto
- Gráfico de líneas: tendencia temporal
- Indicador de tasa de resolución: `(resueltas / total) × 100`
- Estadísticas por prioridad: Crítica, Alta, Media, Baja

---

### 7. Módulo de ChatBot

Asistente conversacional integrado con fallback local cuando no hay conectividad.

**Componentes:**
- `ChatbotScreen` — Interfaz de chat con burbujas de mensaje
- `ChatViewModel` — Gestión del historial y envío de mensajes
- `ChatbotApi` — Integración con el endpoint REST del backend
- `ChatMessageEntity` — Persistencia local del historial

**Funcionalidades:**
- Envío de mensajes al backend con respuesta contextual
- Fallback local: respuestas predefinidas cuando no hay red
- Historial persistido en Room entre sesiones

---

### 8. Módulo de Comentarios (Dominio)

Modelo de dominio para la trazabilidad de incidencias mediante comentarios.

**Modelo:**
```kotlin
data class Comentario(
    val idComentario: String,
    val texto: String,
    val fecha: Long,
    val idIncidencia: String,
    val idUsuario: String,
    val nombreUsuario: String,
    val esPublico: Boolean = true   // false = nota interna del técnico
)
```

**DAO disponible:** `getByIncidencia`, `getPublicosByIncidencia`, `insert`, `delete`, `countByIncidencia`

---

### 9. Módulo de Notificaciones (Dominio)

Sistema de notificaciones internas persistidas localmente y recibidas vía Firebase Cloud Messaging.

**Tipos de notificación:**

| Tipo | Descripción |
|---|---|
| `INCIDENCIA_ASIGNADA` | Una incidencia fue asignada al técnico |
| `ESTADO_ACTUALIZADO` | El estado de una incidencia cambió |
| `COMENTARIO_NUEVO` | Se agregó un comentario a una incidencia |
| `GENERAL` | Notificación general del sistema |

**DAO disponible:** `getByUsuario`, `getNoLeidas`, `countNoLeidas` (badge), `marcarLeida`, `marcarTodasLeidas`, `deleteLeidas`

---

## Arquitectura implementada

### Clean Architecture en capas

```
┌─────────────────────────────────────────────────────────────┐
│                 CAPA DE PRESENTACIÓN (UI)                   │
│                                                             │
│  Compose Screens  ←→  ViewModels  ←→  StateFlow/Flow        │
│  (Stateless)           (State)        (Reactive stream)     │
├─────────────────────────────────────────────────────────────┤
│                   CAPA DE DOMINIO                           │
│                                                             │
│  Modelos de negocio (Incident, User, Project, ...)          │
│  Interfaces de Repositorio (abstracción)                    │
│  Use Cases (GetIncidentsUseCase, ...)                       │
├─────────────────────────────────────────────────────────────┤
│                    CAPA DE DATOS                            │
│                                                             │
│  ┌─────────────────────┐   ┌──────────────────────────┐    │
│  │   Room (SQLite)     │   │   Retrofit (REST API)    │    │
│  │   Fuente principal  │   │   Sincronización online  │    │
│  │   (offline-first)   │   │   BuildConfig.BASE_URL   │    │
│  └─────────────────────┘   └──────────────────────────┘    │
│           ↑                             ↑                   │
│  Implementaciones de repositorio (fusionan las fuentes)     │
└─────────────────────────────────────────────────────────────┘
```

### Patrón MVVM con StateFlow

```
Screen (Composable)
  │  collectAsState()
  ▼
ViewModel
  │  emite UiState
  ▼
StateFlow<UiState>
  │
Repository (interfaz de dominio)
  │
  ├── Room DAO (offline)
  └── Retrofit API (online)
```

### Patrones de diseño aplicados

| Patrón | Implementación |
|---|---|
| **Repository Pattern** | Cada módulo tiene su interfaz de repositorio en `domain/` e implementación en `data/` |
| **Dependency Injection** | Hilt gestiona el grafo de dependencias; `@Singleton` para DAOs, Retrofit y repositorios |
| **Use Case Pattern** | `GetIncidentsUseCase` encapsula la lógica de obtener incidencias del repositorio |
| **Observer / Reactive** | `Flow` y `StateFlow` propagan cambios de Room a la UI sin polling |
| **Factory** | Hilt genera factories para ViewModels, Workers y repositorios |
| **Strategy** | `FormValidator` aplica estrategias de validación intercambiables por campo |

---

## Gestión del repositorio

### Configuración de Git

```bash
# Repositorio inicializado en: d:/MOVIL/SWO
git init
git config user.email "juanpablogiraldoe@gmail.com"
git config user.name "JuanPGE123"
```

### .gitignore

Se configuró un `.gitignore` completo para proyectos Android que excluye:

| Categoría | Archivos/carpetas excluidos |
|---|---|
| Build | `/build/`, `app/build/`, `**/build/` |
| Gradle | `.gradle/`, `gradle-wrapper.jar` |
| IDE | `.idea/`, `*.iml`, `*.iws`, `*.ipr` |
| Secrets | `local.properties`, `*.jks`, `*.keystore`, `keystore.properties` |
| SO | `.DS_Store`, `Thumbs.db` |
| Herramientas | `.artifacts/`, `.gemini/`, `.claude/` |
| Compilados | `*.class`, `*.jar`, `*.aar` |

### Historial de commits

| Hash | Mensaje | Descripción |
|---|---|---|
| `a829459` | `feat: app Android SWO Service Desk - implementación completa` | Commit inicial con 166 archivos y 11,996 líneas de código |
| `b2dd87c` | `docs: agregar documentación técnica completa en carpeta docs/` | README actualizado + 3 archivos de documentación en `docs/` |

### Convención de mensajes de commit

Se siguió el estándar **Conventional Commits**:

```
<tipo>: <descripción en imperativo>

Cuerpo opcional con detalle

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
```

| Tipo | Uso |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de bugs |
| `docs` | Cambios en documentación |
| `refactor` | Refactorización sin cambio de funcionalidad |
| `test` | Adición o modificación de pruebas |

---

## Pruebas unitarias

Se implementaron **55 pruebas unitarias** distribuidas en 7 clases, cubriendo las capas de dominio y presentación. Todas son pruebas JVM (no requieren emulador).

### Resumen por módulo

| Clase | Módulo | Pruebas | Tipo |
|---|---|---|---|
| `FormValidatorTest` | Core / Validaciones | 19 | Lógica pura JVM |
| `LoginViewModelTest` | Autenticación | 7 | ViewModel + MockK |
| `IncidentsViewModelTest` | Incidencias | 5 | ViewModel + UseCase + Mock |
| `DashboardViewModelTest` | Dashboard | 5 | ViewModel + DAO Mock |
| `ReportsViewModelTest` | Reportes | 8 | ViewModel + DAO Mock |
| `ComentarioTest` | Dominio Comentarios | 4 | Mapeo de datos |
| `NotificacionTest` | Dominio Notificaciones | 7 | Mapeo + Enum |
| **Total** | | **55** | |

### Estrategia de pruebas aplicada

**Pruebas puras JVM** (FormValidator, Comentario, Notificacion):
- Sin dependencias externas ni mocks
- Prueban lógica de validación y transformación de datos
- Ejecución instantánea (< 1 segundo para el conjunto completo)

**Pruebas de ViewModel con MockK**:
- Los repositorios e interfaces se mockean con `mockk(relaxed = true)`
- `MainDispatcherRule` reemplaza `Dispatchers.Main` con `UnconfinedTestDispatcher`
- `runTest` ejecuta coroutines de forma determinista
- `Turbine` verifica emisiones de `StateFlow`

**Verificaciones clave implementadas:**

```kotlin
// Verificar que el repositorio NO se llama con campos vacíos
coVerify(exactly = 0) { userRepository.getUserByCredentials(any(), any()) }

// Verificar normalización de correo
coVerify(exactly = 1) {
    userRepository.getUserByCredentials("ana@sena.edu.co", "password123")
}

// Verificar reactividad del StateFlow
totalFlow.value = 12
advanceUntilIdle()
assertEquals(12, viewModel.stats.value.totalIncidents)
```

### Cómo ejecutar

```bash
./gradlew :app:testDebugUnitTest
# Reporte: app/build/reports/tests/testDebugUnitTest/index.html
```

---

## Configuración de ambientes

### Ambientes configurados con BuildConfig

La URL del servidor backend no está hardcodeada. Se configura por variante de build en `app/build.gradle.kts`:

```kotlin
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

| Variable | Debug | Release |
|---|---|---|
| `BASE_URL` | `http://10.0.2.2:8080/api/v1/` (emulador → localhost PC) | `https://api.swo-servicedesk.com/v1/` |
| `APPLICATION_ID` | `com.example.swo.debug` | `com.example.swo` |
| `VERSION_NAME` | `1.0-debug` | `1.0` |
| `DEBUG` | `true` | `false` |

### Ambiente de desarrollo (resumen)

| Herramienta | Versión |
|---|---|
| Android Studio Ladybug | 2024.2.1 |
| JDK | 17 |
| Android SDK | API 35 |
| Gradle | 9.2.1 |
| Git | 2.44+ |

### Usuarios de prueba (DatabaseSeeder)

La base de datos se inicializa automáticamente al primer arranque con usuarios de prueba, sin necesidad del backend activo.

| Correo | Contraseña | Rol |
|---|---|---|
| admin@swo.com | admin123 | Administrador |
| mfernanda@swo.com | tech123 | Técnico |
| carlos@swo.com | client123 | Cliente |

### Base de datos local (Room)

| Versión | Tablas incluidas |
|---|---|
| v5 (actual) | `incidents`, `users`, `projects`, `categories`, `reports`, `chat_messages`, `comentarios`, `notificaciones` |

---

## Componentes reutilizables y patrones aplicados

### Sistema de diseño (`core/designsystem/`)

Se implementaron componentes visuales reutilizables basados en Material 3:

| Componente | Descripción |
|---|---|
| `SWOCard` | Card genérica con elevación y esquinas redondeadas para todo el sistema |
| `ShimmerEffect` | Efecto de carga esqueleto mientras se obtienen los datos |
| `AvatarImage` | Avatar de usuario con iniciales como fallback si no hay imagen |
| `GlowEffect` | Efecto de resplandor para indicadores de estado crítico |

### FormValidator (`core/utils/`)

Clase utilitaria con métodos de validación reutilizables en todos los formularios:

```kotlin
object FormValidator {
    fun validateName(value: String): String?        // null = válido
    fun validateEmail(value: String): String?
    fun validatePassword(value: String, isEditing: Boolean = false): String?
    fun validateProjectName(value: String): String?
    fun validateDescription(value: String): String?
    fun passwordStrength(password: String): PasswordStrength  // WEAK | MEDIUM | STRONG
}
```

### Seguridad (`core/security/`)

| Clase | Responsabilidad |
|---|---|
| `BiometricAuthManager` | Lanza el prompt biométrico y gestiona el resultado |
| `SecurityManager` | Cifra/descifra datos sensibles usando `EncryptedSharedPreferences` |

### Sincronización (`core/sync/`)

`SyncWorker` extiende `CoroutineWorker` y se programa con `WorkManager` para sincronizar incidencias y proyectos con el backend cuando hay conectividad, con reintento automático en caso de fallo.

### Navegación (`ui/navigation/`)

```kotlin
sealed class Screen(val route: String) {
    object Login         : Screen("login")
    object Dashboard     : Screen("dashboard")
    object Incidents     : Screen("incidents")
    object IncidentForm  : Screen("incident_form/{id}")
    object Projects      : Screen("projects")
    object Users         : Screen("users")
    object Reports       : Screen("reports")
    object Chatbot       : Screen("chatbot")
    object Settings      : Screen("settings")
}
```

El `NavGraph` gestiona el flujo de navegación completo, pasando el usuario autenticado entre pantallas.

---

## Conclusiones

**1. La arquitectura Clean + MVVM demostró ser la decisión correcta para este tipo de proyecto.**  
La separación en tres capas (presentación, dominio, datos) permitió que los módulos fueran independientes entre sí: las pruebas unitarias del `LoginViewModel` no necesitaron conocer los detalles de Room ni de Retrofit, solo la interfaz del repositorio. Esto redujo el tiempo de prueba y facilitó los cambios en la capa de datos sin afectar la lógica de negocio.

**2. Jetpack Compose aceleró el desarrollo de la UI sin sacrificar calidad.**  
El modelo declarativo de Compose, combinado con `StateFlow` y `collectAsState()`, eliminó la necesidad de actualizar la UI manualmente ante cambios de estado. Los componentes reutilizables del sistema de diseño (`SWOCard`, `ShimmerEffect`, `AvatarImage`) garantizaron consistencia visual en todos los módulos con mínimo esfuerzo de mantenimiento.

**3. La estrategia offline-first con Room como fuente de verdad es fundamental para el contexto colombiano.**  
Al persistir todos los datos localmente y sincronizar en segundo plano con WorkManager, la aplicación es completamente funcional sin internet. El `DatabaseSeeder` permite demostrar todas las funcionalidades en cualquier entorno sin necesidad de infraestructura de backend activa, lo que facilita la evaluación y demostración del proyecto.

**4. La configuración por ambientes con BuildConfig resolvió el problema de la URL hardcodeada.**  
Separar la URL del backend por variante de build (`debug` vs `release`) siguiendo el principio de 12-factor app eliminó un vector de error común: desplegar a producción con una URL de desarrollo. La solución es limpia, no requiere archivos de configuración externos y es transparente para el desarrollador.

**5. Las 55 pruebas unitarias aportan confianza en la lógica de negocio.**  
La combinación de MockK para simular dependencias externas y `UnconfinedTestDispatcher` para controlar coroutines permitió probar ViewModels complejos como `DashboardViewModel` (que combina 7 flows en tiempo real) y `ReportsViewModel` (que calcula tasas de resolución) sin necesidad de un emulador. Las pruebas documentan el comportamiento esperado del sistema y actúan como red de seguridad ante refactorizaciones futuras.

**6. La documentación estructurada es parte integral del producto de software.**  
La generación de documentación técnica organizada en archivos Markdown separados por preocupación (`pruebas-unitarias.md`, `configuracion-servidor-datos.md`, `ambientes.md`) facilita el onboarding de nuevos desarrolladores y cumple con los estándares de entregables del proyecto formativo del SENA.

---

## Referencias

- Android Developers — Jetpack Compose: https://developer.android.com/develop/ui/compose
- Android Developers — Architecture Guide: https://developer.android.com/topic/architecture
- Android Developers — Room: https://developer.android.com/training/data-storage/room
- Android Developers — WorkManager: https://developer.android.com/topic/libraries/architecture/workmanager
- Hilt — Dependency Injection: https://dagger.dev/hilt/
- Retrofit — Square: https://square.github.io/retrofit/
- MockK — Mocking library for Kotlin: https://mockk.io/
- Turbine — Testing Flow: https://github.com/cashapp/turbine
- Conventional Commits: https://www.conventionalcommits.org/es/v1.0.0/
- Material Design 3: https://m3.material.io/

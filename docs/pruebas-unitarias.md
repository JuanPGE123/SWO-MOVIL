# Pruebas Unitarias — SWO Mobile

Documentación completa de las pruebas unitarias implementadas por módulo. Todas las pruebas son de tipo JVM (no requieren emulador ni dispositivo físico).

---

## Índice

1. [Infraestructura de pruebas](#infraestructura-de-pruebas)
2. [Módulo Core — FormValidator](#módulo-core--formvalidator)
3. [Módulo Autenticación — LoginViewModel](#módulo-autenticación--loginviewmodel)
4. [Módulo Incidencias — IncidentsViewModel](#módulo-incidencias--incidentsviewmodel)
5. [Módulo Dashboard — DashboardViewModel](#módulo-dashboard--dashboardviewmodel)
6. [Módulo Reportes — ReportsViewModel](#módulo-reportes--reportsviewmodel)
7. [Módulo Dominio — Comentario](#módulo-dominio--comentario)
8. [Módulo Dominio — Notificacion](#módulo-dominio--notificacion)
9. [Cómo ejecutar las pruebas](#cómo-ejecutar-las-pruebas)
10. [Resumen de cobertura](#resumen-de-cobertura)

---

## Infraestructura de pruebas

### Dependencias

```kotlin
// app/build.gradle.kts
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.12")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
testImplementation("app.cash.turbine:turbine:1.2.0")
```

| Librería | Versión | Propósito |
|---|---|---|
| JUnit 4 | 4.13.2 | Framework base de pruebas |
| MockK | 1.13.12 | Mocking de dependencias en Kotlin |
| kotlinx-coroutines-test | 1.9.0 | Pruebas de coroutines y ViewModels |
| Turbine | 1.2.0 | Verificación de emisiones de Flow/StateFlow |

### MainDispatcherRule

Archivo: `app/src/test/java/com/example/swo/util/MainDispatcherRule.kt`

JUnit Rule que reemplaza `Dispatchers.Main` por `UnconfinedTestDispatcher` durante las pruebas. Es obligatorio en cualquier prueba que instancie un ViewModel que use `viewModelScope`.

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) { Dispatchers.setMain(testDispatcher) }
    override fun finished(description: Description) { Dispatchers.resetMain() }
}
```

**Uso en pruebas:**
```kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()
```

---

## Módulo Core — FormValidator

**Archivo:** `app/src/test/java/com/example/swo/core/utils/FormValidatorTest.kt`  
**Tipo:** Pruebas puras JVM (sin mocks, sin coroutines)  
**Total:** 19 pruebas

### validateName (5 pruebas)

| # | Nombre del test | Descripción | Resultado esperado |
|---|---|---|---|
| 1 | `validateName con nombre vacío retorna error` | Cadena vacía `""` | `"El nombre es obligatorio"` |
| 2 | `validateName con nombre en blanco retorna error` | Cadena con espacios `"   "` | `"El nombre es obligatorio"` |
| 3 | `validateName con menos de 3 caracteres retorna error` | `"Ab"` (2 chars) | `"Mínimo 3 caracteres"` |
| 4 | `validateName con más de 100 caracteres retorna error` | 101 caracteres | `"Máximo 100 caracteres"` |
| 5 | `validateName con nombre válido retorna null` | `"Juan Pablo"`, `"Ana"`, 100 chars | `null` (sin error) |

### validateEmail (4 pruebas)

| # | Nombre del test | Descripción | Resultado esperado |
|---|---|---|---|
| 6 | `validateEmail con correo vacío retorna error` | `""` | `"El correo es obligatorio"` |
| 7 | `validateEmail con correo muy largo retorna error` | 151 caracteres | `"Correo demasiado largo"` |
| 8 | `validateEmail con formato inválido retorna error` | `"no-es-un-correo"`, `"usuario@"`, `"@dominio.com"` | Mensaje de formato inválido |
| 9 | `validateEmail con correo válido retorna null` | `"juan@sena.edu.co"`, con espacios (trim interno) | `null` |

### validatePassword (5 pruebas)

| # | Nombre del test | Descripción | Resultado esperado |
|---|---|---|---|
| 10 | `validatePassword con contraseña vacía y no editando retorna error` | `""`, `isEditing=false` | `"La contraseña es obligatoria"` |
| 11 | `validatePassword con contraseña vacía en modo edición retorna null` | `""`, `isEditing=true` | `null` (edición permite vacío) |
| 12 | `validatePassword con menos de 6 caracteres retorna error` | `"abc"` | `"Mínimo 6 caracteres"` |
| 13 | `validatePassword con más de 50 caracteres retorna error` | 51 caracteres | `"Máximo 50 caracteres"` |
| 14 | `validatePassword con contraseña válida retorna null` | `"segura123"` | `null` |

### validateProjectName (4 pruebas)

| # | Nombre del test | Descripción | Resultado esperado |
|---|---|---|---|
| 15 | `validateProjectName con nombre vacío retorna error` | `""` | `"El nombre del proyecto es obligatorio"` |
| 16 | `validateProjectName con menos de 3 caracteres retorna error` | `"AB"` | `"Mínimo 3 caracteres"` |
| 17 | `validateProjectName con más de 80 caracteres retorna error` | 81 caracteres | `"Máximo 80 caracteres"` |
| 18 | `validateProjectName con nombre válido retorna null` | `"SWO Proyecto"` | `null` |

### validateDescription y passwordStrength (2 + 4 pruebas)

| # | Nombre del test | Descripción | Resultado esperado |
|---|---|---|---|
| 19 | `validateDescription con más de 500 caracteres retorna error` | 501 chars | Mensaje de límite excedido |
| 20 | `validateDescription con descripción válida retorna null` | Vacía, normal, 500 chars | `null` |
| 21 | `passwordStrength con menos de 6 caracteres retorna WEAK` | `"abc"`, `""` | `PasswordStrength.WEAK` |
| 22 | `passwordStrength con solo letras minúsculas retorna WEAK` | `"abcdef"` | `PasswordStrength.WEAK` |
| 23 | `passwordStrength con longitud y dígito retorna MEDIUM` | `"abcdefg1"` (score 2) | `PasswordStrength.MEDIUM` |
| 24 | `passwordStrength con múltiples condiciones retorna STRONG` | `"Abcdef1!"` (score 4) | `PasswordStrength.STRONG` |

---

## Módulo Autenticación — LoginViewModel

**Archivo:** `app/src/test/java/com/example/swo/ui/LoginViewModelTest.kt`  
**Dependencias mockeadas:** `UserRepository`  
**Total:** 7 pruebas

### Configuración del test

```kotlin
private val userRepository: UserRepository = mockk(relaxed = true)
private lateinit var viewModel: LoginViewModel

@Before
fun setUp() {
    coEvery { userRepository.getUsers() } returns flowOf(emptyList())
    viewModel = LoginViewModel(userRepository)
}
```

### Pruebas

| # | Nombre del test | Escenario | Estado esperado |
|---|---|---|---|
| 1 | `login con correo vacío establece mensaje de error` | `email=""` | `error = "Ingresa tu correo y contraseña"`, `isLoading = false` |
| 2 | `login con contraseña vacía establece mensaje de error` | `password=""` | `error = "Ingresa tu correo y contraseña"`, `isLoading = false` |
| 3 | `login con campos vacíos no llama al repositorio` | `email=""`, `password=""` | `getUserByCredentials` no se llama (`verify exactly 0`) |
| 4 | `login con credenciales incorrectas establece error` | Repositorio retorna `null` | `error = "Correo o contraseña incorrectos"`, `loggedUser = null` |
| 5 | `login con usuario inactivo establece error de cuenta inactiva` | `isActive = false` | `error = "Tu cuenta está inactiva. Contacta al administrador."` |
| 6 | `login exitoso establece el usuario en el estado` | Credenciales correctas, `isActive = true` | `loggedUser = usuarioActivo`, callback ejecutado, `error = null` |
| 7 | `login normaliza el correo a minúsculas antes de consultar` | `"  ANA@SENA.EDU.CO  "` | Repositorio recibe `"ana@sena.edu.co"` (trim + lowercase) |

**Verificación adicional:**
```kotlin
// Prueba 3: El repositorio NO debe ser llamado si los campos están vacíos
coVerify(exactly = 0) { userRepository.getUserByCredentials(any(), any()) }

// Prueba 7: El repositorio SÍ debe ser llamado con el correo normalizado
coVerify(exactly = 1) {
    userRepository.getUserByCredentials("ana@sena.edu.co", "password123")
}
```

---

## Módulo Incidencias — IncidentsViewModel

**Archivo:** `app/src/test/java/com/example/swo/ui/IncidentsViewModelTest.kt`  
**Dependencias mockeadas:** `IncidentRepository`  
**Usa:** `GetIncidentsUseCase` real (no mockeado)  
**Total:** 5 pruebas

### Objeto de prueba

```kotlin
private fun incidenteDePrueba(id: String = "1") = Incident(
    id = id, title = "Incidencia $id", description = "Descripción de prueba",
    status = IncidentStatus.OPEN, priority = IncidentPriority.HIGH,
    reportedBy = "Analista", projectName = "SWO", createdAt = "2025-01-01"
)
```

### Pruebas

| # | Nombre del test | Escenario | Resultado esperado |
|---|---|---|---|
| 1 | `estado inicial tiene isLoading en true y lista vacía` | Flow que nunca emite | `incidents.isEmpty()`, `error = null` |
| 2 | `al cargar incidencias el estado refleja la lista recibida` | Flow emite lista de 3 | `incidents.size = 3`, IDs en orden, `isLoading = false` |
| 3 | `con lista vacía el estado tiene lista vacía y no está cargando` | Flow emite `emptyList()` | `incidents.isEmpty()`, `isLoading = false` |
| 4 | `al crear el ViewModel se llama al repositorio` | Inicialización del VM | `getIncidents()` llamado exactamente 1 vez |
| 5 | `las incidencias con estado OPEN se distinguen correctamente` | Lista mixta de estados | Filtrar por `OPEN` retorna exactamente 1 resultado |

**Verificación con Turbine:**
```kotlin
viewModel.state.test {
    val estado = awaitItem()
    assertEquals(3, estado.incidents.size)
    assertFalse(estado.isLoading)
    cancelAndIgnoreRemainingEvents()
}
```

---

## Módulo Dashboard — DashboardViewModel

**Archivo:** `app/src/test/java/com/example/swo/ui/DashboardViewModelTest.kt`  
**Dependencias mockeadas:** `IncidentDao`, `ProjectDao`, `UserDao`  
**Total:** 5 pruebas

### Función auxiliar de configuración

```kotlin
private fun configurarDaosMock(
    total: Int = 0, open: Int = 0, inProgress: Int = 0, resolved: Int = 0,
    recientes: List<IncidentEntity> = emptyList(),
    proyectos: Int = 0, usuarios: List<UserEntity> = emptyList()
) {
    every { incidentDao.getTotalCount() }            returns MutableStateFlow(total)
    every { incidentDao.getOpenCount() }             returns MutableStateFlow(open)
    every { incidentDao.getInProgressCount() }       returns MutableStateFlow(inProgress)
    every { incidentDao.getResolvedCount() }         returns MutableStateFlow(resolved)
    every { incidentDao.getRecentActiveIncidents() } returns MutableStateFlow(recientes)
    every { projectDao.getCount() }                  returns MutableStateFlow(proyectos)
    every { userDao.getAllUsers() }                   returns MutableStateFlow(usuarios)
}
```

### Pruebas

| # | Nombre del test | Escenario | Resultado esperado |
|---|---|---|---|
| 1 | `estado inicial tiene isLoading en true` | Sin suscriptores aún | `stats.value.isLoading = true` |
| 2 | `stats combina correctamente los conteos de incidencias` | total=20, open=8, inProgress=5, resolved=7 | Todos los campos mapeados correctamente, `isLoading = false` |
| 3 | `stats con todos los valores en cero es consistente` | Todos los conteos en 0 | Todos los campos = 0 |
| 4 | `stats incluye las incidencias recientes activas` | 2 incidencias recientes | `recentIncidents.size = 2`, IDs correctos |
| 5 | `stats se actualiza cuando el flow emite un nuevo valor` | `totalFlow` cambia de 5 a 12 | `totalIncidents` se actualiza reactivamente |

**Verificación de reactividad (prueba 5):**
```kotlin
val totalFlow = MutableStateFlow(5)
// ... configurar mocks
assertEquals(5, viewModel.stats.value.totalIncidents)

totalFlow.value = 12      // simular nueva emisión
advanceUntilIdle()

assertEquals(12, viewModel.stats.value.totalIncidents)
```

---

## Módulo Reportes — ReportsViewModel

**Archivo:** `app/src/test/java/com/example/swo/ui/ReportsViewModelTest.kt`  
**Dependencias mockeadas:** `IncidentDao`, `CategoryDao`  
**Total:** 8 pruebas

### Pruebas

| # | Nombre del test | Escenario | Resultado esperado |
|---|---|---|---|
| 1 | `estado inicial tiene isLoading en true` | Sin suscriptores | `stats.value.isLoading = true` |
| 2 | `stats combina correctamente los conteos por estado` | total=50, open=20, inProgress=10, resolved=15, cancelled=5 | Todos los campos mapeados, `isLoading = false` |
| 3 | `tasa de resolución se calcula correctamente` | 10 resueltas de 40 totales | `resolutionRate = 25.0f` (tolerancia 0.01f) |
| 4 | `tasa de resolución es 0 cuando no hay incidencias` | total=0, resolved=0 | `resolutionRate = 0.0f` (sin división por cero) |
| 5 | `stats incluye conteos por prioridad` | critical=3, high=10, medium=12, low=5 | Campos de prioridad correctos |
| 6 | `stats incluye agrupación por proyecto` | 2 proyectos con conteos | `byProject.size = 2`, nombres y conteos correctos |
| 7 | `categoryStats se mapea desde las entidades de categoría` | 3 categorías con colores | `catStats.size = 3`, nombres y colores mapeados |
| 8 | `categoryStats es lista vacía cuando no hay categorías` | `emptyList()` | `categoryStats.value.isEmpty()` |

**Cálculo de tasa de resolución verificado:**
```
resolutionRate = (resolved.toFloat() / total.toFloat()) * 100f
                = (10f / 40f) * 100f = 25.0f
```

---

## Módulo Dominio — Comentario

**Archivo:** `app/src/test/java/com/example/swo/domain/model/ComentarioTest.kt`  
**Tipo:** Pruebas puras JVM (mapeos de datos)  
**Total:** 4 pruebas

### Pruebas

| # | Nombre del test | Verifica | Resultado esperado |
|---|---|---|---|
| 1 | `toEntity mapea todos los campos correctamente` | `Comentario.toEntity()` | Todos los campos idénticos en la entidad Room |
| 2 | `toDomain mapea todos los campos correctamente` | `ComentarioEntity.toDomain()` | Todos los campos idénticos en el modelo de dominio |
| 3 | `comentario privado se mapea correctamente` | `esPublico = false` en round-trip | Ambos (`entity.esPublico` y `vuelta.esPublico`) son `false` |
| 4 | `esPublico es true por defecto` | Valor por defecto del campo | `comentario.esPublico = true` si no se especifica |

**Objeto de prueba de referencia:**
```kotlin
Comentario(
    idComentario = "c-001",
    texto = "El error ocurre al iniciar sesión desde móvil.",
    fecha = 1_700_000_000L,
    idIncidencia = "inc-42",
    idUsuario = "usr-7",
    nombreUsuario = "Ana García",
    esPublico = true
)
```

---

## Módulo Dominio — Notificacion

**Archivo:** `app/src/test/java/com/example/swo/domain/model/NotificacionTest.kt`  
**Tipo:** Pruebas puras JVM (mapeos de datos + enum)  
**Total:** 7 pruebas

### Pruebas

| # | Nombre del test | Verifica | Resultado esperado |
|---|---|---|---|
| 1 | `toEntity mapea todos los campos correctamente` | `Notificacion.toEntity()` | Todos los campos incluyendo `tipo = "INCIDENCIA_ASIGNADA"` (String) |
| 2 | `toDomain mapea todos los campos correctamente` | `NotificacionEntity.toDomain()` | `tipo = TipoNotificacion.INCIDENCIA_ASIGNADA` (enum) |
| 3 | `toDomain con tipo inválido usa GENERAL como fallback` | Tipo `"TIPO_INEXISTENTE"` en BD | Cae en `TipoNotificacion.GENERAL` (no lanza excepción) |
| 4 | `notificación general sin idIncidencia se mapea correctamente` | `idIncidencia = null` | `entity.idIncidencia = null`, `vuelta.idIncidencia = null` |
| 5 | `notificación leída se mapea correctamente` | `leida = true` en round-trip | `entity.leida = true`, `vuelta.leida = true` |
| 6 | `leida es false por defecto` | Valor por defecto | `notif.leida = false` si no se especifica |
| 7 | `todos los TipoNotificacion tienen label no vacío` | Enum `TipoNotificacion` | Todos los valores del enum tienen `label.isNotBlank()` |

**Tipos de notificación verificados:**
```kotlin
TipoNotificacion.values().forEach { tipo ->
    assertTrue("TipoNotificacion.${tipo.name} debe tener label no vacío", tipo.label.isNotBlank())
}
```

| Tipo | Label |
|---|---|
| `INCIDENCIA_ASIGNADA` | `"Incidencia asignada"` |
| `ESTADO_ACTUALIZADO` | `"Estado actualizado"` |
| `COMENTARIO_NUEVO` | `"Nuevo comentario"` |
| `GENERAL` | `"General"` |

---

## Cómo ejecutar las pruebas

```bash
# Todas las pruebas unitarias
./gradlew :app:test

# Solo las pruebas de la variante debug (con reporte HTML)
./gradlew :app:testDebugUnitTest

# Una clase específica
./gradlew :app:testDebugUnitTest --tests "com.example.swo.core.utils.FormValidatorTest"

# Un test específico
./gradlew :app:testDebugUnitTest --tests "com.example.swo.ui.LoginViewModelTest.login exitoso establece el usuario en el estado"
```

**Reporte HTML generado en:**
```
app/build/reports/tests/testDebugUnitTest/index.html
```

---

## Resumen de cobertura

| Clase de prueba | Módulo | Tipo | Pruebas |
|---|---|---|---|
| `FormValidatorTest` | Core / Validaciones | Pura JVM | 19 |
| `LoginViewModelTest` | Autenticación | ViewModel + Mock | 7 |
| `IncidentsViewModelTest` | Incidencias | ViewModel + UseCase + Mock | 5 |
| `DashboardViewModelTest` | Dashboard | ViewModel + DAO Mock | 5 |
| `ReportsViewModelTest` | Reportes | ViewModel + DAO Mock | 8 |
| `ComentarioTest` | Dominio / Comentarios | Mapeo de datos | 4 |
| `NotificacionTest` | Dominio / Notificaciones | Mapeo + Enum | 7 |
| **Total** | | | **55** |

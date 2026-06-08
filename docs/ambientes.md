# Ambientes de Desarrollo y Pruebas — SWO Mobile

Guía completa para configurar y ejecutar el proyecto en entorno local (desarrollo) y entorno de pruebas.

---

## Índice

1. [Ambiente de desarrollo](#ambiente-de-desarrollo)
2. [Ambiente de pruebas unitarias](#ambiente-de-pruebas-unitarias)
3. [Ambiente de pruebas en emulador](#ambiente-de-pruebas-en-emulador)
4. [Ambiente de pruebas en dispositivo físico](#ambiente-de-pruebas-en-dispositivo-físico)
5. [Variables de entorno por build variant](#variables-de-entorno-por-build-variant)
6. [Usuarios y datos de prueba](#usuarios-y-datos-de-prueba)
7. [Configuración del backend local](#configuración-del-backend-local)
8. [Solución de problemas comunes](#solución-de-problemas-comunes)

---

## Ambiente de desarrollo

### Requisitos del sistema

| Herramienta | Versión mínima | Versión recomendada | Notas |
|---|---|---|---|
| Android Studio | Hedgehog (2023.1.1) | Ladybug (2024.2.1) | Con soporte a Compose y KSP |
| JDK | 17 | 17 | Incluido en Android Studio |
| Android SDK | API 24 | API 35 | SDK 35 requerido para compilar |
| Gradle | 8.x | 9.2.1 | Gestionado por el Gradle wrapper |
| Git | 2.x | 2.44+ | Para control de versiones |
| Kotlin | 2.2.10 | 2.2.10 | Definido en `libs.versions.toml` |

### Configuración inicial paso a paso

#### 1. Clonar el repositorio

```bash
git clone https://github.com/JuanPGE123/SWO-MOVIL.git
cd SWO-MOVIL
```

#### 2. Abrir en Android Studio

```
File → Open → seleccionar carpeta SWO-MOVIL
```

Android Studio detectará automáticamente el proyecto Gradle y empezará a descargar dependencias.

#### 3. Instalar SDK faltantes (si aplica)

Si Android Studio muestra el error `SDK not found`:

```
Tools → SDK Manager → SDK Platforms → instalar API 35
Tools → SDK Manager → SDK Tools → verificar Build Tools 35.x
```

#### 4. Sincronizar Gradle

```
File → Sync Project with Gradle Files
```

O desde terminal:
```bash
./gradlew build
```

#### 5. Crear el emulador (si no existe)

```
Tools → Device Manager → Create Device
    Hardware: Pixel 6
    System Image: API 35 (Android 15) — descargar si no está disponible
    AVD Name: Pixel_6_API35
```

#### 6. Ejecutar la aplicación

```bash
# Compilar e instalar en emulador (variante debug)
./gradlew :app:installDebug

# O usar el botón Run ▶ en Android Studio
```

### Estructura de directorios del proyecto

```
SWO-MOVIL/
├── app/
│   ├── src/
│   │   ├── main/java/com/example/swo/     # Código fuente principal
│   │   ├── test/java/com/example/swo/     # Pruebas unitarias (JVM)
│   │   └── androidTest/                   # Pruebas instrumentadas (emulador)
│   └── build.gradle.kts                   # Configuración de módulo
├── gradle/
│   └── libs.versions.toml                 # Catálogo de versiones
├── docs/                                  # Documentación técnica
├── README.md
└── build.gradle.kts                       # Configuración raíz
```

---

## Ambiente de pruebas unitarias

Las pruebas unitarias son de tipo JVM: no requieren emulador, dispositivo, ni conexión a red.

### Requisitos

- JDK 17 instalado (viene con Android Studio)
- Ningún otro requisito — las dependencias de test se descargan automáticamente

### Herramientas utilizadas

| Herramienta | Versión | Uso |
|---|---|---|
| JUnit 4 | 4.13.2 | Framework base |
| MockK | 1.13.12 | Mocks en Kotlin |
| kotlinx-coroutines-test | 1.9.0 | Testing de ViewModels con coroutines |
| Turbine | 1.2.0 | Testing de StateFlow y Flow |

### Cómo ejecutar

```bash
# Todas las pruebas unitarias
./gradlew :app:test

# Pruebas de la variante debug (genera reporte HTML)
./gradlew :app:testDebugUnitTest

# Pruebas de la variante release
./gradlew :app:testReleaseUnitTest

# Una clase específica
./gradlew :app:testDebugUnitTest --tests "com.example.swo.core.utils.FormValidatorTest"

# Un método específico
./gradlew :app:testDebugUnitTest --tests "com.example.swo.ui.LoginViewModelTest.login exitoso establece el usuario en el estado"
```

### Ver el reporte de resultados

Después de ejecutar, abrir en el navegador:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

El reporte muestra:
- Pruebas pasadas / fallidas / ignoradas por clase
- Tiempo de ejecución
- Stack trace de las pruebas fallidas

### Desde Android Studio

```
View → Tool Windows → Gradle → :app → Tasks → verification → testDebugUnitTest
```

O haciendo clic derecho en cualquier archivo de test → `Run ...Test`.

---

## Ambiente de pruebas en emulador

Para pruebas manuales o instrumentadas usando el emulador de Android Studio.

### Emulador recomendado

| Parámetro | Valor recomendado |
|---|---|
| Dispositivo | Pixel 6 |
| API Level | 35 (Android 15) |
| RAM | 2048 MB mínimo |
| Storage | 6 GB |
| CPU/ABI | x86_64 |

### Instalar y arrancar

```bash
# Listar emuladores disponibles
emulator -list-avds

# Arrancar un emulador específico (desde SDK tools)
emulator -avd Pixel_6_API35

# Instalar la app en el emulador activo
./gradlew :app:installDebug
```

### URL del backend desde el emulador

El emulador de Android mapea `10.0.2.2` al `localhost` del PC host:

```
Emulador → 10.0.2.2:8080 → PC host → localhost:8080 (backend Spring Boot)
```

La variante `debug` ya tiene esta URL configurada:
```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/v1/\"")
```

### Modo offline (sin backend)

La app funciona en modo offline gracias al `DatabaseSeeder`. Al primer arranque carga:
- 5 usuarios de prueba
- 6 categorías
- 4 proyectos
- 12 incidencias

No es necesario tener el backend activo para explorar la funcionalidad.

---

## Ambiente de pruebas en dispositivo físico

### Activar modo desarrollador en el dispositivo

```
Ajustes → Acerca del teléfono → tocar "Número de compilación" 7 veces
Ajustes → Opciones para desarrolladores → activar "Depuración USB"
```

### Conectar y verificar

```bash
# Verificar que el dispositivo es reconocido
adb devices

# Resultado esperado:
# List of devices attached
# XXXXXXXXXXX    device
```

### Instalar la app

```bash
./gradlew :app:installDebug
```

### URL del backend desde dispositivo físico

`10.0.2.2` **no funciona** en dispositivos físicos. Reemplazar con la IP del PC en la red local.

**Paso 1:** Obtener la IP del PC
```powershell
# Windows
ipconfig
# Buscar "Dirección IPv4" — ejemplo: 192.168.1.105
```

**Paso 2:** Actualizar `app/build.gradle.kts`
```kotlin
debug {
    buildConfigField("String", "BASE_URL", "\"http://192.168.1.105:8080/api/v1/\"")
}
```

**Paso 3:** Asegurarse de que el PC y el dispositivo estén en la misma red WiFi.

**Paso 4:** Si el backend usa HTTP (no HTTPS), agregar en `res/xml/network_security_config.xml`:
```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="false">192.168.1.105</domain>
</domain-config>
```

---

## Variables de entorno por build variant

### Tabla de variables configuradas

| Variable | Variante debug | Variante release |
|---|---|---|
| `BuildConfig.BASE_URL` | `http://10.0.2.2:8080/api/v1/` | `https://api.swo-servicedesk.com/v1/` |
| `BuildConfig.APPLICATION_ID` | `com.example.swo.debug` | `com.example.swo` |
| `BuildConfig.VERSION_NAME` | `1.0-debug` | `1.0` |
| `BuildConfig.DEBUG` | `true` | `false` |

### Cómo agregar nuevas variables

```kotlin
// En app/build.gradle.kts
buildTypes {
    debug {
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/v1/\"")
        buildConfigField("Boolean", "ENABLE_LOGS", "true")    // nueva variable
    }
    release {
        buildConfigField("String", "BASE_URL", "\"https://api.swo-servicedesk.com/v1/\"")
        buildConfigField("Boolean", "ENABLE_LOGS", "false")   // nueva variable
    }
}
```

Acceder en Kotlin:
```kotlin
if (BuildConfig.ENABLE_LOGS) {
    Log.d("SWO", "Mensaje de debug")
}
```

---

## Usuarios y datos de prueba

Estos usuarios están precargados por el `DatabaseSeeder` y permiten probar todos los roles sin backend.

| Correo | Contraseña | Rol | Permisos |
|---|---|---|---|
| `admin@swo.com` | `admin123` | Administrador | Acceso total: usuarios, proyectos, incidencias, reportes |
| `mfernanda@swo.com` | `tech123` | Técnico | Gestionar incidencias asignadas, ver reportes |
| `carlos@swo.com` | `client123` | Cliente | Crear incidencias, ver sus propias incidencias |

### Flujos de prueba recomendados

**Como Administrador:**
1. Login con `admin@swo.com` / `admin123`
2. Verificar Dashboard con estadísticas
3. Ir a Usuarios → crear nuevo usuario
4. Ir a Proyectos → crear nuevo proyecto
5. Ver Reportes → verificar gráficos

**Como Técnico:**
1. Login con `mfernanda@swo.com` / `tech123`
2. Ver incidencias asignadas
3. Cambiar estado de una incidencia (OPEN → IN_PROGRESS → RESOLVED)
4. Agregar comentario a una incidencia

**Como Cliente:**
1. Login con `carlos@swo.com` / `client123`
2. Crear nueva incidencia
3. Ver historial de incidencias propias

---

## Configuración del backend local

Para probar con el servidor backend Spring Boot corriendo localmente.

### Requisitos del backend

| Herramienta | Versión |
|---|---|
| Java | 17 |
| Maven | 3.8+ (o usar el wrapper `mvnw`) |
| PostgreSQL / H2 | Según configuración del backend |

### Levantar el backend

```bash
# En el repositorio del backend (Spring Boot)
cd ruta/al/backend

# Con Maven wrapper
./mvnw spring-boot:run

# El servidor queda disponible en:
# http://localhost:8080
# http://localhost:8080/api/v1/
```

### Verificar que el backend responde

```bash
# Desde el PC host
curl http://localhost:8080/api/v1/incidents

# Desde el emulador Android (equivalente a localhost del PC)
# La app usa: http://10.0.2.2:8080/api/v1/incidents
```

### Swagger / API docs (si el backend lo provee)

```
http://localhost:8080/swagger-ui.html
http://localhost:8080/api-docs
```

---

## Solución de problemas comunes

### `CLEARTEXT communication to ... not permitted`

HTTP está bloqueado en Android 9+ por defecto.

**Solución:** Verificar `res/xml/network_security_config.xml` y que el `AndroidManifest.xml` lo referencie:
```xml
<application android:networkSecurityConfig="@xml/network_security_config">
```

### `Connection refused` en emulador

El emulador no puede conectar al backend.

**Verificar:**
1. El backend está corriendo: `curl http://localhost:8080/api/v1/`
2. La URL en `build.gradle.kts` usa `10.0.2.2` (no `localhost` ni `127.0.0.1`)
3. Firewall de Windows no bloquea el puerto 8080

**Permitir puerto 8080 en Windows Firewall:**
```powershell
netsh advfirewall firewall add rule name="SWO Backend 8080" dir=in action=allow protocol=TCP localport=8080
```

### `Room database migration required`

La versión de la BD en el código no coincide con la instalada.

**Solución en desarrollo:**
```bash
# Desinstalar la app (borra la BD)
adb uninstall com.example.swo.debug

# Reinstalar
./gradlew :app:installDebug
```

### Las pruebas unitarias fallan con `Method ... not mocked`

Ocurre cuando las pruebas intentan usar APIs de Android en JVM.

**Solución:** Verificar que el ViewModel solo use lógica pura o dependencias mockeadas con MockK. No llamar a `Log.d()`, `Context`, etc. directamente en ViewModels.

### `KSP / Hilt` error al compilar

```bash
# Limpiar caché de Gradle y recompilar
./gradlew clean build
```

O desde Android Studio: `Build → Clean Project` → `Build → Rebuild Project`.

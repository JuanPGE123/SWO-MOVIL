package com.example.swo.core.utils

object FormValidator {

    private val EMAIL_REGEX =
        Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

    fun validateName(name: String): String? = when {
        name.isBlank()          -> "El nombre es obligatorio"
        name.trim().length < 3  -> "Mínimo 3 caracteres"
        name.trim().length > 100 -> "Máximo 100 caracteres"
        else                    -> null
    }

    fun validateEmail(email: String): String? = when {
        email.isBlank()                          -> "El correo es obligatorio"
        email.length > 150                       -> "Correo demasiado largo"
        !EMAIL_REGEX.matches(email.trim())       -> "Formato de correo inválido (ej: usuario@empresa.com)"
        else                                     -> null
    }

    fun validatePassword(password: String, isEditing: Boolean = false): String? {
        if (isEditing && password.isBlank()) return null   // opcional al editar
        return when {
            password.isBlank()  -> "La contraseña es obligatoria"
            password.length < 6 -> "Mínimo 6 caracteres"
            password.length > 50 -> "Máximo 50 caracteres"
            else                -> null
        }
    }

    fun validateProjectName(name: String): String? = when {
        name.isBlank()          -> "El nombre del proyecto es obligatorio"
        name.trim().length < 3  -> "Mínimo 3 caracteres"
        name.trim().length > 80 -> "Máximo 80 caracteres"
        else                    -> null
    }

    fun validateDescription(desc: String): String? = when {
        desc.length > 500 -> "Máximo 500 caracteres (actual: ${desc.length})"
        else              -> null
    }

    fun passwordStrength(password: String): PasswordStrength {
        if (password.length < 6) return PasswordStrength.WEAK
        var score = 0
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        return when {
            score <= 1 -> PasswordStrength.WEAK
            score <= 2 -> PasswordStrength.MEDIUM
            else       -> PasswordStrength.STRONG
        }
    }

    enum class PasswordStrength(val label: String, val colorHex: Long, val fraction: Float) {
        WEAK("Débil", 0xFFEF4444L, 0.33f),
        MEDIUM("Media", 0xFFF59E0BL, 0.66f),
        STRONG("Fuerte", 0xFF10B981L, 1.0f)
    }
}

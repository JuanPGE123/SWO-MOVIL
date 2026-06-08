package com.example.swo.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias para FormValidator.
 * No requieren mocks ni coroutines — lógica pura JVM.
 */
class FormValidatorTest {

    // ── validateName ─────────────────────────────────────────────────────────

    @Test
    fun `validateName con nombre vacío retorna error`() {
        val resultado = FormValidator.validateName("")
        assertNotNull(resultado)
        assertEquals("El nombre es obligatorio", resultado)
    }

    @Test
    fun `validateName con nombre en blanco retorna error`() {
        val resultado = FormValidator.validateName("   ")
        assertNotNull(resultado)
        assertEquals("El nombre es obligatorio", resultado)
    }

    @Test
    fun `validateName con menos de 3 caracteres retorna error`() {
        val resultado = FormValidator.validateName("Ab")
        assertNotNull(resultado)
        assertEquals("Mínimo 3 caracteres", resultado)
    }

    @Test
    fun `validateName con más de 100 caracteres retorna error`() {
        val nombre = "A".repeat(101)
        val resultado = FormValidator.validateName(nombre)
        assertNotNull(resultado)
        assertEquals("Máximo 100 caracteres", resultado)
    }

    @Test
    fun `validateName con nombre válido retorna null`() {
        assertNull(FormValidator.validateName("Juan Pablo"))
        assertNull(FormValidator.validateName("Ana"))
        assertNull(FormValidator.validateName("A".repeat(100)))
    }

    // ── validateEmail ────────────────────────────────────────────────────────

    @Test
    fun `validateEmail con correo vacío retorna error`() {
        val resultado = FormValidator.validateEmail("")
        assertEquals("El correo es obligatorio", resultado)
    }

    @Test
    fun `validateEmail con correo muy largo retorna error`() {
        val correo = "a".repeat(151)
        val resultado = FormValidator.validateEmail(correo)
        assertEquals("Correo demasiado largo", resultado)
    }

    @Test
    fun `validateEmail con formato inválido retorna error`() {
        assertEquals("Formato de correo inválido (ej: usuario@empresa.com)",
            FormValidator.validateEmail("no-es-un-correo"))
        assertEquals("Formato de correo inválido (ej: usuario@empresa.com)",
            FormValidator.validateEmail("usuario@"))
        assertEquals("Formato de correo inválido (ej: usuario@empresa.com)",
            FormValidator.validateEmail("@dominio.com"))
    }

    @Test
    fun `validateEmail con correo válido retorna null`() {
        assertNull(FormValidator.validateEmail("juan@sena.edu.co"))
        assertNull(FormValidator.validateEmail("usuario+tag@empresa.com"))
        assertNull(FormValidator.validateEmail("  juan@sena.edu.co  ")) // trim interno
    }

    // ── validatePassword ─────────────────────────────────────────────────────

    @Test
    fun `validatePassword con contraseña vacía y no editando retorna error`() {
        val resultado = FormValidator.validatePassword("", isEditing = false)
        assertEquals("La contraseña es obligatoria", resultado)
    }

    @Test
    fun `validatePassword con contraseña vacía en modo edición retorna null`() {
        assertNull(FormValidator.validatePassword("", isEditing = true))
    }

    @Test
    fun `validatePassword con menos de 6 caracteres retorna error`() {
        val resultado = FormValidator.validatePassword("abc")
        assertEquals("Mínimo 6 caracteres", resultado)
    }

    @Test
    fun `validatePassword con más de 50 caracteres retorna error`() {
        val resultado = FormValidator.validatePassword("A".repeat(51))
        assertEquals("Máximo 50 caracteres", resultado)
    }

    @Test
    fun `validatePassword con contraseña válida retorna null`() {
        assertNull(FormValidator.validatePassword("segura123"))
        assertNull(FormValidator.validatePassword("A".repeat(50)))
    }

    // ── validateProjectName ──────────────────────────────────────────────────

    @Test
    fun `validateProjectName con nombre vacío retorna error`() {
        assertEquals("El nombre del proyecto es obligatorio",
            FormValidator.validateProjectName(""))
    }

    @Test
    fun `validateProjectName con menos de 3 caracteres retorna error`() {
        assertEquals("Mínimo 3 caracteres", FormValidator.validateProjectName("AB"))
    }

    @Test
    fun `validateProjectName con más de 80 caracteres retorna error`() {
        assertEquals("Máximo 80 caracteres",
            FormValidator.validateProjectName("P".repeat(81)))
    }

    @Test
    fun `validateProjectName con nombre válido retorna null`() {
        assertNull(FormValidator.validateProjectName("SWO Proyecto"))
        assertNull(FormValidator.validateProjectName("P".repeat(80)))
    }

    // ── validateDescription ──────────────────────────────────────────────────

    @Test
    fun `validateDescription con descripción mayor a 500 caracteres retorna error`() {
        val desc = "x".repeat(501)
        val resultado = FormValidator.validateDescription(desc)
        assertNotNull(resultado)
        assert(resultado!!.startsWith("Máximo 500 caracteres"))
    }

    @Test
    fun `validateDescription con descripción válida retorna null`() {
        assertNull(FormValidator.validateDescription(""))
        assertNull(FormValidator.validateDescription("Descripción normal"))
        assertNull(FormValidator.validateDescription("x".repeat(500)))
    }

    // ── passwordStrength ─────────────────────────────────────────────────────

    @Test
    fun `passwordStrength con menos de 6 caracteres retorna WEAK`() {
        assertEquals(FormValidator.PasswordStrength.WEAK, FormValidator.passwordStrength("abc"))
        assertEquals(FormValidator.PasswordStrength.WEAK, FormValidator.passwordStrength(""))
    }

    @Test
    fun `passwordStrength con solo letras minúsculas retorna WEAK`() {
        assertEquals(FormValidator.PasswordStrength.WEAK, FormValidator.passwordStrength("abcdef"))
    }

    @Test
    fun `passwordStrength con longitud adecuada y una condición extra retorna MEDIUM`() {
        // largo >= 8 + dígito = score 2
        assertEquals(FormValidator.PasswordStrength.MEDIUM,
            FormValidator.passwordStrength("abcdefg1"))
    }

    @Test
    fun `passwordStrength con múltiples condiciones retorna STRONG`() {
        // largo >= 8 + mayúscula + dígito + especial = score 4
        assertEquals(FormValidator.PasswordStrength.STRONG,
            FormValidator.passwordStrength("Abcdef1!"))
    }
}

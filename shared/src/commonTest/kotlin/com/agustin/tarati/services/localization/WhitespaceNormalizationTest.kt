package com.agustin.tarati.services.localization

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests para verificar la normalización de espacios y procesamiento de escapes XML
 * en strings localizados.
 *
 * ## Contexto
 * 1. El formateo automático del IDE introduce saltos de línea en los XML,
 *    causando múltiples espacios en el texto leído.
 * 2. Compose Multiplatform Resources no procesa automáticamente los escapes XML
 *    comunes (\', \", \\), mostrándolos literalmente.
 *
 * La función normalizeWhitespace() soluciona ambos problemas.
 */
class WhitespaceNormalizationTest {
    // ═══════════════════════════════════════════════════════════════════════════
    //  Tests de Normalización de Espacios
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `normalizeWhitespace colapsa espacios múltiples`() {
        val input = "This is a long     text with    multiple   spaces"
        val expected = "This is a long text with multiple spaces"
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace maneja saltos de línea del formateo XML`() {
        // Simula cómo Android lee un string XML formateado
        val input = "Tarati is a strategic board game\n        created by George Spencer Brown"
        val expected = "Tarati is a strategic board game created by George Spencer Brown"
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace preserva saltos de línea explícitos con backslash-n`() {
        val input = "Line 1\\nLine 2\\nLine 3"
        val expected = "Line 1\nLine 2\nLine 3"
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace maneja mezcla de espacios y backslash-n`() {
        val input = "• Players: 2 (WHITE vs BLACK)\\n • Objective:\n        Control the board"
        val expected = "• Players: 2 (WHITE vs BLACK)\n• Objective: Control the board"
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace elimina espacios al inicio y final`() {
        val input = "   Text with leading and trailing spaces   "
        val expected = "Text with leading and trailing spaces"
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace maneja tabs y otros espacios en blanco`() {
        val input = "Text\twith\ttabs\tand   spaces"
        val expected = "Text with tabs and spaces"
        assertEquals(expected, input.normalizeWhitespace())
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Tests de Escapes XML
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `normalizeWhitespace convierte comillas simples escapadas`() {
        val input = "based on his work \\'Laws of Form\\'"
        val expected = "based on his work 'Laws of Form'"
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace convierte comillas dobles escapadas`() {
        val input = "He said \\\"Hello World\\\""
        val expected = "He said \"Hello World\""
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace convierte backslashes escapados`() {
        val input = "Path: C:\\\\Users\\\\Documents"
        val expected = "Path: C:\\Users\\Documents"
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace maneja múltiples tipos de escapes`() {
        val input = "Text with \\'single\\' and \\\"double\\\" quotes"
        val expected = "Text with 'single' and \"double\" quotes"
        assertEquals(expected, input.normalizeWhitespace())
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Tests de Casos Reales del Proyecto
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    fun `normalizeWhitespace maneja caso real del proyecto con comillas escapadas`() {
        // Caso real del archivo strings.xml (líneas 8-11)
        val input = """Tarati is a strategic board game
        created by George Spencer Brown, based on his work \'Laws of Form\'. The game combines elements of traditional
        board games with unique movement and capture mechanics."""

        val expected =
            "Tarati is a strategic board game created by George Spencer Brown, based on his work 'Laws of Form'. The game combines elements of traditional board games with unique movement and capture mechanics."

        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace maneja reglas del juego con viñetas`() {
        // Caso real del archivo strings.xml (líneas 13-17)
        val input = """• Players: 2 (WHITE vs BLACK)\n • Objective:
        Control the board by converting opponent pieces\n • Movement: Pieces move along edges to adjacent vertices\n •
        Upgrades: Pieces become upgraded in opponent's home base\n • Capture: Moving adjacent to opponent pieces
        converts them"""

        val expected =
            "• Players: 2 (WHITE vs BLACK)\n• Objective: Control the board by converting opponent pieces\n• Movement: Pieces move along edges to adjacent vertices\n• Upgrades: Pieces become upgraded in opponent's home base\n• Capture: Moving adjacent to opponent pieces converts them"

        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace maneja caso con escapadas y apostrofes`() {
        // Caso con comilla simple en posesivos (opponent's) y escapadas
        val input = "Moving to opponent\\'s base in the enemy\\'s territory"
        val expected = "Moving to opponent's base in the enemy's territory"
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace convierte comilla doble escapada`() {
        val input = "He said \\\"Hello\\\""
        val expected = "He said \"Hello\""
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace convierte comillas dobles escapadas simple`() {
        val input = "\\\"test\\\""
        val expected = "\"test\""
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace maneja texto complejo con todos los problemas`() {
        // Combina: formateo XML + escapes + \n explícitos
        val input = """George Spencer Brown\\'s game \\"Tarati\\":\n • Based on \\'Laws of Form\\'
        • Strategic gameplay\n • Multiple    spaces   and
        formatting    issues"""

        val expected =
            "George Spencer Brown's game \"Tarati\":\n• Based on 'Laws of Form' • Strategic gameplay\n• Multiple spaces and formatting issues"

        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace maneja formato con parametros y doble salto de linea`() {
        // Caso específico del proyecto: perform_the_indicated_move
        val input = "%1\$s\\n\\nMake the indicated move."
        val expected = "%1\$s\n\nMake the indicated move."
        assertEquals(expected, input.normalizeWhitespace())
    }

    @Test
    fun `normalizeWhitespace preserva saltos de linea multiples consecutivos`() {
        val input = "Line 1\\n\\n\\nLine 2"
        val expected = "Line 1\n\n\nLine 2"
        assertEquals(expected, input.normalizeWhitespace())
    }
}
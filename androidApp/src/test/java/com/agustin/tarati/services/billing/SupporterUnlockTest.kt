package com.agustin.tarati.services.billing

import com.agustin.tarati.network.models.SUPPORTER_PRODUCT_ID
import com.agustin.tarati.ui.theme.GildedPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests de la regla de desbloqueo supporter (C4) — [effectiveOwnedProducts] y
 * [lockedPaletteNames]. Funciones puras: sin red, sin dispatcher, sin mocks.
 */
class SupporterUnlockTest {

    @Test
    fun `supporter unlocks every premium product`() {
        val effective = effectiveOwnedProducts(setOf(SUPPORTER_PRODUCT_ID))

        assertTrue(ALL_PREMIUM_PRODUCT_IDS.all { it in effective })
        assertTrue(PaletteProducts.GILDED in effective)
        assertTrue(PieceProducts.HEXAGON in effective)
    }

    @Test
    fun `without supporter ownership is unchanged`() {
        assertEquals(emptySet<String>(), effectiveOwnedProducts(emptySet()))
        assertEquals(setOf(PieceProducts.SQUARE), effectiveOwnedProducts(setOf(PieceProducts.SQUARE)))
    }

    @Test
    fun `specific ownership unlocks only that product`() {
        val effective = effectiveOwnedProducts(setOf(PaletteProducts.GILDED))

        assertTrue(PaletteProducts.GILDED in effective)
        assertFalse(PieceProducts.HEXAGON in effective)
    }

    @Test
    fun `gilded is locked for a non-supporter without the purchase`() {
        assertEquals(setOf(GildedPalette.name), lockedPaletteNames(emptySet()))
    }

    @Test
    fun `gilded is unlocked for a supporter`() {
        assertTrue(lockedPaletteNames(setOf(SUPPORTER_PRODUCT_ID)).isEmpty())
    }

    @Test
    fun `gilded is unlocked when specifically owned`() {
        assertTrue(lockedPaletteNames(setOf(PaletteProducts.GILDED)).isEmpty())
    }
}

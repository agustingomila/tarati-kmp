@file:OptIn(ExperimentalCoroutinesApi::class)

package com.agustin.tarati.services.billing

import com.agustin.tarati.features.online.auth.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests de [EntitlementsRepositoryImpl] — ciclo de vida de ownership cross-platform.
 *
 * Mockea [EntitlementSyncService] (HTTP) y [AuthRepository] (token) con MockK.
 */
class EntitlementsRepositoryTest {

    private val syncService: EntitlementSyncService = mockk()
    private val authRepository: AuthRepository = mockk(relaxed = true)

    private fun repo() = EntitlementsRepositoryImpl(syncService, authRepository)

    @Test
    fun `refresh loads entitlements when authenticated`(): TestResult = runTest {
        every { authRepository.getToken() } returns "tok"
        coEvery { syncService.getActive("tok") } returns Result.success(listOf("supporter", "palette_gilded"))

        val repository = repo()
        repository.refresh()

        assertEquals(setOf("supporter", "palette_gilded"), repository.entitlements.value)
    }

    @Test
    fun `refresh clears entitlements when no token`(): TestResult = runTest {
        every { authRepository.getToken() } returns null

        val repository = repo()
        repository.refresh()

        assertTrue(repository.entitlements.value.isEmpty())
    }

    @Test
    fun `refresh keeps previous value when the server call fails`(): TestResult = runTest {
        every { authRepository.getToken() } returns "tok"
        coEvery { syncService.getActive("tok") } returns Result.success(listOf("supporter"))
        val repository = repo()
        repository.refresh()

        coEvery { syncService.getActive("tok") } returns Result.failure(RuntimeException("net"))
        repository.refresh()

        assertEquals(setOf("supporter"), repository.entitlements.value)
    }

    @Test
    fun `clear empties the local state`(): TestResult = runTest {
        every { authRepository.getToken() } returns "tok"
        coEvery { syncService.getActive("tok") } returns Result.success(listOf("supporter"))
        val repository = repo()
        repository.refresh()

        repository.clear()

        assertTrue(repository.entitlements.value.isEmpty())
    }

    @Test
    fun `validateGooglePlay grants and refreshes on success`(): TestResult = runTest {
        every { authRepository.getToken() } returns "tok"
        coEvery { syncService.validateGooglePlay("tok", "supporter", "purchase-1") } returns true
        coEvery { syncService.getActive("tok") } returns Result.success(listOf("supporter"))

        val repository = repo()
        val granted = repository.validateGooglePlay("supporter", "purchase-1")

        assertTrue(granted)
        assertEquals(setOf("supporter"), repository.entitlements.value)
        coVerify { syncService.getActive("tok") }
    }

    @Test
    fun `validateGooglePlay returns false without a token`(): TestResult = runTest {
        every { authRepository.getToken() } returns null

        val repository = repo()
        val granted = repository.validateGooglePlay("supporter", "purchase-1")

        assertFalse(granted)
    }

    @Test
    fun `startStripeCheckout returns the url from the server`(): TestResult = runTest {
        every { authRepository.getToken() } returns "tok"
        coEvery { syncService.createStripeCheckout("tok", 500, "once") } returns Result.success("https://checkout/x")

        val url = repo().startStripeCheckout(500, SupporterInterval.ONCE)

        assertEquals("https://checkout/x", url)
    }

    @Test
    fun `startStripeCheckout returns null without a token`(): TestResult = runTest {
        every { authRepository.getToken() } returns null

        val url = repo().startStripeCheckout(500, SupporterInterval.ONCE)

        assertEquals(null, url)
    }

    @Test
    fun `isUnlocked respects supporter and specific ownership`() {
        assertTrue(isUnlocked("palette_gilded", setOf("supporter")))
        assertTrue(isUnlocked("palette_gilded", setOf("palette_gilded")))
        assertFalse(isUnlocked("palette_gilded", setOf("piece_crystal")))
    }
}
